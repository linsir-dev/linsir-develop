package com.linsir.springboot.webflux.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @ClassName : SyncController
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-17 09:45
 */
@RestController
public class SyncController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @GetMapping("sync")
    public String sync() {
        logger.info("sync method start");
        String result = this.execute();
        logger.info("sync method end");
        return result;
    }

    @GetMapping("async/mono")
    public Mono<String> asyncMono() {
        logger.info("async method start");
        Mono<String> result = Mono.fromSupplier(this::execute);
        logger.info("async method end");
        return result;
    }



    private String execute() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "hello";
    }
}
