package com.github.zzbslayer.simulator.graph;

import com.github.zzbslayer.simulator.core.availability.algs.FindMinCuts;
import com.github.zzbslayer.simulator.core.availability.algs.Fordfulkerson;
import com.github.zzbslayer.simulator.core.availability.graph.Graph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class FindMinCutsTest {
    @Test
    void testCase1() {
        int node = 0;
        int master = 3;
        int[][] graph = new int[][]{
                {0, 1, 0, 0, 0},
                {1, 0, 1, 0, 1},
                {0, 1, 0, 1, 1},
                {0, 0, 1, 0, 1},
                {0, 1, 1, 1, 0},
        };
        int minCut = Fordfulkerson.findSizeOfMinCut(graph, node, master);
        Assertions.assertEquals(1, minCut);

        List<List<Integer>> paths = FindMinCuts.findPaths(graph, node, master);
        paths.stream().forEach(path -> {
            path.stream().map(i -> i+", ").forEach(System.out::print);
            System.out.println();
        });

//        0, 1, 2, 3,
//        0, 1, 4, 3,
//        0, 1, 2, 4, 3,
//        0, 1, 4, 2, 3,

        System.out.println();
        int[][] pathMatrix = FindMinCuts.calculatePathMatrix(paths, graph.length, node, master);
        Graph.printGraph(pathMatrix);
        List<int[]> cuts = FindMinCuts.findKCutFromPathMatrix(pathMatrix, node, master, minCut);
//        0, 0, 0, 0,
//        1, 1, 1, 1,
//        1, 0, 1, 1,
//        0, 0, 0, 0,
//        0, 1, 1, 1,
        System.out.println();
        for (int[] cut: cuts) {
            for (int i: cut) {
                System.out.print(i + ", ");
            }
            System.out.println();
        }
    }

    @Test
    void testCase2() {

    }
}
