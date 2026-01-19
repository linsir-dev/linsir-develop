package com.linsir.springboot.webflux.controller;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.UUID;

/**
 * @ClassName : SSEController
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-17 10:23
 *
 * WebFlux-Server-Sent Event服务器推送事件
 */

@RestController
public class SSEController {

    @GetMapping(value = "see", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public Flux<ServerSentEvent<String>> see()
    {
        Flux<Long> longFlux = Flux.interval(Duration.ofMillis(1000));
        return longFlux.map(val ->{
                return ServerSentEvent.<String>builder()
                        .id(UUID.randomUUID().toString())
                        .event("event")
                        .data(val.toString())
                        .build();
            });
    }
}
