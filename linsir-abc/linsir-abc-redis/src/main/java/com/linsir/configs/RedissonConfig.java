package com.linsir.configs;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient()
    {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://redis-11744.c270.us-east-1-3.ec2.redns.redis-cloud.com:11744")
                        .setPassword("AcxmzCMgAykYIzysZA7Y7d6b2Rjcekyp");
        return Redisson.create(config);


        //主从
//        Config config = new Config();
//        config.useMasterSlaveServers()
//            .setMasterAddress("redis://127.0.0.1:6379").setPassword("123456")
//            .addSlaveAddress("redis://127.0.0.1:6389")
//            .addSlaveAddress("redis://127.0.0.1:6399");
//        return Redisson.create(config);

        //哨兵
//        Config config = new Config();
//        config.useSentinelServers()
//            .setMasterName("myMaster")
//            .addSentinelAddress("redis://127.0.0.1:6379", "redis://127.0.0.1:6389")
//            .addSentinelAddress("redis://127.0.0.1:6399");
//        return Redisson.create(config);

        //集群
//        Config config = new Config();
//        config.useClusterServers()
//                //cluster state scan interval in milliseconds
//            .setScanInterval(2000)
//            .addNodeAddress("redis://127.0.0.1:6379", "redis://127.0.0.1:6389")
//            .addNodeAddress("redis://127.0.0.1:6399");
//        return Redisson.create(config);
    }

}
