package com.github.zzbslayer.simulator.core.strategy;

import com.github.zzbslayer.simulator.core.availability.algs.Score;
import com.github.zzbslayer.simulator.core.availability.graph.GraphAvailability;
import com.github.zzbslayer.simulator.core.availability.utils.ScenarioParamter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class AvailabilityAwared {
    /**
     * Find node whose availability is the largest
     */
    public static int placeService(ScenarioParamter scenarioParamter, int[] nodeWorkLoads) {
        int master = scenarioParamter.getClusterHead();
        int nodeNum = scenarioParamter.getNodeNum();
        int serviceResource = scenarioParamter.getServiceResource();
        int[] nodeCapacities = scenarioParamter.getNodeCapacity();

        int bestNode = -1;
        BigDecimal bestScore =  BigDecimal.valueOf(-Double.MAX_VALUE);

//        StringBuilder sb = new StringBuilder();
//        sb.append("[ ");

        double[] availabilitieScores = GraphAvailability.calculateNormalizedNodeGraphAvailabilities(scenarioParamter);

        //System.out.println();

        //printArray(nodeWorkLoads);
        for (int i = 0; i < nodeNum; ++i) {
            if (i == master)
                continue;
            int remain = nodeCapacities[i] - nodeWorkLoads[i];
            if (remain < serviceResource)
                continue;
            double graphAva = availabilitieScores[i];
            /**
             * nodeWorkload 会在部署过程中逐渐发生改变，因此这项指标每次都需要重新计算
             */
            double batteryAva = GraphAvailability.calculateNormalizedNodeBatteryAvailability(i, nodeCapacities, nodeWorkLoads);
//            System.out.println("graphAva: " + graphAva + ", batteryAva: " + batteryAva);
            /**
             * TODO:
             */


            BigDecimal availabilityScore  = new BigDecimal(Score.score(graphAva, batteryAva));

            //System.out.println(availabilityScore + ", " + bestScore);

//            sb.append(availability);
//            sb.append(", ");
            if (availabilityScore.compareTo(bestScore) == 1) {
                bestNode = i;
                bestScore = availabilityScore;
            }
        }
        //sb.append("]");

//        log.info(sb.toString());
        if (bestNode != -1) {
            nodeWorkLoads[bestNode] += serviceResource;
        }
        else {
            throw new RuntimeException("best node not found");
        }
        return bestNode;
    }

    private static void printArray(int[] arr) {
        StringBuilder sb = new StringBuilder();
        sb.append("Ava: [ ");
        for (int i: arr) {
            sb.append(i);
            sb.append(", ");
        }
        sb.append("]");
        log.info(sb.toString());
    }
}
