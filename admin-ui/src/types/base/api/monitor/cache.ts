/** 缓存分组及缓存详情，字段与 SysCache 一致。 */
export interface SysCache {
  cacheName: string
  cacheKey: string
  cacheValue: string
  remark: string
}

/** Redis INFO 字段由服务端以字符串返回，部分字段可能不存在。 */
export interface RedisInfo {
  [key: string]: string | undefined
  redis_version?: string
  redis_mode?: string
  tcp_port?: string
  connected_clients?: string
  uptime_in_days?: string
  used_memory?: string
  used_memory_human?: string
  used_memory_peak?: string
  used_memory_peak_human?: string
  maxmemory?: string
  maxmemory_human?: string
  used_cpu_user?: string
  used_cpu_sys?: string
  aof_enabled?: string
  rdb_last_bgsave_status?: string
  instantaneous_input_kbps?: string
  instantaneous_output_kbps?: string
}

export interface CacheMetrics {
  info: RedisInfo
  dbSize: number
  commandStats: { name: string; calls: number }[]
}
