import type { RouterVo } from '@/types/model/menu';
import { requestClient } from '@/utils/request';

// 获取路由
export const getRouters = (): Promise<RouterVo[]> => {
  return requestClient.get('/auth/getRouters');
};

// 获取路由
export const getRoutersDpp = (id: string): Promise<RouterVo> => {
  return requestClient.get(`/getRoutersDpp/${id}`);
};


