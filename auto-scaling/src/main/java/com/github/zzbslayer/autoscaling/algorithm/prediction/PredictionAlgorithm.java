package com.github.zzbslayer.autoscaling.algorithm.prediction;

import com.github.zzbslayer.autoscaling.entity.History;
import io.fabric8.kubernetes.api.model.apps.Deployment;

import java.util.List;

public interface PredictionAlgorithm {
    int predictAccess(Deployment deployment, List<History> histories);
}
