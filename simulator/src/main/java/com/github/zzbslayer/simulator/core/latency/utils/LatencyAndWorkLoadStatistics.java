package com.github.zzbslayer.simulator.core.latency.utils;

import com.github.zzbslayer.simulator.core.latency.record.AccessRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class LatencyAndWorkLoadStatistics {
    int hopsSum;
    int accessCnt;

    int machineWorkload; // how many services running on it
    int timeIntervalCnt;

    public void addHop(int hop) {
        hopsSum += hop;
        ++accessCnt;
    }

    public void addMachineWorkLoad(int[] nodeload) {
        for (int i: nodeload) {
            machineWorkload += i;
        }
        ++timeIntervalCnt;
    }

    public void print() {
        log.info("Access count: {}", accessCnt);
        log.info("Average hop: {}", Double.valueOf(hopsSum) / accessCnt);

        log.info("Time interval cnt: {}", this.timeIntervalCnt);
        log.info("Average machine workload: {}", Double.valueOf(hopsSum) / accessCnt);
    }
}
