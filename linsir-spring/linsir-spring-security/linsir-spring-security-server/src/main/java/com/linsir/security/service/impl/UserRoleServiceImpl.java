package com.linsir.security.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linsir.security.entity.UserRole;
import com.linsir.security.mapper.UserRoleMapper;
import com.linsir.security.service.UserRoleService;
import org.springframework.stereotype.Service;

/**
 * 用户角色关联 Service 实现类
 * 继承 ServiceImpl 获得通用 Service 实现
 *
 * @author linsir
 * @version 1.0.0
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

}
