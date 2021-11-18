package com.github.zzbslayer.vnfdemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/test")
    public int test() {
        for (int i = 0; i < 1000000; i++) {}
        return 1;
    }
}
