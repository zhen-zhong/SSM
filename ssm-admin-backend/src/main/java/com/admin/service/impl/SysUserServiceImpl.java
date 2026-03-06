package com.admin.service.impl;

import com.admin.entity.SysUser;
import com.admin.mapper.SysRoleMapper;
import com.admin.mapper.SysUserMapper;
import com.admin.mapper.SysUserRoleMapper;
import com.admin.service.SysUserService;
import com.github.pagehelper.PageHelper; 
import com.github.pagehelper.PageInfo;
import com.admin.utils.JwtUtils;
import com.admin.vo.UserInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.admin.entity.SysRole;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Map<String, Object> listPage(int pageNum, int pageSize) {
        // 1. 开启分页拦截
        PageHelper.startPage(pageNum, pageSize);
        
        // 2. 直接查询。由于 XML 中配置了 collection select，MyBatis 会自动为这 10 个用户拉取角色
        List<SysUser> list = userMapper.listAll(); 
        
        // 3. 将结果交由 PageInfo 处理总页数等计算
        PageInfo<SysUser> pageInfo = new PageInfo<>(list);

        // 4. 拼装纯净的返回结果（不再需要手动 for 循环去查角色了）
        Map<String, Object> result = new HashMap<>();
        result.put("list", pageInfo.getList());
        result.put("total", pageInfo.getTotal());
        result.put("pageNum", pageInfo.getPageNum());
        result.put("pageSize", pageInfo.getPageSize());
        
        return result;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(SysUser user) {
        SysUser existUser = userMapper.selectByUsername(user.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        userMapper.insert(user); 
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