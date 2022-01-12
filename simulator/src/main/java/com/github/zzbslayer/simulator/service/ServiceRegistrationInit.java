package com.github.zzbslayer.simulator.service;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ServiceRegistrationInit implements InitializingBean {
    @Autowired
    RedissonClient redissonClient;

    @Override
    public void afterPropertiesSet() throws Exception {
        RBucket<String> bucket = redissonClient.getBucket("vnf-2");
        bucket.set("10.0.0.94"); // svc ip and port of service "vnf-2"
    }
}
