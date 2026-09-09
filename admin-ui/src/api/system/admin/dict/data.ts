import { requestClient } from "@/utils/request";


// 查询字典数据列表
export function listData(query) {
  return requestClient.get('/system/dict/data/list', query);
}

// 查询字典数据详细
export function getData(dictCode) {
  return requestClient.get('/system/dict/data/' + dictCode);
}

// 根据字典类型查询字典数据信息
export function getDicts(dictType) {
  return requestClient.get('/system/dict/data/type/' + dictType);
}

// 新增字典数据
export function addData(data) {
  return requestClient.post('/system/dict/data', data);
}

// 修改字典数据
export function updateData(data) {
  return requestClient.put('/system/dict/data', data);
}

// 删除字典数据
export function delData(dictCode) {
  return requestClient.delete('/system/dict/data/' + dictCode);
}
