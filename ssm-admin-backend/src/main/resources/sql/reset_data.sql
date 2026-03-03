SET FOREIGN_KEY_CHECKS = 0;

-- 极速清空所有表数据，并将自增主键归零
TRUNCATE TABLE `sys_user`;
TRUNCATE TABLE `sys_role`;
TRUNCATE TABLE `sys_menu`;
TRUNCATE TABLE `sys_user_role`;
TRUNCATE TABLE `sys_role_menu`;
TRUNCATE TABLE `sys_log`;
TRUNCATE TABLE `sys_dict`;

-- 恢复超级管理员基础数据
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`) VALUES (1, 'admin', '123456', '超级管理员');
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', '拥有所有权限', 1, NOW());
INSERT INTO `sys_user_role` VALUES (1, 1);

SET FOREIGN_KEY_CHECKS = 1;