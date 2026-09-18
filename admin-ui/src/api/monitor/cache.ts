import type { CacheMetrics, SysCache } from '@/types/base/api/monitor/cache'
import { requestClient } from '@/utils/request'

export function getCache() {
  return requestClient.get<CacheMetrics>('/monitor/cache')
}

export function listCacheName() {
  return requestClient.get<SysCache[]>('/monitor/cache/groups')
}

export function listCacheKey(cacheName: string) {
  return requestClient.get<string[]>(
    `/monitor/cache/keys/${encodeURIComponent(cacheName)}`,
  )
}

export function getCacheValue(cacheName: string, cacheKey: string) {
  return requestClient.get<SysCache>('/monitor/cache/value', {
    cacheName,
    cacheKey,
  })
}

export function clearCacheName(cacheName: string) {
  return requestClient.delete<void>(
    `/monitor/cache/groups/${encodeURIComponent(cacheName)}`,
  )
}

export function clearCacheKey(cacheKey: string) {
  return requestClient.delete<void>('/monitor/cache/keys', {
    params: { cacheKey },
  })
}
