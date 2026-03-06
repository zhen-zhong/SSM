import { request } from '../request';

/** * =================================================================
 * 用户管理模块 (对应后端 SysUserController)
 * =================================================================
 */

/** 用户登录 */
export function fetchLogin(data: any) {
  return request<any>({ url: '/user/login', method: 'post', data });
}

/** 获取当前登录用户信息及角色 */
export function fetchGetUserInfo() {
  return request<any>({ url: '/user/info', method: 'get' });
}

/** * 获取用户列表（分页）
 * @param pageNum 当前页码
 * @param pageSize 每页条数
 */
export function fetchUserList(pageNum: number, pageSize: number) {
  return request<any>({
    url: '/user/list',
    method: 'get',
    params: { pageNum, pageSize }
  });
}

/** 新增用户 (注册) */
export function fetchAddUser(data: any) {
  return request<any>({ url: '/user/register', method: 'post', data });
}

/** 修改用户基本信息 */
export function fetchUpdateUser(data: any) {
  return request<any>({ url: '/user/update', method: 'put', data });
}

/** 管理员重置用户密码 */
export function fetchResetPasswordAdmin(userId: number, newPassword: string) {
  return request<any>({
    url: '/user/reset-password-admin',
    method: 'put',
    params: { userId, newPassword }
  });
}

/** 分配用户角色权限 */
export function fetchAssignRole(userId: number, roleIds: number[]) {
  return request<any>({
    url: '/user/role',
    method: 'post',
    params: { userId },
    data: roleIds
  });
}

/** 获取所有可用角色列表 (用于下拉框，传入大 pageSize 获取全部) */
export function fetchAllRoles(pageNum: number, pageSize: number) {
  return request<any>({
    url: '/user/role/list',
    method: 'get',
    params: { pageNum, pageSize }
  });
}

/** 修改账号状态 */
export function fetchUpdateStatus(id: number, status: number) {
  return request<any>({
    url: '/user/status',
    method: 'put',
    params: { id, status }
  });
}

/** 逻辑删除用户 */
export function fetchDeleteUser(id: number) {
  return request<any>({ url: `/user/${id}`, method: 'delete' });
}

/** 找回密码（身份校验方式） */
export function fetchFindPassword(data: any) {
  return request<any>({ url: '/user/find-password', method: 'post', data });
}

/** 用户自行修改密码 */
export function fetchResetSelfPassword(oldPassword: string, newPassword: string) {
  return request<any>({
    url: '/user/reset-password',
    method: 'put',
    params: { oldPassword, newPassword }
  });
}

/** * =================================================================
 * 角色管理模块
 * =================================================================
 */

/** 获取角色列表 (分页) */
export function fetchRoleList(pageNum: number, pageSize: number) {
  return request<any>({
    url: '/user/role/list',
    method: 'get',
    params: { pageNum, pageSize }
  });
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
