package com.linsir.security.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linsir.security.entity.User;
import com.linsir.security.mapper.UserMapper;
import com.linsir.security.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 用户 Service 实现类
 * 继承 ServiceImpl 获得通用 Service 实现
 *
 * @author linsir
 * @version 1.0.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
