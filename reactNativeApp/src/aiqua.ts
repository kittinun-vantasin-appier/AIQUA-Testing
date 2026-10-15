import { Platform } from 'react-native';
import RNAiqua from '@appier/react-native-sdk';
import { CURRENCY, type Product } from './catalog';
import { cartTotal, quantityOf, unitCount, type Cart, type Order } from './cart';

const APP_ID = '2fe580dde51346bd40f1'; // Constants.APP_ID in sharedLogic
const USER_ID = 'kittinun.vantasin@appier.com'; // the same user as the native apps, so they share one AIQUA profile

// The SDK silently drops events logged before configure() resolves, so every event waits for it.
let configured: Promise<unknown> = Promise.resolve();

/** Starts the SDK once at launch. iOS rejects a missing appGroup although it's typed optional; we have none. */
export function startAiqua() {
  configured = RNAiqua.configure({ appId: APP_ID, appGroup: '' }).then(() => {
    RNAiqua.setUserId(USER_ID);
    RNAiqua.setCustomKey('platform', `${Platform.OS === 'ios' ? 'iOS' : 'Android'} ${Platform.Version}`);
  });
}

/** Where ViewModel hooks send events, mirroring the SDK. The app uses `aiquaLogger`; tests could pass a fake. */
export type EventLogger = (name: string, parameters: object, valueToSum?: number, currency?: string) => void;

/** Adds `host` to every event, so AIQUA can tell this app's events from the native apps' (ADR-0006). */
export const aiquaLogger: EventLogger = (name, parameters, valueToSum, currency) => {
  const params = { ...parameters, host: 'react_native' };
  configured.then(() =>
    valueToSum === undefined
      ? RNAiqua.logEvent(name, params)
      : RNAiqua.logEvent(name, params, valueToSum, currency ?? CURRENCY),
  );
};

// The events, with the same names and parameters as sharedLogic's EventLogger.kt.

export const screenViewed = (log: EventLogger, screenName: string, parameters: object = {}) =>
  log('screen_viewed', { ...parameters, screen_name: screenName });

export const cartAdded = (log: EventLogger, product: Product, cart: Cart) =>
  log('cart_added', {
    product_id: product.id,
    product_name: product.name,
    category: product.category,
    price: product.price,
    quantity: 1,
    product_quantity: quantityOf(cart, product.id),
    cart_unit_count: unitCount(cart),
  });

/** One `product_purchased` per Cart Line, then `checkout_completed`, which alone carries the Order total. */
export function orderPlaced(log: EventLogger, order: Order) {
  order.cart.lines.forEach(line =>
    log('product_purchased', {
      order_id: order.id,
      product_id: line.product.id,
      product_name: line.product.name,
      category: line.product.category,
      price: line.product.price,
      quantity: line.quantity,
    }),
  );
  log('checkout_completed', { order_id: order.id, product_count: unitCount(order.cart) }, cartTotal(order.cart), CURRENCY);
}

export const checkoutFailed = (log: EventLogger, error: unknown) =>
  log('checkout_failed', { reason: error instanceof Error ? error.message : String(error) });
