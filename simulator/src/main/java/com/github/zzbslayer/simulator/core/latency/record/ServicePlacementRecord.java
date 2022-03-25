package com.github.zzbslayer.simulator.core.latency.record;

import com.github.zzbslayer.simulator.core.availability.graph.Graph;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
public class ServicePlacementRecord {
    int serviceNum;
    int[][] graph;
    // service, node, placement
    Map<Integer, Map<Integer, Integer>> servicePlacement = new HashMap<>();
    int[] nodeWorkLoads;
    int[] serviceInstancePlacement;


    public ServicePlacementRecord(int[][] graph, int serviceNum) {
        this.serviceNum = serviceNum;
        this.nodeWorkLoads = new int[graph.length];
        this.serviceInstancePlacement = new int[this.serviceNum];

        this.graph = graph;
        for (int i = 0; i < serviceNum; ++i) {
            servicePlacement.put(i, new HashMap<>());
        }
    }

    public void calculateActualServicePlacement() {
        for (Map.Entry<Integer, Map<Integer, Integer>> e: this.servicePlacement.entrySet()) {
            int service = e.getKey();
            this.serviceInstancePlacement[service] = getServiceInstanceNum(service);
        }
    }

    public int getServiceInstanceNum(int service) {
        Map<Integer, Integer> nodeInstanceNum = servicePlacement.get(service);
        //nodeInstanceNum.entrySet().stream().forEach(e -> log.info("(key, value): ({}, {})", e.getKey(), e.getValue()));
        return nodeInstanceNum.entrySet().stream().map(e -> e.getValue()).reduce(0, (a, b) -> a + b);
    }

    public void putServiceAtNode(int service, int node) {
        Map<Integer, Integer> temp = servicePlacement.get(service);
        temp.put(node, temp.getOrDefault(node, 0) + 1);

        this.nodeWorkLoads[node]++;
    }

    /**
     * let this class to decide which node to remove
     * maybe we should create another class to do this
     * @param service
     * @param removeNum
     */
    public void removeServiceFromNodeRoundRobin(int service, int removeNum) {
        if (removeNum < 0)
            throw new RuntimeException("Illegal parameter");

        // node, instance num
        Map<Integer, Integer> temp = servicePlacement.get(service);
        int lastRemoveNum = removeNum;
        while (lastRemoveNum > 0) {
            removeNum = _removeServiceFromNodeRoundRobin(temp, lastRemoveNum);

            if (removeNum == lastRemoveNum)
                throw new RuntimeException("No service to remove");
            lastRemoveNum = removeNum;
        }
    }

    private int _removeServiceFromNodeRoundRobin(Map<Integer, Integer> servicePlacement, int removeNum) {
        // node, instance num
        for (Map.Entry<Integer, Integer> e: servicePlacement.entrySet()) {
            if (removeNum == 0)
                break;
            int node = e.getKey();

            int instances = e.getValue();
            if (instances > 0) {
                e.setValue(instances - 1);
                --removeNum;
                this.nodeWorkLoads[node] = this.nodeWorkLoads[node] - 1;
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
