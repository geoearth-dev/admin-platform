import { requestClient } from '@/utils/request';

// 查询学生列表
export function listStudent(query) {
  return requestClient.get('/example/student/list', query);
}

// 查询学生详细
export function getStudent(id) {
  return requestClient.get('/example/student/' + id);
}
// 新增学生
export function addStudent(data) {
  return requestClient.post('/example/student', data);
}

// 修改学生
export function updateStudent(data) {
  return requestClient.put('/example/student', data);
}

// 删除学生
export function delStudent(id) {
  return requestClient.delete('/example/student/' + id);
}
