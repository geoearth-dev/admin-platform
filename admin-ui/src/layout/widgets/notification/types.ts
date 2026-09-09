import type { HistoryState, LocationQueryRaw } from 'vue-router';

interface NotificationItem {
  id: number | string;
  avatar: string;
  date: string;
  isRead?: boolean;
  message: string;
  title: string;
  /**
   * 跳转链接，可以是路由路径或完整 URL
   * @example '/dashboard' 或 'https://example.com'
   */
  link?: string;
  query?: LocationQueryRaw;
  state?: HistoryState;
  /** 业务字段 */
  [key: string]: unknown;
}

export type { NotificationItem };
