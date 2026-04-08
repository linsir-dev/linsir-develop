package com.linsir.system.modules.rbac.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linsir.system.modules.rbac.entity.Role;

import java.util.List;

/**
 * 角色 Service 接口
 *
 * @author linsir
 * @version 1.0.0
 */
public interface RoleService extends IService<Role> {

    /**
     * 根据角色编码查询角色
     *
     * @param roleCode 角色编码
     * @return 角色信息
     */
    Role getByRoleCode(String roleCode);

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> listByUserId(Long userId);
}
