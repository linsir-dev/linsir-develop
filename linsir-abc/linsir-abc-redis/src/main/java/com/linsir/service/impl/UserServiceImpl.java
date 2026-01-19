package com.linsir.service.impl;

import com.linsir.entity.User;
import com.linsir.repository.UserRepository;
import com.linsir.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public User getUserById(int id) {
        return userRepository.findById(id).get();
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }


}
