import { useSyncExternalStore } from 'react';

/** A value that Repositories own and screens observe, the TypeScript stand-in for sharedLogic's StateFlow. */
export type Store<T> = {
  get: () => T;
  set: (update: (value: T) => T) => void;
  subscribe: (listener: () => void) => () => void;
};

export function createStore<T>(initial: T): Store<T> {
  let value = initial;
  const listeners = new Set<() => void>();
  return {
    get: () => value,
    set: update => {
      value = update(value);
      listeners.forEach(listener => listener());
    },
    subscribe: listener => {
      listeners.add(listener);
      return () => listeners.delete(listener);
    },
  };
}

export const useStore = <T>(store: Store<T>): T => useSyncExternalStore(store.subscribe, store.get);

export const delay = (ms: number) => new Promise<void>(resolve => setTimeout(resolve, ms));
