import { requestClient } from '@/utils/request';

/**
 * 模拟任意状态码
 */
async function getMockStatusApi(status: number) {
  return requestClient.get<void>('/mock/status', { status });
}

export { getMockStatusApi };
