package com.github.zzbslayer.simulator.core.latency.record;

import com.github.zzbslayer.simulator.core.availability.graph.Graph;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
public class ServicePlacementRecord {
    int[][] graph;
    // service, node, placement
    Map<Integer, Map<Integer, Integer>> servicePlacement = new HashMap<>();
    int[] nodeWorkLoads;

    public ServicePlacementRecord(int[][] graph, int serviceNum) {
        this.nodeWorkLoads = new int[graph.length];

        this.graph = graph;
        for (int i = 0; i < serviceNum; ++i) {
            servicePlacement.put(i, new HashMap<>());
        }
    }

    public void putServiceAtNode(int service, int node) {
        Map<Integer, Integer> temp = servicePlacement.get(service);
        temp.put(node, temp.getOrDefault(node, 0) + 1);

        this.nodeWorkLoads[node]++;
    }

    public void removeServiceFromNodeRoundRobin(int service, int removeNum) {
        Map<Integer, Integer> temp = servicePlacement.get(service);
        int lastRemoveNum = removeNum;
        while (removeNum > 0) {
            removeNum = _removeServiceFromNodeRoundRobin(temp, removeNum);
            if (removeNum == lastRemoveNum)
                throw new RuntimeException("No service to remove");
        }
    }

    private int _removeServiceFromNodeRoundRobin(Map<Integer, Integer> servicePlacement, int removeNum) {
        for (Map.Entry<Integer, Integer> e: servicePlacement.entrySet()) {
            if (removeNum == 0)
                break;
            int instances = e.getValue();
            if (instances > 0) {
                e.setValue(instances - 1);
                --removeNum;
                --this.nodeWorkLoads[e.getKey()];
            }
        }
        return removeNum; // return remaining
    }

    public int findNearestService(int srcNode, int service) {
        int targetNode = -1;
        int targetDistance = Integer.MAX_VALUE;

        Map<Integer, Integer> temp = servicePlacement.get(service);
        for (Map.Entry<Integer, Integer> e: temp.entrySet()) {
            if (e.getValue() > 0 ) {
                int distance = Graph.calculateDistance(this.graph, srcNode, e.getKey());
                if (distance < targetDistance) {
                    targetNode = e.getKey();
                    targetDistance = distance;
                }
            }
        }
        return targetNode;
    }

    public void printServicePlacement() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (Map.Entry<Integer, Map<Integer, Integer>> e1: servicePlacement.entrySet()) {
            sb.append("service ");
            sb.append(e1.getKey());
            sb.append(" on: ");

            for (Map.Entry<Integer, Integer> e2: e1.getValue().entrySet()) {
                sb.append("node ");
                sb.append(e2.getKey());
                sb.append(" with ");
                sb.append(e2.getValue());
                sb.append(" instances; ");
                //log.info("{} <service {}> on <node {}>", e2.getValue(), e1.getKey(), e2.getKey());
            }
            sb.append("\n");
        }
        log.info(sb.toString());

    }
}
