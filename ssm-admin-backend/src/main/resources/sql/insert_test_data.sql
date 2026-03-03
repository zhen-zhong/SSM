-- 批量插入测试用户
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `phone`, `status`) VALUES 
(2, 'operator', '123456', '普通操作员', '13800000002', 1),
(3, 'test', '123456', '测试专员', '13800000003', 1),
(4, 'banned_user', '123456', '被封禁用户', '13800000004', 0);

-- 批量插入菜单树 (M目录, C菜单, F按钮)
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `path`, `component`, `perms`, `icon`, `menu_type`, `order_num`) VALUES 
(1, 0, '仪表盘', '/dashboard', 'views/dashboard/index', NULL, 'dashboard', 'C', 1),
(2, 0, '系统管理', '/sys', NULL, NULL, 'setting', 'M', 2),
(3, 2, '用户管理', '/sys/user', 'views/sys/user/index', 'sys:user:list', 'user', 'C', 1),
(4, 2, '角色管理', '/sys/role', 'views/sys/role/index', 'sys:role:list', 'team', 'C', 2),
(5, 2, '菜单管理', '/sys/menu', 'views/sys/menu/index', 'sys:menu:list', 'menu', 'C', 3),
(6, 3, '用户新增', NULL, NULL, 'sys:user:add', NULL, 'F', 1),
(7, 3, '用户修改', NULL, NULL, 'sys:user:edit', NULL, 'F', 2),
(8, 3, '用户删除', NULL, NULL, 'sys:user:delete', NULL, 'F', 3);

-- 批量分配 用户-角色
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (2, 2), (3, 2);

-- 批量分配 角色-菜单 (超管拥有全部，操作员只拥有部分)
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES 
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8),
(2, 1), (2, 2), (2, 3);

-- 批量插入字典数据
INSERT INTO `sys_dict` (`dict_type`, `dict_label`, `dict_value`, `sort_num`) VALUES 
('sys_normal_disable', '正常', '1', 1),
('sys_normal_disable', '停用', '0', 2),
('sys_user_sex', '男', '1', 1),
('sys_user_sex', '女', '2', 2);