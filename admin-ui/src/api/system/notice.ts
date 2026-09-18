import { requestClient } from '@/utils/request';
import type { PageResult } from '@/types/base/api/common';
import type {
  NoticeQueryParams,
  NoticeSaveParams,
  NoticeReadUserQueryParams,
  SysNotice,
  NoticeReadUser,
  SysNoticeTopResult,
} from '@/types/base/api/system/notice';

// 查询公告列表
export function listNotice(query: NoticeQueryParams = {}) {
  return requestClient.get<PageResult<SysNotice>>('/system/notice/list', query);
}

// 查询公告详细
export function getNotice(id: number) {
  return requestClient.get<SysNotice>(`/system/notice/${id}`);
}

// 新增公告
export function addNotice(data: NoticeSaveParams) {
  return requestClient.post<void>('/system/notice', data);
}

// 修改公告
export function updateNotice(data: NoticeSaveParams) {
  return requestClient.put<void>('/system/notice', data);
}

// 删除公告
export function delNotice(id: number | number[]) {
  const ids = Array.isArray(id) ? id.join(',') : id;
  return requestClient.delete<void>(`/system/notice/${ids}`);
}

/** 最新5条正常公告（含内容、已读状态）及全部正常公告的未读总数。 */
export function listNoticeTop(): Promise<SysNoticeTopResult> {
  return requestClient.get<SysNoticeTopResult>('/system/notice/listTop');
}

/** 标记当前登录用户已读；用户ID由后端获取。 */
export function markNoticeRead(noticeId: number) {
  return requestClient.post<void>('/system/notice/markRead', undefined, {
    params: { noticeId },
  });
}

/** 标记指定公告已读，保留逗号分隔字符串调用方式，也支持ID数组。 */
export function markNoticeReadAll(ids: string | number[]) {
  return requestClient.post<void>('/system/notice/markReadAll', undefined, {
    params: { ids: Array.isArray(ids) ? ids.join(',') : ids },
  });
}

/** 已读用户分页列表，支持 noticeId、searchValue、pageNum、pageSize。 */
export function listNoticeReadUsers(query: NoticeReadUserQueryParams) {
  return requestClient.get<PageResult<NoticeReadUser>>('/system/notice/readUsers/list', query);
}
