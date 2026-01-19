package com.linsir.service;

import com.linsir.entity.User;

public interface UserService {

     void save(User user);

     User getUserById(int id);

     User getUserByUsername(String username);

}
