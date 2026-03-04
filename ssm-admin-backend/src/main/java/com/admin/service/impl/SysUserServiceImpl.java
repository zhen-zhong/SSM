package com.admin.service.impl;

import com.admin.entity.SysUser;
import com.admin.mapper.SysRoleMapper;
import com.admin.mapper.SysUserMapper;
import com.admin.mapper.SysUserRoleMapper;
import com.admin.service.SysUserService;
import com.admin.utils.JwtUtils;
import com.admin.vo.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysRoleMapper roleMapper;

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
    public UserInfoVO getUserInfo(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在或已被删除");
        }

        List<String> roleCodes = roleMapper.selectRoleCodesByUserId(userId);

        UserInfoVO vo = new UserInfoVO();
        vo.setUserId(user.getId());
        vo.setUserName(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setRoles(roleCodes != null && !roleCodes.isEmpty() ? roleCodes : new ArrayList<>());

        return vo;
    }

    /**
     * 修改后的注册逻辑：支持同步保存角色关联
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(SysUser user) {
        SysUser existUser = userMapper.selectByUsername(user.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 2. 设置初始状态
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        
        // 3. 插入用户基本信息
        userMapper.insert(user); 
        
        // 4. 处理角色关联
        Long userId = user.getId(); 
        if (user.getRoleIds() != null && !user.getRoleIds().isEmpty()) {
            userRoleMapper.batchInsert(userId, user.getRoleIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserInfo(SysUser user) {
        SysUser existUser = userMapper.selectById(user.getId());
        if (existUser == null) {
            throw new RuntimeException("修改失败：用户不存在");
        }

        if (user.getUsername() != null && !user.getUsername().equals(existUser.getUsername())) {
            if (userMapper.selectByUsername(user.getUsername()) != null) {
                throw new RuntimeException("修改失败：用户名已存在");
            }
        }
        userMapper.update(user);

        // 🌟 如果修改用户时也传了 roleIds，则同步更新角色关联
        if (user.getRoleIds() != null) {
            userRoleMapper.deleteByUserId(user.getId());
            if (!user.getRoleIds().isEmpty()) {
                userRoleMapper.batchInsert(user.getId(), user.getRoleIds());
            }
        }
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
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("分配角色失败：目标用户不存在");
        }
        userRoleMapper.deleteByUserId(userId);
        if (roleIds != null && !roleIds.isEmpty()) {
            try {
                userRoleMapper.batchInsert(userId, roleIds);
            } catch (Exception e) {
                throw new RuntimeException("角色分配执行失败，数据库写入异常");
            }
        }
    }

    @Override
    public void deleteUser(Long id) {
        SysUser userToDelete = userMapper.selectById(id);
        
        if (userToDelete != null && "admin".equalsIgnoreCase(userToDelete.getUsername())) {
            throw new RuntimeException("操作失败：系统内置超级管理员账号禁止删除！");
        }
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
    @Transactional 
    public void findPassword(String username, String realName, String phone, String newPassword) {
        SysUser user = userMapper.selectByUsername(username);
        if (user == null || !user.getRealName().equals(realName) || !user.getPhone().equals(phone)) {
            throw new RuntimeException("身份信息校验失败，请核对姓名与手机号");
        }
        if (user.getStatus() == 0) {
            throw new RuntimeException("该账号已被停用，请联系管理员");
        }
        SysUser updateParam = new SysUser();
        updateParam.setId(user.getId());
        updateParam.setPassword(newPassword); 
        userMapper.update(updateParam);
    }

    @Override
    @Transactional
    public void resetPassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!user.getPassword().equals(oldPassword)) {
            throw new RuntimeException("旧密码输入错误");
        }
        SysUser updateParam = new SysUser();
        updateParam.setId(userId);
        updateParam.setPassword(newPassword);
        userMapper.update(updateParam);
    }
}