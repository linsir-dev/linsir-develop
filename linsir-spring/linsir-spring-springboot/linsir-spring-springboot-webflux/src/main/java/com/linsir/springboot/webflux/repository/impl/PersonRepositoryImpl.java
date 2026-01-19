package com.linsir.springboot.webflux.repository.impl;

import com.linsir.springboot.webflux.entiy.Person;
import com.linsir.springboot.webflux.repository.PersonRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName : PersonRepositoryImpl
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-16 10:07
 */

@Repository
public class PersonRepositoryImpl implements PersonRepository {

    private final Map<Long,Person> peoples = new HashMap<>();

    public PersonRepositoryImpl() {
        // 初始化模拟数据
        peoples.put(1L, new Person(1L, "Alice", 30));
        peoples.put(2L, new Person(2L, "Bob", 35));
        peoples.put(3L, new Person(3L, "Charlie", 40));
    }

    @Override
    public Flux<Person> findAll() {
        return Flux.fromIterable(peoples.values());
    }

    @Override
    public Mono<Person> findById(Long id) {
        return Mono.justOrEmpty(peoples.get(id));
    }
}
