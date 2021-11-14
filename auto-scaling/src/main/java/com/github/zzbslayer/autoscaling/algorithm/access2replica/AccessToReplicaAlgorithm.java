package com.github.zzbslayer.autoscaling.algorithm.access2replica;

import com.github.zzbslayer.autoscaling.entity.History;
import io.fabric8.kubernetes.api.model.apps.Deployment;

import java.util.List;

public interface AccessToReplicaAlgorithm {
    int accessToReplica(Deployment deployment, List<History> histories, int predictionAccess);
}
