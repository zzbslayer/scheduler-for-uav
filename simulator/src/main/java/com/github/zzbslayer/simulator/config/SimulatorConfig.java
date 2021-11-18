package com.github.zzbslayer.simulator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimulatorConfig {
    @Value("${simulator.dataset}")
    public String DATASET_PATH;

    @Value("${simulator.host}")
    public String HOST;
}
