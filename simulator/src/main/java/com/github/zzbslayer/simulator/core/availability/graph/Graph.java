package com.github.zzbslayer.simulator.core.availability.graph;

import java.util.*;

public class Graph {
    private int[][] adjacencyMatrix;

    private static ThreadLocal<Random> randomThreadLocal = new ThreadLocal<Random>() {
        @Override
        protected Random initialValue() {
            return new Random();
        }
    };

    public static int[][] randomUndirectedGraph(int nodeNum) {
        int[][] adjacencyMatrix = new int[nodeNum][nodeNum];

        for (int i = 0; i < nodeNum-1; ++i) {
            for (int j = i + 1; j < nodeNum; ++j) {
                int edge = randomThreadLocal.get().nextBoolean() ? 1 : 0;
                adjacencyMatrix[i][j] = edge;
                adjacencyMatrix[j][i] = edge;
            }
        }
        // 保证是一个连通图
        for (int i = 0; i < nodeNum - 1; ++i) {
            adjacencyMatrix[i][i+1] = 1;
            adjacencyMatrix[i+1][i] = 1;
        }
        return adjacencyMatrix;
    }

    public static double[] randomNodeAvailability(int nodeNum) {
        double[] ava = new double[nodeNum];
        for (int i = 0; i < nodeNum; ++i) {
            ava[i] = (double) randomThreadLocal.get().nextInt(100) / 100;
        }
        return ava;
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

    public static boolean reachable(int[][]graph, int[] failedNode, int src, int dst) {
        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visitedNode = new HashSet<>();
        queue.offer(src);
        boolean reachable = false;
        while (!queue.isEmpty()) {
            int node = queue.poll();
            if (node == dst) {
                reachable = true;
                break;
            }
            if (visitedNode.contains(node))
                continue;
            visitedNode.add(node);
            int[] neighbours = graph[node];
            for (int i = 0; i < neighbours.length; ++i) {
                if (neighbours[i] == 1 && failedNode[i] == 0) {
                    queue.offer(i);
                }
            }
        }
        return reachable;
    }

    public static void printGraph(int[][] graph) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < graph.length; ++i) {
            for (int j = 0; j < graph[0].length; ++j) {
                sb.append(graph[i][j]);
                sb.append(", ");
            }
            sb.append("\n");
        }
        System.out.print(sb.toString());
    }

    public static void main(String[] args) {
        int[][] g = randomUndirectedGraph(5);
        printGraph(g);
    }
}