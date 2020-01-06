import * as storage from "store";
import eventsPlugin from "store/plugins/events";

interface StoreJsAPI {
  readonly version: string;
  readonly enabled: boolean;
  get(key: string, optionalDefaultValue?: any): any;
  set(key: string, value: any): any;
  remove(key: string): void;
  each(callback: (val: any, namespacedKey: string) => void): void;
  clearAll(): void;
  hasNamespace(namespace: string): boolean;
  createStore(plugins?: Function[], namespace?: string): StoreJsAPI;
  addPlugin(plugin: Function): void;
  namespace(namespace: string): StoreJsAPI;
  watch(key: string, callback: Function): string;
  unwatch(watchId: string): void;
}

// Store with event plugin types
const store = storage as StoreJsAPI;

// Add events plugin
store.addPlugin(eventsPlugin);

// Methods
export const get = (key: string, defaultValue?: any) =>
  store.get(key, defaultValue);

export const set = (key: string, value: any) => store.set(key, value);

export const remove = (key: string) => store.remove(key);

export const watch = (key: string, callback: Function) =>
  store.watch(key, callback);

export const unwatch = (watchId: string) => store.unwatch(watchId);
