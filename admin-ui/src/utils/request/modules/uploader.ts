import type { Request } from '../request';
import type { RequestConfig } from './types';

export type UploadRequestConfig = Omit<
  RequestConfig<FormData>,
  'url' | 'data' | 'method' | 'responseType'
> & {
  /** 直接传文件时使用的字段名，默认 file */
  fieldName?: string;
};

export class FileUploader {
  private readonly client: Pick<Request, 'post'>;

  constructor(client: Pick<Request, 'post'>) {
    this.client = client;
  }

  /**
   * 上传文件。
   * File 继承 Blob，因此也可以直接传 File。
   * 多文件或附带其他字段时，由调用方构造 FormData。
   */
  upload<T = unknown>(
    url: string,
    data: Blob | FormData,
    config?: UploadRequestConfig,
  ): Promise<T> {
    const { fieldName = 'file', ...requestConfig } = config ?? {};

    const formData = data instanceof FormData ? data : new FormData();

    if (!(data instanceof FormData)) {
      formData.append(fieldName, data);
    }

    return this.client.post<T, FormData>(url, formData, {
      ...requestConfig,
      responseType: 'json',
    });
  }
}
