package com.linsir.springboot.webflux.service.impl;

import com.linsir.springboot.webflux.entiy.User;
import com.linsir.springboot.webflux.repository.UserRepository;
import com.linsir.springboot.webflux.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

/**
 * @ClassName : UserServiceImpl
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-18 14:46
 */

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private  ReactiveMongoTemplate reactiveMongoTemplate;

    @Autowired
    private  UserRepository userRepository;

    @Override
    public Mono<User> createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Mono<User> findById(String userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Mono<User> updateUser(String userId, User user) {
        return userRepository.findById(userId)
                .flatMap(dbUser->{
                   dbUser.setAge(user.getAge());
                   dbUser.setSalary(user.getSalary());
                   return userRepository.save(dbUser);
                });
    }

    @Override
    public Mono<User> deleteUser(String userId) {
        return userRepository.findById(userId).flatMap(deleteUser->{
            return userRepository.deleteById(deleteUser.getId())
                    .then(Mono.just(deleteUser));
        });
    }

    @Override
    public Flux<User> fetchUsers(String name) {
        Query query = new Query().with(Sort.by(Collections.singletonList(Sort.Order.asc("age"))));
        query.addCriteria(Criteria.where("name").regex(name));
        return reactiveMongoTemplate.find(query, User.class);
    }
}
