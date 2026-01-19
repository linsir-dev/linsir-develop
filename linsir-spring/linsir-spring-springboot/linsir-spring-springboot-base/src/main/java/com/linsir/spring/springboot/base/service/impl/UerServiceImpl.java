package com.linsir.spring.springboot.base.service.impl;

import com.linsir.spring.springboot.base.service.RoleService;
import com.linsir.spring.springboot.base.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @ClassName : UerServiceImpl
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-11 03:43
 */
@Service
public class UerServiceImpl implements UserService {

    @Autowired
    private RoleService roleService;

    @Override
    public void doService() {
        System.out.println("dos");
        throw new RuntimeException();
    }


    @Transactional(rollbackFor = Exception.class)
    public void doSS()
    {
        doService();
        roleService.doRoleService();
    }
}
