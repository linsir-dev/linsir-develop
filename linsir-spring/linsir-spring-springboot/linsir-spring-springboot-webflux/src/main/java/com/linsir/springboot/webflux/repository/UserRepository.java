package com.linsir.springboot.webflux.repository;

import com.linsir.springboot.webflux.entiy.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @ClassName : UserRepository
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-18 14:43
 */

@Repository
public interface UserRepository extends ReactiveMongoRepository<User,String> {
}
