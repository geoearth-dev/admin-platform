import type { App } from 'vue';
import { createPersistedState } from 'pinia-plugin-persistedstate';
import { createPinia, type Pinia } from 'pinia';
import SecureLSModule from 'secure-ls';

let pinia: Pinia;

const SecureLS =
  (SecureLSModule as unknown as { default?: typeof SecureLSModule }).default ?? SecureLSModule;

/**
 * @zh_CN 初始化pinia
 */
export async function initStores(app: App, namespace: string) {
  pinia = createPinia();
  const ls = new SecureLS({
    encodingType: 'aes',
    encryptionSecret: import.meta.env.VITE_APP_STORE_SECURE_KEY,
    isCompression: true,
    encryptionNamespace: `${namespace}-secure-meta`,
  });
  pinia.use(
    createPersistedState({
      // key $appName-$store.id
      key: (storeKey) => `${namespace}-${storeKey}`,
      storage: import.meta.env.DEV
        ? localStorage
        : {
            getItem(key) {
              return ls.get(key);
            },
            setItem(key, value) {
              ls.set(key, value);
            },
          },
    }),
  );
  app.use(pinia);
  return pinia;
}

export function resetAllStores() {
  if (!pinia) {
    console.error('Pinia is not installed');
    return;
  }
  const allStores = pinia._s;
  allStores.forEach((store) => {
    store.$reset();
  });
}
