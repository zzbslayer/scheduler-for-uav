package com.github.zzbslayer.simulator.core.latency.prediction;

import com.github.zzbslayer.simulator.core.latency.record.AccessRecord;

import java.util.*;

public class Prediction {
    private final static int HISTORY_SIZE = 10;
    Map<Integer, Map<Integer, Queue<Integer>>> nodeServiceAccessHistory;

    public Prediction(int nodeNum, int serviceNum) {
        nodeServiceAccessHistory= new HashMap();

        for (int i = 0; i < nodeNum; ++i) {
            Map<Integer, Queue<Integer>> temp = new HashMap<>();
            nodeServiceAccessHistory.put(i, temp);
            for (int j = 0; j < serviceNum; ++j) {
                temp.put(j, new LinkedList<>());
            }
        }
    }

    private void predictionDataPreprocess(AccessRecord currentCycleAccess) {
        for (Map.Entry<Integer, Map<Integer, Queue<Integer>>> e1: nodeServiceAccessHistory.entrySet()) {
            int node = e1.getKey();
            for (Map.Entry<Integer, Queue<Integer>> e2: e1.getValue().entrySet()) {
                int service = e2.getKey();
                Queue<Integer> history = e2.getValue();

                int access = currentCycleAccess.getAccess(node, service);
                history.offer(access);

                if (history.size() > HISTORY_SIZE)
                    history.poll();
            }
        }
    }

    private int predictOne(Queue<Integer> queue) {
        return queue.peek();
    }

    private AccessRecord predictAll() {
        Map<Integer, Map<Integer, Integer>> map = new HashMap<>();

        for (Map.Entry<Integer, Map<Integer, Queue<Integer>>> e1: nodeServiceAccessHistory.entrySet()) {
            int node = e1.getKey();
            for (Map.Entry<Integer, Queue<Integer>> e2 : e1.getValue().entrySet()) {
                int service = e2.getKey();
                Queue<Integer> history = e2.getValue();
                int predictionResult = predictOne(history);

                Map<Integer, Integer> temp = map.getOrDefault(node, new HashMap<>());
                temp.put(service, predictionResult);
                map.put(node, temp);
            }
        }
        AccessRecord res = new AccessRecord(map);
        return res;
    }

    public AccessRecord predict(AccessRecord currentCycleAccess) {
        predictionDataPreprocess(currentCycleAccess);
        AccessRecord res = predictAll();
        return res;
    }
}
