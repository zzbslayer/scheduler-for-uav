package com.github.zzbslayer.gateway.service;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InitService implements InitializingBean {
    @Autowired
    RedissonClient redissonClient;

    @Override
    public void afterPropertiesSet() throws Exception {
        RBucket<String> s1 = redissonClient.getBucket("vnf-2");
        s1.set("10.108.99.208:8080");

        RBucket<int[][]> graph = redissonClient.getBucket("graph");

    }
}
