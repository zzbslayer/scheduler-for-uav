package com.github.zzbslayer.simulator.core.availability.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class ServiceFailureStatistics {
    /**
     * TODO standard deviation of service placement
     */

    int servicePlacementSum;
    int servicePlacementSquaresSum;

    int instance;
    int failedInstance;
    int node;
    int failedNode;
    int times = 0;

    public double getFailRate() {
        return ((double) failedInstance) / instance;
    }

    /**
     * standard devication = sqrt( ave(x^2) - ave(x)^2 )
     * @return
     */
    public double getStandardDeviationOfServicePlacement() {
        double squareAve = 1.0 * servicePlacementSquaresSum / node;
        double ave = 1.0 * servicePlacementSum / node;
        return Math.sqrt(squareAve - ave*ave);
    }

    public void add(ServiceFailureStatistics serviceFailureStatistics) {
        this.servicePlacementSum += serviceFailureStatistics.getServicePlacementSum();
        this.servicePlacementSquaresSum += serviceFailureStatistics.getServicePlacementSquaresSum();

        this.instance += serviceFailureStatistics.getInstance();
        this.failedInstance += serviceFailureStatistics.getFailedInstance();
        this.node += serviceFailureStatistics.getNode();
        this.failedNode += serviceFailureStatistics.getFailedNode();
        this.times++;
    }

    public void print() {
//        log.info("times: {}", this.getTimes());
//        log.info("instance num: {}", this.getInstance());
//        log.info("fail instance num: {}", this.getFailedInstance());
//        log.info("node num: {}", this.getNode());
        log.info("fail node num: {}", this.getFailedNode());

        log.info("fail rate: {}", this.getFailRate());
        log.info("standarad deviation of service placement: {}", this.getStandardDeviationOfServicePlacement());

        System.out.println();
    }
}
