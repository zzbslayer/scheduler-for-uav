package com.github.zzbslayer.simulator.core.latency.prediction.strategies.ema;

import com.github.zzbslayer.simulator.core.latency.prediction.NodeServicePrediction;
import lombok.extern.slf4j.Slf4j;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * Exponential moving average
 * verified but cant prediction
 */
@Slf4j
public class ExponentialMovingAveragePrediction extends NodeServicePrediction {
    private double alpha = 0.8;
    private Map<Integer, Map<Integer, Double>> oldValues;

    public ExponentialMovingAveragePrediction(int nodeNum, int serviceNum) {
        super(nodeNum, serviceNum);

        oldValues= new HashMap();

        for (int i = 0; i < nodeNum; ++i) {
            Map<Integer, Double> temp = new HashMap<>();
            oldValues.put(i, temp);
            for (int j = 0; j < serviceNum; ++j) {
                temp.put(j, null);
            }
        }
    }


    @Override
    protected int predictOne(int node, int service, Deque<Integer> queue) {
        int latestValue = queue.getLast();

        Map<Integer, Double> serviceToOldValue = oldValues.get(node);
        Double value = serviceToOldValue.get(service);
//        if (node == 0)
//            log.info("node {} service {} old value: {}, lastest value: {}", node, service, value, latestValue);
        if (value == null) {
            value = (double) latestValue;
            serviceToOldValue.put(service, (double) latestValue);
        }
        else {
            value = value + alpha * (latestValue - value);
        }
//        if (service == 1)
//            log.info("node {} service {} New value: {}", node, service, value);
        return (int) Math.round(value);
    }
}
