import type { TagProps } from 'element-plus'
import { acceptHMRUpdate, defineStore } from 'pinia'
import { ref } from 'vue'
import { getDicts } from '@/api/system/dict/data'

/** 下拉框、单选框和字典标签共用的选项。 */
export interface DictOption {
  label: string
  value: string
  elTagType?: TagProps['type']
  elTagClass: string
}

/** 后端的 default、空值或自定义 listClass 不作为 ElTag 的 type 传入。 */
function getTagType(value?: string | null): DictOption['elTagType'] {
  switch (value) {
    case 'primary':
    case 'success':
    case 'info':
    case 'warning':
    case 'danger':
      return value
    default:
      return undefined
  }
}

/** 按需加载的内存缓存；刷新浏览器后重新获取，不持久化字典选项。 */
const useDictStore = defineStore('dict', () => {
  const dict = ref<Record<string, DictOption[]>>({})
  // 首次加载尚未完成时缓存仍为 null，也需要通知 useDict 重新加载。
  const cacheVersion = ref(0)
  // 请求句柄不放入响应式 state；每个 Store 实例各自管理。
  const pending = new Map<string, Promise<DictOption[]>>()

  /** null 表示未加载；[] 表示已加载，但该类型没有可用选项。 */
  function getDict(key: string): DictOption[] | null {
    if (!key?.trim() || !Object.hasOwn(dict.value, key)) return null
    return dict.value[key] ?? null
  }

  function setDict(key: string, value: DictOption[]): void {
    if (!key?.trim()) return
    // 手动设置的新值不能再被之前的请求覆盖。
    pending.delete(key)
    dict.value = { ...dict.value, [key]: value }
  }

  function removeDict(key: string): boolean {
    if (!key?.trim()) return false
    const existed = Object.hasOwn(dict.value, key) || pending.has(key)
    pending.delete(key)
    delete dict.value[key]
    cacheVersion.value += 1
    return existed
  }

  function cleanDict(): void {
    pending.clear()
    dict.value = {}
    cacheVersion.value += 1
  }

  /** 命中缓存直接返回；同一类型的并发调用共用一次请求。 */
  function loadDict(key: string, force = false): Promise<DictOption[]> {
    if (!key?.trim()) return Promise.resolve([])
    if (force) removeDict(key)
    const cached = getDict(key)
    if (cached !== null) return Promise.resolve(cached)
    const active = pending.get(key)
    if (active) return active

    const request = getDicts(key)
      .then((rows) => {
        // 清理缓存、手动设置或强制重载后，忽略原请求的旧结果。
        if (pending.get(key) !== request) return getDict(key) ?? []
        const options: DictOption[] = rows.map((row) => ({
          label: row.dictLabel,
          value: row.dictValue,
          elTagType: getTagType(row.listClass),
          elTagClass: row.cssClass ?? '',
        }))
        dict.value = { ...dict.value, [key]: options }
        return options
      })
      .finally(() => {
        // 失败也释放句柄，下一次调用可以重试。
        if (pending.get(key) === request) pending.delete(key)
      })
    pending.set(key, request)
    return request
  }

  /** Setup Store 手动提供 $reset，兼容项目 resetAllStores()。 */
  function $reset(): void {
    cleanDict()
  }

  return {
    dict,
    cacheVersion,
    getDict,
    setDict,
    removeDict,
    cleanDict,
    loadDict,
    $reset,
  }
})

if (import.meta.hot) {
  import.meta.hot.accept(acceptHMRUpdate(useDictStore, import.meta.hot))
}

export default useDictStore
