package com.github.zzbslayer.simulator.core.latency.prediction;

import com.github.zzbslayer.simulator.config.LatencyExperimentConfig;
import com.github.zzbslayer.simulator.core.latency.prediction.mapper.AccessInstanceMapper;
import com.github.zzbslayer.simulator.core.latency.record.AccessRecord;

import java.util.*;

public abstract class NodeServicePrediction implements Prediction{
    private final static int HISTORY_SIZE = LatencyExperimentConfig.HISTORY_SIZE;
    Map<Integer, Map<Integer, Deque<Integer>>> nodeServiceAccessHistory;
    int nodeNum;
    int serviceNum;
    AccessInstanceMapper mapper = AccessInstanceMapper.getMapper();

    public NodeServicePrediction() {};

    /**
     * Init node service access history
     * @param nodeNum
     * @param serviceNum
     */
    public NodeServicePrediction(int nodeNum, int serviceNum) {
        this.nodeNum = nodeNum;
        this.serviceNum = serviceNum;

        nodeServiceAccessHistory= new HashMap();

        for (int i = 0; i < nodeNum; ++i) {
            Map<Integer, Deque<Integer>> temp = new HashMap<>();
            nodeServiceAccessHistory.put(i, temp);
            for (int j = 0; j < serviceNum; ++j) {
                temp.put(j, new LinkedList<>());
            }
        }
    }

    protected void predictionDataPreprocess(AccessRecord currentCycleAccess) {
        for (Map.Entry<Integer, Map<Integer, Deque<Integer>>> e1: nodeServiceAccessHistory.entrySet()) {
            int node = e1.getKey();
            for (Map.Entry<Integer, Deque<Integer>> e2: e1.getValue().entrySet()) {
                int service = e2.getKey();
                Queue<Integer> history = e2.getValue();

                int access = currentCycleAccess.getAccess(node, service);
                history.offer(access);

                if (history.size() > HISTORY_SIZE)
                    history.poll();
            }
        }
    }

    protected abstract int predictOne(int node, int service, Deque<Integer> queue);

    protected int[] predictAll() {
        // node, service, cnt
        Map<Integer, Map<Integer, Integer>> map = new HashMap<>();

        for (Map.Entry<Integer, Map<Integer, Deque<Integer>>> e1: nodeServiceAccessHistory.entrySet()) {
            int node = e1.getKey();
            for (Map.Entry<Integer, Deque<Integer>> e2 : e1.getValue().entrySet()) {
                int service = e2.getKey();
                Deque<Integer> history = e2.getValue();
                int predictionResult = predictOne(node, service, history);

                Map<Integer, Integer> temp = map.getOrDefault(node, new HashMap<>());
                temp.put(service, predictionResult);
                map.put(node, temp);
            }
        }
        int[] placement = new int[this.serviceNum];

        for (Map.Entry<Integer, Map<Integer, Integer>> e1: map.entrySet()) {
            int node = e1.getKey();
            for (Map.Entry<Integer, Integer> e2 : e1.getValue().entrySet()) {
                int service = e2.getKey();
                int cnt = e2.getValue();
                placement[service] += cnt;
            }
        }
        // map access cnt to instance num
        for (int i = 0; i < placement.length; ++i) {
            placement[i] = mapper.accessToInstance(placement[i]);
        }
        return placement;
    }

    public int[] predict(AccessRecord currentCycleAccess) {
        predictionDataPreprocess(currentCycleAccess);
        return predictAll();
    }
}
