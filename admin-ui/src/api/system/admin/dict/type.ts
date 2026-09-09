import { requestClient } from "@/utils/request";


// 查询字典类型列表
export function listType(query) {
  return requestClient.get('/system/dict/type/list', query);
}

// 查询字典类型详细
export function getType(dictId) {
  return requestClient.get('/system/dict/type/' + dictId);
}

// 新增字典类型
export function addType(data) {
  return requestClient.post('/system/dict/type', data);
}

// 修改字典类型
export function updateType(data) {
  return requestClient.put('/system/dict/type', data);
}

// 删除字典类型
export function delType(dictId) {
  return requestClient.delete('/system/dict/type/' + dictId);
}

// 刷新字典缓存
export function refreshCache() {
  return requestClient.delete('/system/dict/type/refreshCache');
}

// 获取字典选择框列表
export function optionselect() {
  return requestClient.get('/system/dict/type/optionselect');
}
