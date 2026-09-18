import type { BaseEntity, PageParam } from '../common'

/** 通知公告分页查询参数 */
export interface NoticeQueryParams extends PageParam {
  /** 公告标题 */
  noticeTitle?: string
  /** 操作人员 */
  createBy?: string
  /** 公告类型 */
  noticeType?: string
  /** 状态（0关闭 1正常） */
  status?: '0' | '1'
}

/** 通知公告信息 */
export interface SysNotice extends BaseEntity {
  /** 公告编号 */
  id: number
  /** 公告标题 */
  noticeTitle: string
  /** 公告类型（1通知 2公告） */
  noticeType: '1' | '2'
  /** 公告内容 */
  noticeContent?: string
  avatar?: string | null
  link?: string | null
  /** 状态（0关闭 1正常） */
  status?: '0' | '1'
  /** 是否已读 */
  isRead: boolean
}

/** 对应 NoticeSaveDTO；修改时携带 id。 */
export interface NoticeSaveParams {
  id?: number
  /** 必填，最多50个字符 */
  noticeTitle: string
  /** 公告类型（1通知 2公告） */
  noticeType: '1' | '2'
  noticeContent?: string
  avatar?: string | null
  link?: string
  /** 状态（0关闭 1正常） */
  status?: '0' | '1'
  remark?: string
}

/** 对应 NoticeReadVO。 */
export interface SysNoticeTopResult {
  sysNotice: SysNotice[]
  /** 全部正常公告的未读总数，不局限于顶部5条 */
  unreadCount: number
}

/** 公告已读用户查询参数 */
export interface NoticeReadUserQueryParams extends PageParam {
  /** 公告编号 */
  noticeId: number
  /** 关键字（登录名/用户名） */
  searchValue?: string
}

/** 公告已读用户 */
export interface NoticeReadUser {
  userId: number
  userName?: string
  nickName?: string
  deptName?: string
  phoneNumber?: string
  readTime?: string
}
