package com.linsir.system.modules.rbac.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linsir.system.modules.rbac.entity.Role;
import com.linsir.system.modules.rbac.mapper.RoleMapper;
import com.linsir.system.modules.rbac.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色 Service 实现类
 *
 * @author linsir
 * @version 1.0.0
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Override
    public Role getByRoleCode(String roleCode) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleCode, roleCode);
        return getOne(wrapper);
    }

    @Override
    public List<Role> listByUserId(Long userId) {
        // 通过关联表查询，这里先返回空列表，后续实现
        return List.of();
    }
}
