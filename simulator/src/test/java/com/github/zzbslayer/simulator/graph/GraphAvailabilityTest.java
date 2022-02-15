package com.github.zzbslayer.simulator.graph;


import com.github.zzbslayer.simulator.core.availability.graph.Graph;
import com.github.zzbslayer.simulator.core.availability.graph.GraphAvailability;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GraphAvailabilityTest {

    @Test
    void testCase1() {
        int graph[][] = new int[][]{
                {0, 1, 0, 0, 0},
                {1, 0, 1, 0, 1},
                {0, 1, 0, 1, 1},
                {0, 0, 1, 0, 1},
                {0, 1, 1, 1, 0},
        };
        double[] availabilities = {0.99, 0.97, 0.95, 0.94, 0.94, 0.955};
        double graphAvailability = GraphAvailability.calculateNodeGraphAvailability(graph, 0, 3, availabilities);
        System.out.println("Graph Availability: " + graphAvailability);
        Assertions.assertEquals(0.97, graphAvailability);
    }
}
