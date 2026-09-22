import type {
  OnlineQueryParams,
  OnlineSession,
} from '@/types/base/api/monitor/online'
import { requestClient } from '@/utils/request'

// 查询在线用户列表
export function list(params: OnlineQueryParams = {}, silent = false) {
  return requestClient.get<OnlineSession[]>(
    '/monitor/online-session/list',
    params,
    { silent },
  )
}

// 强退用户
export function forceLogout(sessionId: string) {
  return requestClient.delete<void>(
    `/monitor/online-session/${encodeURIComponent(sessionId)}`,
  )
}
