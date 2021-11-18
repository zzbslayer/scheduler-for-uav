package com.github.zzbslayer.simulator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {
    @Bean("simulator-executor")
    public Executor taskExecutro(){
        ExecutorService executorService = Executors.newCachedThreadPool();
        return executorService;
    }
}
