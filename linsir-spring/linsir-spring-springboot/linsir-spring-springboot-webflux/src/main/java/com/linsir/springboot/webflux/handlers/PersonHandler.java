package com.linsir.springboot.webflux.handlers;

import com.linsir.springboot.webflux.entiy.Person;
import com.linsir.springboot.webflux.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @ClassName : PersonHandler
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-17 01:38
 */

@Component
public class PersonHandler {

    @Autowired
    private PersonRepository personRepository;

    public Mono<ServerResponse> getAllPeople(ServerRequest request) {
        return ServerResponse.ok().body(personRepository.findAll(), Person.class);
    }

    public Mono<ServerResponse> getPersonById(ServerRequest request) {
        Long personId = Long.valueOf(request.pathVariable("id"));
        return personRepository.findById(personId)
                .flatMap(person -> ServerResponse.ok().bodyValue(person))
                .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND).build());
    }

}
