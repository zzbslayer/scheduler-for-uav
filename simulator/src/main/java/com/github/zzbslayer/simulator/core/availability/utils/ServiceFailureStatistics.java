package com.github.zzbslayer.simulator.core.availability.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceFailureStatistics {
    int instance;
    int failedInstance;
    int node;
    int failedNode;

    public double getFailRate() {
        return ((double) failedInstance) / instance;
    }

    public void add(ServiceFailureStatistics serviceFailureStatistics) {
        this.instance += serviceFailureStatistics.getInstance();
        this.failedInstance += serviceFailureStatistics.getFailedInstance();
        this.node += serviceFailureStatistics.getNode();
        this.failedNode += serviceFailureStatistics.getFailedNode();
    }
}
