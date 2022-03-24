package com.github.zzbslayer.simulator.latency;

import com.github.zzbslayer.simulator.core.availability.graph.Graph;
import com.github.zzbslayer.simulator.core.latency.record.ServicePlacementRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ServicePlacementRecordTest {
    @Test
    public void nodeWorkLoadTest() {
        int nodeNum = 4;
        int serviceNum = 5;
        int[][] graph = Graph.randomUndirectedGraph(nodeNum);
        ServicePlacementRecord servicePlacementRecord = new ServicePlacementRecord(graph, serviceNum);

        servicePlacementRecord.putServiceAtNode(1, 0);
        servicePlacementRecord.putServiceAtNode(1, 1);
        servicePlacementRecord.putServiceAtNode(1, 2);
        servicePlacementRecord.putServiceAtNode(1, 3);
        servicePlacementRecord.putServiceAtNode(2, 0);

        int[] nodeWorkloads = servicePlacementRecord.getNodeWorkLoads();
        for (int node = 0; node < nodeNum; ++node) {
            if (node == 0)
                Assertions.assertEquals(2, nodeWorkloads[node]);
            else
                Assertions.assertEquals(1, nodeWorkloads[node]);
        }

        servicePlacementRecord.removeServiceFromNodeRoundRobin(1, 2);
        /**
         * 期望从 0
         */
        for (int node = 0; node < nodeNum; ++node) {
            if (node == 1)
                Assertions.assertEquals(0, nodeWorkloads[node]);
            else
                Assertions.assertEquals(1, nodeWorkloads[node]);
        }
    }

}
