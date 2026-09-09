import type { AxiosInstance, InternalAxiosRequestConfig } from 'axios';
import errorCode from './errorCode';
import { ElMessage } from 'element-plus';
import type { ApiResponse, RequestAuthOptions, RequestConfig } from './types';
import axios from 'axios';

const MAX_REQUEST_SIGNATURE_LENGTH = 1024 * 1024; //限制存放数据5M

type InternalRequestConfig = InternalAxiosRequestConfig &
  Pick<RequestConfig, 'noAuth' | 'loading' | 'silent'> & {
    requestKey?: string;
    /**
     * 当前请求是否已经在刷新 Token 后重试过。
     */
    authRetried?: boolean;
  };
type GetAuthOptions = () => RequestAuthOptions | undefined;

const requestRecords = new Set<string>();

/**
 * 获取业务错误信息。
 */
function getBusinessErrorMessage(response: ApiResponse<unknown>): string {
  return response.message ?? errorCode[String(response.code)] ?? errorCode['default'];
}
/**
 * 获取 Axios 错误提示。
 */
function getRequestErrorMessage(error: unknown): string {
  if (!axios.isAxiosError(error)) {
    return error instanceof Error ? error.message : '请求发生未知错误';
  }

  if (
    error.code === 'ECONNABORTED' ||
    error.code === 'ETIMEDOUT' ||
    error.message.toLowerCase().includes('timeout')
  ) {
    return '系统接口请求超时';
  }

  /**
   * 没有 response 一般意味着请求根本没收到服务器响应。
   */
  if (!error.response) {
    return '后端接口连接异常';
  }

  const responseData = error.response.data;

  if (isApiResponse(responseData)) {
    return getBusinessErrorMessage(responseData);
  }

  const status = error.response.status;

  return errorCode[String(status)] || `系统接口 ${status} 异常`;
}

/**
 * 创建请求唯一签名。
 */
function createRequestKey(config: InternalRequestConfig): null | string {
  // FormData 无法通过 JSON.stringify 区分文件内容。
  // 跳过当前基于 JSON 的重复提交检查。
  if (config.data instanceof FormData) {
    return null;
  }
  const key: string = [
    config.method?.toUpperCase(),
    config.baseURL,
    config.url,
    JSON.stringify(config.params ?? null),
    JSON.stringify(config.data ?? null),
  ].join('|');
  if (key.length > MAX_REQUEST_SIGNATURE_LENGTH) {
    console.warn(`[${config.url ?? ''}] 请求数据较大，跳过前端重复提交检查`);
    return null;
  }
  return key;
}

/**
 * 释放正在执行中的请求。
 */
function releasePendingRequest(config: InternalRequestConfig): void {
  const internalConfig = config as InternalRequestConfig;
  const key = internalConfig.requestKey ?? '';
  if (requestRecords.has(key)) {
    requestRecords.delete(key);
    delete internalConfig.requestKey;
  }
}

/**
 * 请求拦截器
 */
function addRequestInterceptor(axiosInstance: AxiosInstance, getAuthOptions: GetAuthOptions) {
  axiosInstance.interceptors.request.use(
    (requestConfig) => {
      const config = requestConfig as InternalRequestConfig;

      /**
       * Token
       */
      if (config.noAuth) {
        // 如果不需要登录拦截，则清除请求头中的Authorization
        config.headers.delete('Authorization');
      } else {
        const token = getAuthOptions()?.getToken?.();

        if (token) {
          config.headers.set('Authorization', `Bearer ${token}`);
        } else {
          config.headers.delete('Authorization');
        }
      }
      /**
       * 设置请求头中的语言信息
       */
      config.headers.set('Accept-Language', navigator.language || 'zh-CN');
      /**
       * 防止重复提交。
       */
      // 请求方法
      const method = config.method?.toLowerCase();
      if (method === 'post' || method === 'put' || method === 'patch') {
        const key = createRequestKey(config);
        if (key) {
          if (requestRecords.has(key)) {
            return Promise.reject(new Error('请求正在处理中，请勿重复提交'));
          } else {
            requestRecords.add(key);
            config.requestKey = key;
          }
        }
      }
      return config;
    },
    (error) => {
      // 处理请求错误
      return Promise.reject(error);
    },
  );
}

/**
 * 响应拦截器
 */
function addResponseInterceptor(axiosInstance: AxiosInstance, getAuthOptions: GetAuthOptions) {
  axiosInstance.interceptors.response.use(
    (response) => {
      releasePendingRequest(response.config);

      const config = response.config as InternalRequestConfig;
      // 二进制数据则直接返回
      if (config.responseType === 'blob' || config.responseType === 'arraybuffer') {
        return response;
      }
      const responseData = response.data;
      /**
       * 非业务响应 调用方处理。
       */
      if (!isApiResponse(responseData)) {
        return response;
      }
      // 未设置状态码则默认成功状态
      const code = responseData.code;
      if (code === 200) {
        return response;
      }
      // 获取错误信息
      const message = getBusinessErrorMessage(responseData);
      // 业务层 401
      if (code === 401) {
        return refreshAndRetry(axiosInstance, config, getAuthOptions, new Error(message));
      }
      /**
       * silent=true 时  不产生全局 UI 提示。
       */
      if (!config.silent) {
        ElMessage.error(message);
      }
      return Promise.reject(new Error(message));
    },
    async (error) => {
      const config = axios.isAxiosError(error)
        ? (error.config as InternalRequestConfig | undefined)
        : undefined;
      // 包括取消请求在内，都需要释放重复提交记录。
      if (config) {
        releasePendingRequest(config);
      }
      // 取消请求不弹错误提示。
      if (axios.isCancel(error)) {
        return Promise.reject(error);
      }

      if (axios.isAxiosError(error)) {
        //HTTP 401
        if (error.response?.status === 401 && config) {
          return refreshAndRetry(axiosInstance, config, getAuthOptions, error);
        }
        if (!config?.silent) {
          ElMessage.error({ message: getRequestErrorMessage(error), duration: 5000 });
        }
      } else {
        ElMessage.error({ message: getRequestErrorMessage(error), duration: 5000 });
      }
      // 处理响应错误
      return Promise.reject(error);
    },
  );
}

/**
 * 判断是否是后端标准响应。
 */
function isApiResponse(value: unknown): value is ApiResponse<unknown> {
  if (value === null || typeof value !== 'object') {
    return false;
  }
  const data = value as Record<string, unknown>;
  return typeof data.code === 'number';
}

function setupRequestInterceptors(axiosInstance: AxiosInstance, getAuthOptions: GetAuthOptions) {
  addRequestInterceptor(axiosInstance, getAuthOptions);
  addResponseInterceptor(axiosInstance, getAuthOptions);
}

/**
 * 全局只允许同时存在一个 refresh 请求。
 *
 * 多个接口同时返回 401 时，其他接口等待同一个 Promise。
 */
let refreshPromise: null | Promise<string> = null;

async function refreshAndRetry(
  axiosInstance: AxiosInstance,
  config: InternalRequestConfig,
  getAuthOptions: GetAuthOptions,
  originalError: unknown,
) {
  // 登录、刷新、退出等 noAuth 请求不参与自动刷新。
  if (config.noAuth) {
    return Promise.reject(originalError);
  }
  const authOptions = getAuthOptions();
  if (!authOptions) {
    return Promise.reject('请求认证处理器尚未初始化');
  }

  // 已重试过仍然 401，说明新 Token 也无法通过认证。
  if (config.authRetried) {
    await authOptions.onUnauthorized?.();
    return Promise.reject(originalError);
  }

  if (!authOptions.refreshToken) {
    await authOptions.onUnauthorized?.();
    return Promise.reject(originalError);
  }

  config.authRetried = true;

  /*
   * 多个 401 只创建一个 refresh 请求。
   * refresh 失败时，只在这个 Promise 中统一处理登录过期。
   */
  if (!refreshPromise) {
    refreshPromise = authOptions
      .refreshToken()
      .catch(async (error) => {
        await authOptions.onUnauthorized?.();
        throw error;
      })
      .finally(() => {
        refreshPromise = null;
      });
  }

  const newToken = await refreshPromise;

  /*
   * 虽然请求拦截器重放时会再次注入 Token，
   */
  config.headers.set('Authorization', `Bearer ${newToken}`);

  return axiosInstance.request(config);
}

export { setupRequestInterceptors };
