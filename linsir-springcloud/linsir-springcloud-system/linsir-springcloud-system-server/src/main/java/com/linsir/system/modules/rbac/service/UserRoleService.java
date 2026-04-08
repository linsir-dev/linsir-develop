package com.linsir.system.modules.rbac.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linsir.system.modules.rbac.entity.UserRole;

import java.util.List;

/**
 * 用户角色关联 Service 接口
 *
 * @author linsir
 * @version 1.0.0
 */
public interface UserRoleService extends IService<UserRole> {

    /**
     * 根据用户ID查询角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> listRoleIdsByUserId(Long userId);

    /**
     * 根据角色ID查询用户ID列表
     *
     * @param roleId 角色ID
     * @return 用户ID列表
     */
    List<Long> listUserIdsByRoleId(Long roleId);

    /**
     * 分配用户角色
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     * @return 是否成功
     */
    boolean assignRoles(Long userId, List<Long> roleIds);
}
