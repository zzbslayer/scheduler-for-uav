package com.github.zzbslayer.simulator.core.latency.record;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
public class AccessRecord {
    /**
     * <node, <service, cnt>>
     */
    Map<Integer, Map<Integer, Integer>> nodeServiceAccessMap = new HashMap<>();
    int nodeNum;
    int serviceNum;

    public AccessRecord(int nodeNum, int serviceNum) {
        this.nodeNum = nodeNum;
        this.serviceNum = serviceNum;
        for (int i = 0; i < nodeNum; ++i) {
            Map<Integer, Integer> map = new HashMap<>();
            for (int j = 0; j < serviceNum; ++j) {
                map.put(j, 0);
            }
            nodeServiceAccessMap.put(i, map);

        }
    }

    public void copyFrom(AccessRecord accessRecord) {
        Map<Integer, Map<Integer, Integer>> map = accessRecord.getNodeServiceAccessMap();
        for (Map.Entry<Integer, Map<Integer, Integer>> e1: map.entrySet()) {
            for (Map.Entry<Integer, Integer> e2: e1.getValue().entrySet()) {
                this.nodeServiceAccessMap.get(e1.getKey()).put(e2.getKey(), e2.getValue());
            }
        }
    }

    public Map<Integer, Integer> getServiceAccessMap() {
        Map<Integer, Integer> res = new HashMap<>();
        nodeServiceAccessMap.entrySet().stream().forEach(e1 -> {
            Map<Integer, Integer> subServiceAccessMap = e1.getValue();
            subServiceAccessMap.entrySet().stream().forEach(e2 -> {
                int svc = e2.getKey();
                res.put(svc, res.getOrDefault(svc, 0) + e2.getValue());
            });
        });
        return res;
    }

    public void printServiceAccessMap(String title) {
        Map<Integer, Integer> map = getServiceAccessMap();
        StringBuilder sb = new StringBuilder();
        sb.append(title);
        sb.append(": {");
        map.entrySet().stream().forEach(e -> {
            sb.append(e.getKey());
            sb.append(": ");
            sb.append(e.getValue());
            sb.append(", ");
        });
        sb.append("}");
        log.info(sb.toString());
    }

    public void reset() {
        for (Map.Entry<Integer, Map<Integer, Integer>> e1: nodeServiceAccessMap.entrySet()) {
            e1.getValue().entrySet().stream().forEach(e2 -> e2.setValue(0));
        }
    }

    public AccessRecord(Map<Integer, Map<Integer, Integer>> nodeServiceAccessMap) {
        this.nodeServiceAccessMap = nodeServiceAccessMap;
    }

    /**
     * node access service
     * @param node
     * @param service
     */
    public void access(int node, int service) {
        Map<Integer, Integer> temp = nodeServiceAccessMap.getOrDefault(node, new HashMap<>());
        temp.put(service, temp.getOrDefault(service, 0) + 1);
        nodeServiceAccessMap.put(node, temp);
    }

    public int getAccess(int node, int service) {
        return nodeServiceAccessMap.get(node).getOrDefault(service, 0);
    }

    public int getAccess(int service) {
        int res = 0;
        for (Map.Entry<Integer, Map<Integer, Integer>> e1: nodeServiceAccessMap.entrySet()) {
            res += e1.getValue().getOrDefault(service, 0);
        }
        return res;
    }
}
