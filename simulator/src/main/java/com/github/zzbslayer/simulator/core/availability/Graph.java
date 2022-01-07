package com.github.zzbslayer.simulator.core.availability;

import java.util.Random;

public class Graph {
    private int[][] adjacencyMatrix;

    private static ThreadLocal<Random> randomThreadLocal = new ThreadLocal<Random>() {
        @Override
        protected Random initialValue() {
            return new Random();
        }
    };

    public static Graph randomUndirectedGraph(int nodeNum) {
        int[][] adjacencyMatrix = new int[nodeNum][nodeNum];
        for (int i = 0; i < nodeNum; ++i) {
            adjacencyMatrix[i][i] = 1;
        }
        for (int i = 0; i < nodeNum-1; ++i) {
            for (int j = i + 1; j < nodeNum; ++j) {
                int edge = randomThreadLocal.get().nextBoolean() ? 1 : 0;
                adjacencyMatrix[i][j] = edge;
                adjacencyMatrix[j][i] = edge;
            }
        }
        Graph graph = new Graph(adjacencyMatrix);
        return graph;
    }

    public Graph(int[][] aMatrix) {
        this.adjacencyMatrix = aMatrix;
    }

    public double calculateGraphAvailability(int idx) {
        return 0;
    }

    // kube1 to 0
    private static int mapNodeNameToMatrixIndex(String name) {
        return Integer.parseInt(name.substring(4))+1;
    }

    private static String mapMatrixIndexToNodeName(int idx) {
        return "kube" + idx+1;
    }
}
