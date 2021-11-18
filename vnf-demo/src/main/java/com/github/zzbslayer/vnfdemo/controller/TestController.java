package com.github.zzbslayer.vnfdemo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController implements InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {
      log.info("TestController registered.");
    }

    @GetMapping("/test")
    public int test() {
        for (int i = 0; i < 1000000; i++) {}
        return 1;
    }
}
