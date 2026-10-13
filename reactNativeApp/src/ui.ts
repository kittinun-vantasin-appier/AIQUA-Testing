import { useColorScheme } from 'react-native';

// Copied from sharedLogic's moko-resources (base/strings.xml, colors/colors.xml): only what this app uses.

export const strings = {
  tabHome: 'Home',
  tabCart: 'Cart',
  homeTitle: 'Groceries',
  homeError: 'Couldn’t load products',
  retry: 'Retry',
  cartTitle: 'Cart',
  cartEmpty: 'Your cart is empty',
  startShopping: 'Start shopping',
  priceEach: (price: string) => `${price} each`,
  total: 'Total',
  buy: 'Buy',
  placingOrder: 'Placing your order…',
  orderPlaced: 'Order placed',
  orderFailed: 'Order failed',
  orderFailedHint: 'Your cart is still here. Please try again.',
};

const light = {
  primary: '#3D863E',
  onPrimary: '#FFFFFF',
  primaryContainer: '#C2EEBC',
  onPrimaryContainer: '#1E361D',
  error: '#C05648',
  background: '#FFFFFF',
  onBackground: '#1B1B1B',
  onSurfaceVariant: '#5E5E5E',
  surfaceContainer: '#F1F1F1',
  outlineVariant: '#C6C6C6',
};

const dark: typeof light = {
  primary: '#7EDB7B',
  onPrimary: '#1E361D',
  primaryContainer: '#336A33',
  onPrimaryContainer: '#C2EEBC',
  error: '#FFB4A5',
  background: '#1B1B1B',
  onBackground: '#E2E2E2',
  onSurfaceVariant: '#C6C6C6',
  surfaceContainer: '#282828',
  outlineVariant: '#474747',
};

export type Colors = typeof light;

export const useColors = (): Colors => (useColorScheme() === 'dark' ? dark : light);
