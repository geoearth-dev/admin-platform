import { computed, watch } from 'vue'
import type { ComputedRef } from 'vue'
import useDictStore from '@/store/system/admin/dict'
import type { DictOption } from '@/store/system/admin/dict'

/**
 * 在组件 setup 中按需获取字典。
 * const { sys_normal_disable } = useDict('sys_normal_disable');
 * 清理缓存后，仍在使用该字典的组件会重新加载；缓存更新同步反映到所有调用者。
 */
export function useDict<T extends string>(
  ...dictTypes: T[]
): Record<T, ComputedRef<DictOption[]>> {
  const store = useDictStore()
  return Object.fromEntries(
    [...new Set(dictTypes)].map((key) => {
      const options = computed(() => store.getDict(key) ?? [])
      watch(
        [() => store.getDict(key), () => store.cacheVersion],
        ([cached]) => {
          if (cached === null) {
            void store.loadDict(key).catch(() => {
              // 请求层已提示错误，不缓存失败结果；后续调用可重试。
            })
          }
        },
        { immediate: true },
      )
      return [key, options]
    }),
  ) as Record<T, ComputedRef<DictOption[]>>
}
