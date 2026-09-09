import type { AxiosResponse } from 'axios';

import type { Request } from '../request';
import type { RequestConfig } from './types';

export type DownloadRequestConfig<D = unknown> = Omit<RequestConfig<D>, 'url' | 'responseType'>;

export class FileDownloader {
  private readonly client: Pick<Request, 'raw'>;

  constructor(client: Pick<Request, 'raw'>) {
    this.client = client;
  }

  /** 获取文件内容 */
  async download<D = unknown>(url: string, config?: DownloadRequestConfig<D>): Promise<Blob> {
    const response = await this.downloadRaw<D>(url, config);
    return response.data;
  }

  /** 获取文件内容及响应头，例如读取后端返回的文件名 */
  downloadRaw<D = unknown>(
    url: string,
    config?: DownloadRequestConfig<D>,
  ): Promise<AxiosResponse<Blob>> {
    return this.client.raw<Blob, D>({
      method: 'GET',
      ...config,
      url,
      responseType: 'blob',
    });
  }
}
