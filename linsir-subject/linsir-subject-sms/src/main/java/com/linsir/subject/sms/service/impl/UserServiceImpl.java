package com.linsir.subject.sms.service.impl;

import com.linsir.subject.sms.entity.User;
import com.linsir.subject.sms.mapper.UserMapper;
import com.linsir.subject.sms.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author linsir
 * @since 2023-12-12
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
