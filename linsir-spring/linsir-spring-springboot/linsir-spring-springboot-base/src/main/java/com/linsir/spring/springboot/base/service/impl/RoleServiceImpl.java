package com.linsir.spring.springboot.base.service.impl;

import com.linsir.spring.springboot.base.service.RoleService;
import org.springframework.stereotype.Service;

/**
 * @ClassName : RoleServiceImpl
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-11 03:42
 */
@Service
public class RoleServiceImpl implements RoleService {
    @Override
    public void doRoleService() {
        System.out.println("doRole");
    }
}
