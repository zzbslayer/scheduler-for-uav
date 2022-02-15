package com.github.zzbslayer.simulator.core.availability.utils;

import com.github.zzbslayer.simulator.core.availability.graph.Graph;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Random;

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

    static double randomPercent() {
        return (double) randomThreadLocal.get().nextInt(100) / 100;
    }

    static double[] generateNodeAvailability(int nodeNum) {
        double[] availabilities = new double[nodeNum];
        for (int i = 0; i < nodeNum; ++i) {
            availabilities[i] = randomPercent();
        }
        return availabilities;
    }

    static int[] randomFailure(double[] availabilities, int master) {
        int[] failure = new int[availabilities.length];
        for (int i = 0; i < availabilities.length; ++i) {
            if (i == master)
                continue;
            failure[i] = randomPercent() > availabilities[i] ? -1 : 0;
        }
        return failure;
    }

    public static ScenarioParamter randomNewInstance() {
        int nodeNum = 5;
        int master = 3;
        double[] availabilities = generateNodeAvailability(nodeNum);
        return ScenarioParamter.builder()
                .nodeNum(nodeNum)
                .serviceNum(8)
                .replicaNum(1)
                .serviceResource(1)
                .clusterHead(master)
                .graph(Graph.randomUndirectedGraph(nodeNum))
                .availabilities(availabilities)
                .nodeCapacity(generateNodeCapacity(nodeNum))
                .failedNode(randomFailure(availabilities, master))
                .build();
    }
}
