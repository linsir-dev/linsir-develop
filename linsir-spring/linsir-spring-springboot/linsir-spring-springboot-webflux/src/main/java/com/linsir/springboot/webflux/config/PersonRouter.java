package com.linsir.springboot.webflux.config;

import com.linsir.springboot.webflux.handlers.PersonHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

/**
 * @ClassName : PersonRouter
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-17 01:41
 */
@Configuration
public class PersonRouter {

    @Autowired
    private PersonHandler personHandler;

    @Bean
    public RouterFunction<ServerResponse> route() {
        return RouterFunctions
                .route(GET("/people").and(accept(APPLICATION_JSON)), personHandler::getAllPeople)
                .andRoute(GET("/people/{id}").and(accept(APPLICATION_JSON)), personHandler::getPersonById);
    }

}
