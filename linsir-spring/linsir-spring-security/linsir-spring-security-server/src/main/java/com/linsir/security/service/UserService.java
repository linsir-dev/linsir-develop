package com.linsir.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linsir.security.entity.Role;
import com.linsir.security.entity.User;

import java.util.List;

/**
 * 用户 Service 接口
 * 继承 IService 获得通用 Service 方法
 *
 * @author linsir
 * @version 1.0.0
 */
public interface UserService extends IService<User> {

    /**
     * 获取用户的角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> getUserRoles(Long userId);

    /**
     * 给用户分配角色
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     */
    void assignRoles(Long userId, List<Long> roleIds);
}
