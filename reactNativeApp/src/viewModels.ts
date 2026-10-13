// One hook per screen, like sharedLogic's ViewModels: it turns Repository state into a UiState of independent
// fields and is the only place events are sent from (ADR-0004).
import { useCallback, useEffect, useState } from 'react';
import { aiquaLogger, cartAdded, checkoutFailed, orderPlaced, screenViewed, type EventLogger } from './aiqua';
import { createCartRepository, createOrderService, cartTotal, quantityOf, unitCount, MAX_QUANTITY } from './cart';
import { createHomeRepository, formatPrice, type Product } from './catalog';
import { delay, useStore } from './store';

// Created once, like AppContainer.
const homeRepository = createHomeRepository();
const cartRepository = createCartRepository(createOrderService());
const log: EventLogger = aiquaLogger;

/** No badge at zero, and "99+" past 99. */
const badgeText = (count: number) => (count <= 0 ? null : count > 99 ? '99+' : String(count));

function addToCart(product: Product) {
  const previousCount = unitCount(cartRepository.cart.get());
  cartRepository.add(product);
  const cart = cartRepository.cart.get();
  if (unitCount(cart) > previousCount) cartAdded(log, product, cart);
}

export function useMainViewModel() {
  const cart = useStore(cartRepository.cart);
  return { cartBadgeText: badgeText(unitCount(cart)) };
}

export function useHomeViewModel() {
  const products = useStore(homeRepository.products);
  const cart = useStore(cartRepository.cart);
  const [load, setLoad] = useState({ isLoading: products === null, error: null as unknown });

  const refresh = useCallback(async () => {
    setLoad({ isLoading: true, error: null });
    try {
      await homeRepository.refresh();
      setLoad({ isLoading: false, error: null });
    } catch (error) {
      setLoad({ isLoading: false, error });
    }
  }, []);

  useEffect(() => {
    if (homeRepository.products.get() === null) refresh();
  }, [refresh]);

  // Categories and Products A→Z, headers uppercased, each Product with its quantity in the Cart.
  const categories = [...new Set((products ?? []).map(p => p.category))].sort();
  const sections = categories.map(category => ({
    title: category.toUpperCase(),
    data: (products ?? [])
      .filter(p => p.category === category)
      .sort((a, b) => a.name.localeCompare(b.name))
      .map(product => ({ product, priceText: formatPrice(product.price), quantity: quantityOf(cart, product.id) })),
  }));

  return {
    uiState: { ...load, sections },
    onScreenViewed: () => screenViewed(log, 'home'),
    onRetryClick: refresh,
    onAddClick: addToCart,
    onDecrementClick: (product: Product) => cartRepository.decrement(product.id),
  };
}

export function useCartViewModel() {
  const cart = useStore(cartRepository.cart);
  const [order, setOrder] = useState({ isPlacing: false, placedTotalText: null as string | null, error: null as unknown });
  const isOrderOverlayVisible = order.isPlacing || order.placedTotalText !== null || order.error !== null;

  const onBuyClick = async () => {
    if (cart.lines.length === 0 || isOrderOverlayVisible) return;
    setOrder({ isPlacing: true, placedTotalText: null, error: null });
    try {
      const placed = await cartRepository.checkout();
      orderPlaced(log, placed);
      setOrder({ isPlacing: false, placedTotalText: formatPrice(cartTotal(placed.cart)), error: null });
    } catch (error) {
      checkoutFailed(log, error);
      setOrder({ isPlacing: false, placedTotalText: null, error });
    }
    await delay(2000);
    setOrder({ isPlacing: false, placedTotalText: null, error: null });
  };

  return {
    uiState: {
      lines: cart.lines.map(line => ({
        product: line.product,
        quantity: line.quantity,
        canAdd: line.quantity < MAX_QUANTITY,
        unitPriceText: formatPrice(line.product.price),
        totalText: formatPrice(line.product.price * line.quantity),
      })),
      totalText: formatPrice(cartTotal(cart)),
      isPlacingOrder: order.isPlacing,
      placedOrderTotalText: order.placedTotalText,
      orderError: order.error,
      isOrderOverlayVisible,
    },
    onScreenViewed: () => screenViewed(log, 'cart', { cart_unit_count: unitCount(cartRepository.cart.get()) }),
    onAddClick: addToCart,
    onDecrementClick: (product: Product) => cartRepository.decrement(product.id),
    onRemoveClick: (product: Product) => cartRepository.remove(product.id),
    onBuyClick,
  };
}
