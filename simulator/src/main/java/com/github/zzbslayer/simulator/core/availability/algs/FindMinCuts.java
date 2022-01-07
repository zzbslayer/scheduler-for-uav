package com.github.zzbslayer.simulator.core.availability.algs;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

public class FindMinCuts {

    private int[][] findPathMatrix(int[][] graph, int src, int dst) {
        List<List<Integer>> paths = findPaths(graph, src, dst);

        int[][] pathMatrix;
        return null;
    }

    /**
     * find paths covering all points ? error
     * find paths covering all edges ? error
     * the only way is to find all paths
     */
    private static List<List<Integer>> findPaths(int[][] graph, int src, int dst) {
        List<List<Integer>> paths = new ArrayList<>();

        Queue<List<Integer>> bfs = new LinkedBlockingDeque<>();

        List<Integer> initPath = new ArrayList<>();
        initPath.add(src);
        bfs.add(initPath);

        while(!bfs.isEmpty()) {
            List<Integer> path = bfs.poll();
            int last = path.get(path.size()-1);
            int[] neighbours = graph[last];

            for (int i = 0; i < neighbours.length; ++i) {
                if (neighbours[i] == 1) {
                    if (!path.contains(i)){
                        List<Integer> newPath = new ArrayList<>(path);
                        newPath.add(i);
                        if (i == dst) {
                            paths.add(newPath);
                            continue;
                        }
                        bfs.offer(newPath);
                    }

                }
            }
        }

        return paths;
    }

    private static int[][] calculatePathMatrix(List<List<Integer>> paths, int numOfNodes) {
        int numOfPaths = paths.size();
        int[][] pathMatrix = new int[numOfNodes][numOfPaths];
        for (int pathIndex = 0; pathIndex < numOfPaths; ++pathIndex) {
            for (int nodeIndex: paths.get(pathIndex)) {
                pathMatrix[nodeIndex][pathIndex] = 1;
            }
        }
        return pathMatrix;
    }

    private static List<int[]> findKCutFromPathMatrix(int[][] pathMatrix, int src, int dst, int k) {
        List<int[]> cuts = new ArrayList<>();
        // TODO refactor
        if (k == 1) {
            for (int i = 0; i < pathMatrix.length; ++i) {
                if (i == src || i == dst)
                    continue;
                int[] iVec = pathMatrix[i];

                if (isCut(iVec)) {
                    int[] cut = {i};
                    cuts.add(cut);
                }
            }
        }
        else if (k == 2) {
            for (int i = 0; i < pathMatrix.length; ++i) {
                if (i == src || i == dst)
                    continue;

                int[] iVec = pathMatrix[i];
                for (int j = i+1; j < pathMatrix.length; ++j) {
                    if (j == src || j == dst)
                        continue;

                    int[] jVec = pathMatrix[j];
                    if (isCut(iVec, jVec)) {
                        int[] cut = {i, j};
                        cuts.add(cut);
                    }
                }
            }
        }

        return cuts;
    }

    private static boolean isCut(int[]... vecs) {
        int[] sum = new int[vecs[0].length];
        for (int[] vec: vecs) {
            for (int i = 0; i < vec.length; ++i) {
                sum[i] += vec[i];
            }
        }

        for (int i: sum) {
            if (i == 0)
                return false;
        }
        return true;
    }

    public static List<int[]> findKCutFromGraph(int[][] graph, int src, int dst, int k) {
        List<List<Integer>> paths = findPaths(graph, 0, 5);
        int[][] pathMatrix = calculatePathMatrix(paths, graph.length);
        List<int[]> cuts = findKCutFromPathMatrix(pathMatrix, src, dst, k);
        return cuts;
    }

    public static void main(String[] args) {
        // Let us create a graph shown in the above example
        int graph[][] = new int[][] {
                { 0, 1, 1, 0, 1, 0 },
                { 1, 0, 1, 1, 0, 0 },
                { 1, 1, 0, 1, 0, 1 },
                { 0, 1, 1, 0, 0, 1 },
                { 1, 0, 0, 0, 0, 0 },
                { 0, 0, 1, 1, 0, 0 }
        };
        int src = 0;
        int dst = 5;

        int minCut = Fordfulkerson.findSizeOfMinCut(graph, src, dst);
        System.out.println("Min Cut Size: " + minCut);

        List<List<Integer>> paths = findPaths(graph, 0, 5);
        System.out.println("Paths: ");
        for (List<Integer> path: paths) {
            path.forEach(integer -> System.out.print(integer + ", "));
            System.out.println();
        }
        System.out.println();
        System.out.println("Path Matrix:");
        int[][] pathMatrix = calculatePathMatrix(paths, graph.length);
        for (int[] line: pathMatrix) {
            for (int i: line) {
                System.out.print(i + ", ");
            }
            System.out.println();
        }

        System.out.println();
        System.out.println("Cuts:");
        List<int[]> cuts = findKCutFromPathMatrix(pathMatrix, src, dst, minCut);
        for (int[] cut: cuts) {
            for (int i: cut) {
                System.out.print(i + ", ");
            }
            System.out.println();
        }

    }
}
