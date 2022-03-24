package com.github.zzbslayer.simulator.core.latency.prediction;

import com.github.zzbslayer.simulator.config.LatencyExperimentConfig;
import com.github.zzbslayer.simulator.core.latency.prediction.mapper.AccessInstanceMapper;
import com.github.zzbslayer.simulator.core.latency.record.AccessRecord;

import java.util.*;

public abstract class ServicePrediction implements Prediction{
    private final static int HISTORY_SIZE = LatencyExperimentConfig.HISTORY_SIZE;
    Map<Integer, Deque<Integer>> serviceAccessHistory;
    int nodeNum;
    int serviceNum;
    AccessInstanceMapper mapper = AccessInstanceMapper.getMapper();

    public ServicePrediction() {};

    /**
     * Init node service access history
     * @param nodeNum
     * @param serviceNum
     */
    public ServicePrediction(int nodeNum, int serviceNum) {
        this.nodeNum = nodeNum;
        this.serviceNum = serviceNum;
        serviceAccessHistory= new HashMap();

        for (int j = 0; j < serviceNum; ++j) {
            serviceAccessHistory.put(j, new LinkedList<>());
        }
    }

    protected void predictionDataPreprocess(AccessRecord currentCycleAccess) {
        for (Map.Entry<Integer, Deque<Integer>> e1: serviceAccessHistory.entrySet()) {
            int service = e1.getKey();
            Deque<Integer> history = e1.getValue();

            int access = currentCycleAccess.getAccess(service);
            history.offer(access);

            if (history.size() > HISTORY_SIZE)
                history.poll();
        }
    }

    protected abstract int predictOne(int service, Deque<Integer> queue);

    protected int[] predictAll() {
        int[] expectedServicePlacementOfFuture = new int[this.serviceNum];

        for (Map.Entry<Integer, Deque<Integer>> e: serviceAccessHistory.entrySet()) {
            int service = e.getKey();
            Deque<Integer> history = e.getValue();
            int predictionResult = predictOne(service, history);

            expectedServicePlacementOfFuture[service] = mapper.accessToInstance(predictionResult);
        }

        return expectedServicePlacementOfFuture;
    }

    public int[] predict(AccessRecord currentCycleAccess) {
        predictionDataPreprocess(currentCycleAccess);
        return predictAll();
    }
}
