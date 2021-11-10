package com.github.zzbslayer.autoscaling.service;

import org.springframework.stereotype.Service;

@Service
public class PredictionService {
    public int getExpectedRelica(String deploymentName) {
        return 1;
    }
}
