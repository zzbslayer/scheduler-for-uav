package com.github.zzbslayer.simulator.core.availability.graph;

import com.github.zzbslayer.simulator.core.availability.algs.FindMinCuts;
import com.github.zzbslayer.simulator.core.availability.algs.Fordfulkerson;
import com.github.zzbslayer.simulator.core.availability.utils.Normalization;
import com.github.zzbslayer.simulator.core.availability.utils.ScenarioParamter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class GraphAvailability {

    public static double calculateNodeFailRate(int[][] graph, int node, int master, double[] availabilities) {
        int cutSize = Fordfulkerson.findSizeOfMinCut(graph, node, master);
        //log.debug("{} {} cut size: {}", node, master, cutSize);
        if (cutSize > 2) {
            // TODO
            double ave = Arrays.stream(availabilities).average().getAsDouble();
            return Math.pow((1 - ave), cutSize);
        }
        else {
            List<int[]> cuts = FindMinCuts.findKCutFromGraph(graph, node, master, cutSize);
            double overallFailRate = 0; // add
            for (int[] cut: cuts) {

                double cutFailRate = 1; // multiply

                for (int i = 0; i < availabilities.length; ++i) {
                    boolean isVertexOfCut = false;
                    for (int j: cut) {
                        if (i == j) {
                            isVertexOfCut = true;
                            break;
                        }
                    }
                    cutFailRate *= isVertexOfCut ? (1 - availabilities[i]) : availabilities[i];
                }


                //log.debug("Cur fail rate: {}", cutFailRate);
                overallFailRate += cutFailRate;

            }
            return overallFailRate;
        }
    }

    public static double calculateNodeGraphAvailability(int[][] graph, int node, int master, double[] availabilities) {
        return 1.0 - calculateNodeFailRate(graph, node, master, availabilities);
    }

    /**
     * 计算单个节点的电池可用性，或者说剩余资源
     * 剩余资源越少，意味着耗电越多，电池的可用性越低
     * 同时这个指标也能使资源分布更加平均
     * @param nodeCapacity
     * @param nodeWorkLoad
     * @return
     */
    public static double calculateNodeBatteryAvailability(int nodeCapacity, int nodeWorkLoad) {
        return 1.0 - 1.0 * nodeWorkLoad / nodeCapacity;
    }

    /**
     * 计算单个节点的电池可用性，并且归一化
     * 因为每次部署服务的时候，都需要计算可用性，服务部署上去以后，电池可用性会改变，因此每次都需要重新计算归一化以后的电池可用性
     * @param node
     * @param nodeCapacities
     * @param nodeWorkLoads
     * @return
     */
    public static double calculateNormalizedNodeBatteryAvailability(int node, int[] nodeCapacities, int[] nodeWorkLoads) {
        double[] batteryAvas = new double[nodeCapacities.length];
//        System.out.println(" node work load: ");
//        for (int i: nodeWorkLoads) {
//            System.out.print(i + ", ");
//        }
//        System.out.println();

        for (int i = 0; i < nodeCapacities.length; ++i) {
            batteryAvas[i] = calculateNodeBatteryAvailability(nodeCapacities[i], nodeWorkLoads[i]);
            //System.out.println("111: " + batteryAvas[i]);
        }
        /**
         * maxMinNormalize
         */
        double[] normalizedBatteryAvas = Normalization.maxMinNormalization(batteryAvas);
        return normalizedBatteryAvas[node];
    }

    /**
     * 计算所有节点的图结构的可用性，并且归一化
     */
    public static double[] calculateNormalizedNodeGraphAvailabilities(ScenarioParamter scenarioParamter) {
        int[][] graph = scenarioParamter.getGraph();
        int master = scenarioParamter.getClusterHead();
        double[] availabilities = scenarioParamter.getAvailabilities();

        int nodeNum = graph.length;
        double[] graphAvas = new double[nodeNum];

        for (int i = 0; i < nodeNum; ++i) {
            graphAvas[i] = calculateNodeGraphAvailability(graph, i, master, availabilities);
        }
//        double maxAva = scenarioParamter.getExpectedAvailability() - scenarioParamter.getAvailabilityRange();
//        double minAva = scenarioParamter.getExpectedAvailability() + scenarioParamter.getAvailabilityRange();

//        System.out.println("1. raw graph ava: ");
//        for (int i = 0; i < nodeNum; ++i) {
//            System.out.print(graphAvas[i]);
//            System.out.print(". ");
//        }

        double[] normalizedGraphAvas = Normalization.maxMinNormalization(graphAvas);

//        System.out.println("\n2. normalized graph ava: ");
//        for (int i = 0; i < nodeNum; ++i) {
//            System.out.print(normalizedGraphAvas[i]);
//            System.out.print(". ");
//        }

        return normalizedGraphAvas;
    }

    /**
     * deprecated now
     * @param graph
     * @param node
     * @param master
     * @param availabilities
     * @param nodeCapacity
     * @param nodeWorkLoad
     * @return
     */
    public static double calculateNodeAvailability(int[][] graph, int node, int master, double[] availabilities, int nodeCapacity, int nodeWorkLoad) {
        double graphAva = calculateNodeGraphAvailability(graph, node, master, availabilities);
        double batteryAva = calculateNodeBatteryAvailability(nodeCapacity, nodeWorkLoad);
        /**
         * TODO any better solution ?
         */
        double res = graphAva; //0.7 * graphAva + 0.3 * batteryAva;
        //System.out.println("node "+node+": "+graphAva+ ", "+batteryAva + " => " + res);
        return res;
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
        double availability = calculateNodeGraphAvailability(graph, 0, 5, availabilities);
        System.out.println("Availability: " + availability);
    }
}
