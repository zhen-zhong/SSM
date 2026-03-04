import { request } from '../request';

/** 获取所有用户列表 */
export function fetchUserList() {
  return request<any>({ url: '/user/list', method: 'get' });
}

/** 新增用户 */
export function fetchAddUser(data: any) {
  return request<any>({ url: '/user/register', method: 'post', data });
}

/** 更新用户基本信息 */
export function fetchUpdateUser(data: any) {
  return request<any>({ url: '/user/update', method: 'put', data });
}

/** 管理员重置密码 */
export function fetchResetPassword(userId: number, newPassword: string) {
  return request<any>({
    url: '/user/reset-password-admin',
    method: 'put',
    params: { userId, newPassword }
  });
}

export function fetchAssignRole(userId: number, roleIds: number[]) {
  return request<any>({
    url: '/user/role',
    method: 'post',
    params: { userId },
    data: roleIds
  });
}

/** 删除用户 */
export function fetchDeleteUser(id: number) {
  return request<any>({ url: `/user/${id}`, method: 'delete' });
}

export function fetchAllRoles() {
  return request<any>({ url: '/user/role/list', method: 'get' });
}

/** 获取所有角色列表 */
export function fetchRoleList() {
  return request<any>({ url: '/user/role/list', method: 'get' });
}

/** 新增角色 */
export function fetchAddRole(data: any) {
  return request<any>({ url: '/role/add', method: 'post', data });
}

/** 修改角色 */
export function fetchUpdateRole(data: any) {
  return request<any>({ url: '/role/update', method: 'put', data });
}

/** 删除角色 */
export function fetchDeleteRole(id: number) {
  return request<any>({ url: `/role/${id}`, method: 'delete' });
}
