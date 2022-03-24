package com.github.zzbslayer.simulator.core.latency.prediction.nsprediction;

import com.github.zzbslayer.simulator.core.latency.prediction.NodeServicePrediction;

import java.util.Deque;

public class LastValuePrediction extends NodeServicePrediction {

    public LastValuePrediction(int nodeNum, int serviceNum) {
        super(nodeNum, serviceNum);
    }

    @Override
    protected int predictOne(int node, int service, Deque<Integer> queue) {
        return queue.getLast();
    }
}
