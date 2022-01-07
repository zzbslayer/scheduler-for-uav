package com.github.zzbslayer.simulator.core.availability;

import com.github.zzbslayer.simulator.core.availability.algs.FindMinCuts;
import com.github.zzbslayer.simulator.core.availability.algs.Fordfulkerson;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class GraphAvailability {

    public static double calculateNodeFailRate(int[][] graph, int node, int master, double[] availabilities) {
        int cutSize = Fordfulkerson.findSizeOfMinCut(graph, node, master);
        //log.debug("Cut size: {}", cutSize);
        if (cutSize > 2) {
            double ave = Arrays.stream(availabilities).average().getAsDouble();
            return Math.pow((1 - ave), cutSize);
        }
        else {
            List<int[]> cuts = FindMinCuts.findKCutFromGraph(graph, node, master, cutSize);
            double failRate = 0; // add
            for (int[] cut: cuts) {
                double curFailRate = 1; // multiply
                for (int i: cut) {
                    curFailRate *= (1 - availabilities[i]);
                }
                //log.debug("Cur fail rate: {}", curFailRate);
                failRate += curFailRate;
            }
            return failRate;
        }
    }

    public static double calculateNodeAvailability(int[][] graph, int node, int master, double[] availabilities) {
        return 1 - calculateNodeFailRate(graph, node, master, availabilities);
    }

    public static void main(String[] args) {
        // Let us create a graph shown in the above example
        int graph[][] = new int[][]{
                {0, 1, 1, 0, 1, 0},
                {1, 0, 1, 0, 0, 0},
                {1, 1, 0, 1, 0, 1},
                {0, 0, 1, 0, 0, 1},
                {1, 0, 0, 0, 0, 0},
                {0, 0, 1, 1, 0, 0}
        };
        double[] availabilities = {0.99, 0.97, 0.95, 0.94, 0.94, 0.955};
        double availability = calculateNodeAvailability(graph, 0, 5, availabilities);
        System.out.println("Availability: " + availability);
    }
}
