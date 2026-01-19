package com.linsir.springboot.webflux.repository;

import com.linsir.springboot.webflux.entiy.Person;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @ClassName : PersonRepository
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-16 10:05
 */

public interface PersonRepository {

    Flux<Person> findAll();

    Mono<Person> findById(Long id);
}
