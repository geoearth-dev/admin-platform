import axios, { type AxiosInstance, type AxiosResponse } from 'axios';
import type { RequestAuthOptions, RequestConfig } from './modules/types';
import { setupRequestInterceptors } from './modules/interceptors';
import { FileUploader, type UploadRequestConfig } from './modules/uploader';
import { FileDownloader, type DownloadRequestConfig } from './modules/downloader';
import type { AjaxResult } from '@/types/base/api/common';

class Request {
  private readonly instance: AxiosInstance;
  private authOptions?: RequestAuthOptions;

  private readonly uploader: FileUploader;
  private readonly downloader: FileDownloader;
  constructor() {
    this.instance = axios.create({
      baseURL: import.meta.env.VITE_API_BASE || '/api',
      timeout: 12000,
      withCredentials: true,
    });
    // 拦截器
    setupRequestInterceptors(this.instance, () => this.authOptions);
    // 文件上传器
    this.uploader = new FileUploader(this);
    // 文件下载器
    this.downloader = new FileDownloader(this);
  }
  /**
   * 注入Auth处理。
   */
  configureAuth(options: RequestAuthOptions): void {
    this.authOptions = options;
  }
  //#region 常用方法
  /**
   * 获取基础URL
   */
  // public getBaseUrl() {
  //   return this.instance.defaults.baseURL;
  // }
  /**
   * GET
   */
  get<T = unknown, P = unknown>(url: string, params?: P, config?: RequestConfig<P>): Promise<T> {
    return this.request<T, P>({ ...config, url, params, method: 'GET' });
  }
  /**
   * POST
   */
  post<T = unknown, D = unknown>(url: string, data?: D, config?: RequestConfig<D>): Promise<T> {
    return this.request<T, D>({ ...config, url, data, method: 'POST' });
  }
  /**
   * PUT
   */
  put<T = unknown, D = unknown>(url: string, data?: D, config?: RequestConfig<D>): Promise<T> {
    return this.request<T, D>({ ...config, url, data, method: 'PUT' });
  }

  /**
   * PATCH
   */
  patch<T = unknown, D = unknown>(url: string, data?: D, config?: RequestConfig<D>): Promise<T> {
    return this.request<T, D>({ ...config, url, data, method: 'PATCH' });
  }
  /**
   * DELETE
   */
  delete<T = unknown>(url: string, config?: RequestConfig): Promise<T> {
    return this.request<T>({ ...config, url, method: 'DELETE' });
  }
  /**
   * 统一解包后端 ApiResult.data。
   *
   * 非 200 业务码已经由响应拦截器抛出，
   * 这里不再重复显示错误。
   */
  async request<T = unknown, D = unknown>(config: RequestConfig<D>): Promise<T> {
    const response = await this.instance.request<AjaxResult<T>, AxiosResponse<AjaxResult<T>>, D>(
      config,
    );
    return response.data.data;
  }
  /**
   * 返回原始 AxiosResponse。
   *
   * 特殊接口、第三方接口、需要读取响应头时使用。
   */
  raw<T = unknown, D = unknown>(config: RequestConfig<D>): Promise<AxiosResponse<T>> {
    return this.instance.request<T, AxiosResponse<T>, D>(config);
  }
  /**
   * 文件上传
   */
  upload<T = unknown>(
    url: string,
    data: Blob | FormData,
    config?: UploadRequestConfig,
  ): Promise<T> {
    return this.uploader.upload<T>(url, data, config);
  }
  /**
   * 请求文件内容，返回 Blob。
   */
  download<D = unknown>(url: string, config?: DownloadRequestConfig<D>): Promise<Blob> {
    return this.downloader.download<D>(url, config);
  }
  /**
   * 请求文件内容，返回 文件内容及响应头。
   */
  downloadRaw<D = unknown>(
    url: string,
    config?: DownloadRequestConfig<D>,
  ): Promise<AxiosResponse<Blob>> {
    return this.downloader.downloadRaw<D>(url, config);
  }

  //#endregion
}

export { Request };
