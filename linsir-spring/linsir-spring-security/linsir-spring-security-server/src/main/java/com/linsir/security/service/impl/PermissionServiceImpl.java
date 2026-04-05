package com.linsir.security.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linsir.security.entity.Permission;
import com.linsir.security.mapper.PermissionMapper;
import com.linsir.security.service.PermissionService;
import org.springframework.stereotype.Service;

/**
 * 权限 Service 实现类
 * 继承 ServiceImpl 获得通用 Service 实现
 *
 * @author linsir
 * @version 1.0.0
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

}
