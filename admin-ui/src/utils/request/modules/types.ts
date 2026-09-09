import type { AxiosRequestConfig } from 'axios';

interface RequestConfig<T = unknown> extends AxiosRequestConfig<T> {
  /** 是否显示 loading（全局） */
  loading?: boolean;
  /** 是否跳过错误提示（默认会提示） */
  silent?: boolean;
  /** 是否跳过登录拦截 */
  noAuth?: boolean;
}
/**
 * 后端统一响应结构
 */
interface ApiResponse<T> {
  /**
   * 200 表示成功 其他表示失败
   */
  code: number;
  data: T;
  message: string;
}

interface RequestAuthOptions {
  /**
   * 获取当前 Token。
   */
  getToken?: () => null | string | undefined;
  /**
   * 刷新 AccessToken，返回新的 Token。
   */
  refreshToken?: () => Promise<string>;
  /**
   * 用户确认重新登录后的处理函数。
   */
  onUnauthorized?: () => Promise<void> | void;
}
export type { ApiResponse, RequestConfig, RequestAuthOptions };
