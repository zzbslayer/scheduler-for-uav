package com.github.zzbslayer.vnfdemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration("app-config")
public class AppConfig {
    @Value("${application.name}")
    public String APP_NAME;
}
