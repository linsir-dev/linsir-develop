package com.linsir.security.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linsir.security.entity.RolePermission;
import com.linsir.security.mapper.RolePermissionMapper;
import com.linsir.security.service.RolePermissionService;
import org.springframework.stereotype.Service;

/**
 * 角色权限关联 Service 实现类
 * 继承 ServiceImpl 获得通用 Service 实现
 *
 * @author linsir
 * @version 1.0.0
 */
@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionService {

}
