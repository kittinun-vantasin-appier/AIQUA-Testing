// The same product.json as the native apps, so Product IDs, Categories and Prices in events match (see metro.config.js).
import productJson from '../../sharedLogic/src/commonMain/moko-resources/files/product.json';
import { createStore, delay } from './store';

/** One thing for sale in the Catalog. `price` is in whole Japanese yen. */
export type Product = { id: string; name: string; category: string; emoji: string; price: number };

/** The currency of every Price (ISO 4217): whole Japanese yen. */
export const CURRENCY = 'JPY';

/** Formats whole yen for display, e.g. 1280 -> "¥1,280". */
export const formatPrice = (yen: number) => '¥' + String(yen).replace(/\B(?=(\d{3})+(?!\d))/g, ',');

/** Service: reads the Products after 500 ms, to feel like a network call. Stateless. */
export async function fetchProducts(): Promise<Product[]> {
  await delay(500);
  return productJson;
}

/** Repository: holds the Home content, null until the first load. */
export function createHomeRepository(fetch = fetchProducts) {
  const products = createStore<Product[] | null>(null);
  return {
    products,
    refresh: async () => {
      const fetched = await fetch();
      products.set(() => fetched);
    },
  };
}
