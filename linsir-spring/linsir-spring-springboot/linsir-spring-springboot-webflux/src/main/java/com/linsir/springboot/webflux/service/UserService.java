package com.linsir.springboot.webflux.service;

import com.linsir.springboot.webflux.entiy.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @ClassName : UserService
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-18 14:45
 */

public interface UserService {

   Mono<User> createUser(User user);

   Flux<User> getAllUsers();

   Mono<User> findById(String userId);

   Mono<User> updateUser(String userId,User user);

   Mono<User> deleteUser(String userId);

   Flux<User> fetchUsers(String name);
}
