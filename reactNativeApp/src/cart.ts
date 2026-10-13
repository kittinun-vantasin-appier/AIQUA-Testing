import type { Product } from './catalog';
import { createStore, delay } from './store';

export const MAX_QUANTITY = 9;

/** One Product in the Cart plus how many units the Shopper wants, from 1 to MAX_QUANTITY. */
export type CartLine = { product: Product; quantity: number };

/** The Products the Shopper intends to buy, as Cart Lines in the order they were first added. */
export type Cart = { lines: CartLine[] };

export const cartTotal = (cart: Cart) => cart.lines.reduce((sum, l) => sum + l.product.price * l.quantity, 0);
export const unitCount = (cart: Cart) => cart.lines.reduce((sum, l) => sum + l.quantity, 0);
export const quantityOf = (cart: Cart, productId: string) =>
  cart.lines.find(l => l.product.id === productId)?.quantity ?? 0;

/** A confirmed purchase of everything that was in the Cart. `id` is the Order ID. */
export type Order = { id: string; cart: Cart };

/**
 * Service: pretends to call a backend. Waits, then succeeds with an Order ID like "ORD-482913", except that every
 * `failEvery`th attempt (5th, 10th, …) fails, so a failure can be reproduced on demand.
 */
export function createOrderService(latencyMs = 2000, failEvery = 5) {
  let attempts = 0;
  return {
    placeOrder: async (cart: Cart): Promise<Order> => {
      await delay(latencyMs);
      attempts++;
      if (attempts % failEvery === 0) throw new Error(`Simulated failure on attempt ${attempts}`);
      return { id: 'ORD-' + String(Math.floor(Math.random() * 1_000_000)).padStart(6, '0'), cart };
    },
  };
}

type OrderService = ReturnType<typeof createOrderService>;

/** Repository: owns the one in-memory Cart and its rules. */
export function createCartRepository(orderService: OrderService) {
  const cart = createStore<Cart>({ lines: [] });
  const withQuantity = (c: Cart, productId: string, quantity: number): Cart => ({
    lines: c.lines
      .map(l => (l.product.id === productId ? { ...l, quantity } : l))
      .filter(l => l.quantity > 0),
  });

  return {
    cart,
    add: (product: Product) =>
      cart.set(c => {
        const quantity = quantityOf(c, product.id);
        if (quantity === 0) return { lines: [...c.lines, { product, quantity: 1 }] };
        return quantity >= MAX_QUANTITY ? c : withQuantity(c, product.id, quantity + 1);
      }),
    // Taking away the last unit removes the Cart Line.
    decrement: (productId: string) => cart.set(c => withQuantity(c, productId, quantityOf(c, productId) - 1)),
    remove: (productId: string) => cart.set(c => withQuantity(c, productId, 0)),
    /** Places an Order, then takes exactly the ordered units out of the Cart. If it throws, the Cart is untouched. */
    checkout: async (): Promise<Order> => {
      const order = await orderService.placeOrder(cart.get());
      cart.set(c => ({
        lines: c.lines
          .map(l => ({ ...l, quantity: l.quantity - quantityOf(order.cart, l.product.id) }))
          .filter(l => l.quantity > 0),
      }));
      return order;
    },
  };
}
