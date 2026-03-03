package com.admin.service.impl;

import com.admin.entity.SysUser;
import com.admin.mapper.SysUserMapper;
import com.admin.service.SysUserService;
import com.admin.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.admin.mapper.SysUserRoleMapper;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Override
    public SysUser getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public String login(String username, String password) {
        SysUser user = userMapper.selectByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            throw new RuntimeException("用户名或密码错误");
        }
        return JwtUtils.createToken(user.getUsername());
    }

    @Override
    public void register(SysUser user) {
        user.setStatus(1);
        user.setIsDeleted(0);
        userMapper.insert(user);
    }

    @Override
    @Transactional
    public void updateUserInfo(SysUser user) {
        // 1. 校验用户是否存在
        SysUser existUser = userMapper.selectById(user.getId());
        if (existUser == null) {
            throw new RuntimeException("修改失败：用户不存在");
        }

        // 2. 如果修改了用户名，校验新用户名是否已存在（防止冲突）
        if (user.getUsername() != null && !user.getUsername().equals(existUser.getUsername())) {
            if (userMapper.selectByUsername(user.getUsername()) != null) {
                throw new RuntimeException("修改失败：用户名已存在");
            }
        }

        // 3. 调用 Mapper 执行动态更新
        // 这里的 update 方法在 XML 中使用了 <set> 和 <if>，会自动忽略 null 字段
        userMapper.update(user);
    }

    @Override
    public void updatePassword(Long id, String newPwd) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setPassword(newPwd);
        userMapper.update(user);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setStatus(status);
        userMapper.update(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class) 
    public void updateRole(Long userId, List<Long> roleIds) {
        // 1. 安全检查：确保要操作的用户确实存在
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("分配角色失败：目标用户不存在");
        }

        // 2. 清空旧数据：删除该用户在 sys_user_role 表中的所有既有关系
        // 这样做是为了实现“覆盖式更新”，逻辑最清晰且不容易出错
        userRoleMapper.deleteByUserId(userId);

        // 3. 插入新数据：如果传入的角色列表不为空，则批量执行插入
        if (roleIds != null && !roleIds.isEmpty()) {
            try {
                userRoleMapper.batchInsert(userId, roleIds);
            } catch (Exception e) {
                // 记录日志或处理数据库约束异常
                throw new RuntimeException("角色分配执行失败，数据库写入异常");
            }
        }
        
        // 💡 论文亮点：此处可以在后续加入清除该用户缓存的逻辑，保证权限实时生效
    }

    @Override
    public void deleteUser(Long id) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setIsDeleted(1);
        userMapper.update(user);
    }

    @Override
    public List<SysUser> listAll() {
        return userMapper.listAll();
    }

    @Override
    @Transactional // 涉及数据修改，建议开启事务
    public void findPassword(String username, String realName, String phone, String newPassword) {
        // 1. 根据用户名查询用户信息
        SysUser user = userMapper.selectByUsername(username);
        
        // 2. 核心校验逻辑：判断用户是否存在且身份信息（姓名、手机号）是否匹配
        if (user == null || !user.getRealName().equals(realName) || !user.getPhone().equals(phone)) {
            throw new RuntimeException("身份信息校验失败，请核对姓名与手机号");
        }
        
        // 3. 校验账号状态
        if (user.getStatus() == 0) {
            throw new RuntimeException("该账号已被停用，请联系管理员");
        }
        
        // 4. 执行更新
        SysUser updateParam = new SysUser();
        updateParam.setId(user.getId());
        updateParam.setPassword(newPassword); // 建议后续引入 BCrypt 加密
        userMapper.update(updateParam);
    }

    @Override
    @Transactional
    public void resetPassword(Long userId, String oldPassword, String newPassword) {
        // 1. 获取用户信息
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 2. 校验旧密码是否正确
        if (!user.getPassword().equals(oldPassword)) {
            throw new RuntimeException("旧密码输入错误");
        }
        
        // 3. 执行修改
        SysUser updateParam = new SysUser();
        updateParam.setId(userId);
        updateParam.setPassword(newPassword);
        userMapper.update(updateParam);
    }

}