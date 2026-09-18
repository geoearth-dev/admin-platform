export interface NotificationItem {
  id: number | string
  avatar: string
  title: string
  message: string
  date: string
  isRead: boolean
  typeLabel?: string
}

export interface NoticeDetail {
  noticeTitle: string
  noticeContent?: string | null
  createBy?: string
  createTime?: string
  typeLabel?: string
  statusLabel?: string
}
