import { createCartRepository, createOrderService, quantityOf } from '../src/cart';
import type { Product } from '../src/catalog';

const apple: Product = { id: 'apple', name: 'Apple', category: 'fruits', emoji: '🍎', price: 198 };

test('a Cart Line holds at most 9 units', () => {
  const repository = createCartRepository(createOrderService(0));
  for (let i = 0; i < 10; i++) repository.add(apple);
  expect(quantityOf(repository.cart.get(), 'apple')).toBe(9);
});

test('taking away the last unit removes the Cart Line', () => {
  const repository = createCartRepository(createOrderService(0));
  repository.add(apple);
  repository.decrement('apple');
  expect(repository.cart.get().lines).toEqual([]);
});

test('every 5th Order fails and leaves the Cart as it was', async () => {
  const repository = createCartRepository(createOrderService(0));
  for (let i = 0; i < 4; i++) {
    repository.add(apple);
    await repository.checkout();
  }
  repository.add(apple);
  await expect(repository.checkout()).rejects.toThrow('Simulated failure on attempt 5');
  expect(quantityOf(repository.cart.get(), 'apple')).toBe(1);
});
