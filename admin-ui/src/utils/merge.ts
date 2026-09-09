import { createDefu } from 'defu';

export { defu as merge } from 'defu';

export const mergeWithArrayOverride = createDefu((originObj, key, updates) => {
  if (Array.isArray(originObj[key]) && Array.isArray(updates)) {
    Reflect.set(originObj, key, [...updates],)
    return true;
  }
  return false
});
