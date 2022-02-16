package com.github.zzbslayer.simulator.core.availability.utils;

import com.github.zzbslayer.simulator.core.availability.graph.Graph;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScenarioParamter {
    private static ThreadLocal<Random> randomThreadLocal = new ThreadLocal<Random>() {
        @Override
        protected Random initialValue() {
            return new Random();
        }
    };
    private final static double DEFAULT_EXPECTED_AVE_AVAILABILITY = 0.9;
    private final static int DEFAULT_NODE_NUM = 5;
    private final static int DEFAULT_MASTER_NODE = 2;

    int nodeNum;
    int serviceNum;
    int replicaNum;
    int serviceResource;
    int clusterHead;

    int[][] graph;
    double[] availabilities;
    int[] nodeCapacity;
    int[] failedNode;

    /**
     *  naive version
     *  a service account for 1 capacity
     *  a node with 5 capacity can run 5 services
     */
    static int[] generateNodeCapacity(int nodeNum) {
        int[] nodeCapacity = new int[nodeNum];
        for (int i = 0; i < nodeNum; ++i) {
            nodeCapacity[i] = 5;
        }
        return nodeCapacity;
    }

    static double randomPercent(double expectedAve) {
        // temp = (1 - 0.9)
        // temp * 200 = 20
        // [0, 20) / 100 = [0, 0.2)
        // [0, 0.2] - temp + expected = [0.8, 1.0)
        double temp = 1.0 - expectedAve;
        return (double) randomThreadLocal.get().nextInt((int)(temp * 200)) / 100 - temp + expectedAve;
    }

    static double randomPercent() {
        return (double) randomThreadLocal.get().nextInt(100) / 100;
    }


    static double[] generateNodeAvailability(int nodeNum) {
        return generateNodeAvailability(nodeNum, 0.9);
    }

    static double[] generateNodeAvailability(int nodeNum, double expectedAve) {
        double[] availabilities = new double[nodeNum];
        for (int i = 0; i < nodeNum; ++i) {
            availabilities[i] = randomPercent(expectedAve);
        }
        return availabilities;
    }

    static int[] randomFailure(int[][] graph, double[] availabilities, int master) {
        int nodeNum = graph.length;
        int[] failure = new int[nodeNum];

        int _cnt = 0;
        for (int i = 0; i < nodeNum; ++i) {
            if (i == master)
                continue;
            double percentage = randomPercent();
            //System.out.println( percentage+ ", " + availabilities[i]);
            if (percentage > availabilities[i]) {
                failure[i] = -1;
                _cnt++;
            }
        }
        System.out.println(_cnt);

        for (int i = 0; i < nodeNum; ++i) {
            if (i == master)
                continue;
            if (failure[i] == 0 && !Graph.reachable(graph, failure, i, master)) {
                failure[i] = -2; // not connected
                _cnt++;
            }
        }
        System.out.println(_cnt);
        System.out.println();
        return failure;
    }

    // TODO


    public static ScenarioParamter randomNewInstance() {
        int nodeNum = DEFAULT_NODE_NUM;
        int master = DEFAULT_MASTER_NODE;
        double[] availabilities = generateNodeAvailability(nodeNum, DEFAULT_EXPECTED_AVE_AVAILABILITY);
        int[][] graph = Graph.randomUndirectedGraph(nodeNum);
        return ScenarioParamter.builder()
                .nodeNum(nodeNum)
                .serviceNum(8)
                .replicaNum(1)
                .serviceResource(1)
                .clusterHead(master)
                .graph(graph)
                .availabilities(availabilities)
                .nodeCapacity(generateNodeCapacity(nodeNum))
                .failedNode(randomFailure(graph, availabilities, master))
                .build();
    }

    public static void main(String[] args) {
        double sum = 0;
        int iteration = 100000;
        double max = 0;
        double min = 1;
        for (int i = 0; i < iteration; ++i) {
            double temp = randomPercent(0.9);
            sum += temp;
            max = Math.max(max, temp);
            min = Math.min(min, temp);
        }
        System.out.println(min + ", " + max + ", " + sum / iteration);
    }
}
