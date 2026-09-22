import { requestClient } from '@/utils/request';

const TEST_DOWNLOAD_URL = 'https://httpbin.org/bytes/1024';
const TEST_IMAGE_URL = 'https://httpbin.org/image/png';

// 公网测试资源不携带系统登录凭证。
const downloadOptions = { noAuth: true, withCredentials: false };

/**
 * 下载文件，获取Blob
 * @returns Blob
 */
async function downloadFile1() {
  return requestClient.download(TEST_DOWNLOAD_URL, downloadOptions);
}

/**
 * 下载文件，获取完整的Response
 * @returns AxiosResponse<Blob>
 */
async function downloadFile2() {
  return requestClient.downloadRaw(TEST_DOWNLOAD_URL, downloadOptions);
}

export { downloadFile1, downloadFile2, TEST_DOWNLOAD_URL, TEST_IMAGE_URL };
