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
    public final static double PROBABILITY_RANGE = 0.05;
    private static ThreadLocal<Random> randomThreadLocal = new ThreadLocal<Random>() {
        @Override
        protected Random initialValue() {
            return new Random();
        }
    };
    private final static double DEFAULT_EXPECTED_AVE_AVAILABILITY = 0.6;
    private final static int DEFAULT_NODE_NUM = 8;
    private final static int DEFAULT_NODE_CAPACITY = 10;
    private final static int DEFAULT_SERVICE_NUM = 10;
    private final static int DEFAULT_MASTER_NODE = 2;

    int nodeNum;
    int serviceNum;
    int serviceResource;
    int clusterHead;

    int[][] graph;
    double[] availabilities;
    int[] nodeCapacity;
    int[] failedNode;

    double expectedAvailability;
    double availabilityRange;

    /**
     *  naive version
     *  a service account for 1 capacity
     *  a node with 5 capacity can run 5 serviceswan
     */
    static int[] generateNodeCapacity(int nodeNum, int nodeCapacity) {
        int[] nodeCapacities = new int[nodeNum];
        for (int i = 0; i < nodeNum; ++i) {
            nodeCapacities[i] = nodeCapacity;
        }
        return nodeCapacities;
    }

    static double randomPercent(double expectedAve) {
        /**
         * input expectedAve
         * output random double around [expectedAve - range, expectedAve + range]
         */
        double randomDouble = randomThreadLocal.get().nextDouble() * PROBABILITY_RANGE * 2; // from 0 to range*2
        double res = expectedAve - PROBABILITY_RANGE + randomDouble;
        //System.out.println("??: " + res);
        return res;
    }

    static double randomPercent() {
        return (double) randomThreadLocal.get().nextInt(100) / 100;
    }


    static double[] generateNodeAvailability(int nodeNum) {
        return generateNodeAvailability(nodeNum, 0.9);
    }

    static double[] generateNodeAvailability(int nodeNum, double expectedAve) {
        double[] availabilities = new double[nodeNum];
        if (expectedAve == 1) {
            Arrays.fill(availabilities, 1);
        }
        else {
            for (int i = 0; i < nodeNum; ++i) {
                availabilities[i] = randomPercent(expectedAve);
            }
        }

        return availabilities;
    }

    static int[] randomFailure(int[][] graph, double[] availabilities, int master) {
        int nodeNum = graph.length;
        int[] failure = new int[nodeNum];

        for (int i = 0; i < nodeNum; ++i) {
            if (i == master)
                continue;
            double percentage = randomPercent();
            //System.out.println( percentage+ ", " + availabilities[i]);
            if (percentage > availabilities[i]) {
                failure[i] = -1;
            }
        }

        for (int i = 0; i < nodeNum; ++i) {
            if (i == master)
                continue;
            if (failure[i] == 0 && !Graph.reachable(graph, failure, i, master)) {
                failure[i] = -2; // not connected
            }
        }
        return failure;
    }

    // TODO


    public static ScenarioParamter randomNewInstance() {
        return randomNewInstance(DEFAULT_NODE_NUM, DEFAULT_NODE_CAPACITY, DEFAULT_SERVICE_NUM, DEFAULT_EXPECTED_AVE_AVAILABILITY);
    }

    public static ScenarioParamter randomNewInstance(int nodeNum, int nodeCapacity, int serviceNum, double ava) {
        int master = DEFAULT_MASTER_NODE;
        double[] availabilities = generateNodeAvailability(nodeNum, ava);
        int[][] graph = Graph.randomUndirectedGraph(nodeNum);
        return ScenarioParamter.builder()
                .nodeNum(nodeNum)
                .serviceNum(serviceNum)
                .serviceResource(1)
                .clusterHead(master)
                .graph(graph)
                .availabilities(availabilities)
                .nodeCapacity(generateNodeCapacity(nodeNum, nodeCapacity))
                .failedNode(randomFailure(graph, availabilities, master))
                .expectedAvailability(ava)
                .availabilityRange(PROBABILITY_RANGE)
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
