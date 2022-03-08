package com.github.zzbslayer.simulator.core.latency.record;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class AccessRecord {
    /**
     * <node, <service, cnt>>
     */
    Map<Integer, Map<Integer, Integer>> accessMap = new HashMap<>();
    int nodeNum;

    public AccessRecord(int nodeNum) {
        this.nodeNum = nodeNum;
        for (int i = 0; i < nodeNum; ++i) {
            accessMap.put(i, new HashMap<>());
        }
    }

    public void reset() {
        for (Map.Entry<Integer, Map<Integer, Integer>> e: accessMap.entrySet()) {
            e.getValue().clear();
        }
    }

    public AccessRecord(Map<Integer, Map<Integer, Integer>> accessMap) {
        this.accessMap = accessMap;
    }

    /**
     * node access service
     * @param node
     * @param service
     */
    public void access(int node, int service) {
        Map<Integer, Integer> temp = accessMap.getOrDefault(node, new HashMap<>());
        temp.put(service, temp.getOrDefault(service, 0) + 1);
        accessMap.put(node, temp);
    }

    public int getAccess(int node, int service) {
        return accessMap.get(node).getOrDefault(service, 0);
    }
}
