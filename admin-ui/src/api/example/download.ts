import { requestClient } from '@/utils/request';
/**
 * 下载文件，获取Blob
 * @returns Blob
 */
async function downloadFile1() {
  return requestClient.download(
    'https://unpkg.com/@vbenjs/static-source@0.1.7/source/logo-v1.webp',
  );
}

/**
 * 下载文件，获取完整的Response
 * @returns AxiosResponse<Blob>
 */
async function downloadFile2() {
  return requestClient.downloadRaw(
    'https://unpkg.com/@vbenjs/static-source@0.1.7/source/logo-v1.webp',
  );
}

export { downloadFile1, downloadFile2 };
