package com.linsir.security.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linsir.security.entity.Role;
import com.linsir.security.mapper.RoleMapper;
import com.linsir.security.service.RoleService;
import org.springframework.stereotype.Service;

/**
 * 角色 Service 实现类
 * 继承 ServiceImpl 获得通用 Service 实现
 *
 * @author linsir
 * @version 1.0.0
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

}
