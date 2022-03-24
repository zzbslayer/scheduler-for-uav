package com.github.zzbslayer.simulator.core.availability;

import com.github.zzbslayer.simulator.core.availability.graph.Graph;
import com.github.zzbslayer.simulator.core.availability.utils.ScenarioParamter;
import com.github.zzbslayer.simulator.core.availability.utils.ServiceFailureStatistics;
import com.github.zzbslayer.simulator.core.strategy.AvailabilityAwared;
import com.github.zzbslayer.simulator.core.strategy.K8Default;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Random;

@Slf4j
public class GraphAvailabilitySimulation {
    private static ThreadLocal<Random> randomThreadLocal = new ThreadLocal<Random>() {
        @Override
        protected Random initialValue() {
            return new Random();
        }
    };

    public static void simulateOnce() {
        ScenarioParamter scenarioParamter = ScenarioParamter.randomNewInstance();
        Graph.printGraph(scenarioParamter.getGraph());

        ServiceFailureStatistics ava = simulateAvailabilityAwaredPlacement(scenarioParamter);
        ava.print();

        System.out.println();

        ServiceFailureStatistics k8s = simulateK8DefaultPlacement(scenarioParamter);
        k8s.print();
    }

    public static void simulate(int times, int nodeNum, int nodeCapacity, int serviceNum, double ava) {
        ServiceFailureStatistics sumAva = new ServiceFailureStatistics();
        ServiceFailureStatistics sumK8s = new ServiceFailureStatistics();
        for (int i = 0; i < times; ++i) {
            ScenarioParamter scenarioParamter = ScenarioParamter.randomNewInstance(nodeNum, nodeCapacity, serviceNum, ava);
            ServiceFailureStatistics avaStats = simulateAvailabilityAwaredPlacement(scenarioParamter);
            ServiceFailureStatistics k8sStats = simulateK8DefaultPlacement(scenarioParamter);

            sumAva.add(avaStats);
            sumK8s.add(k8sStats);
        }

        System.out.println("Ava statistics: ");
        sumAva.print();
        System.out.println("K8S statistics: ");
        sumK8s.print();
    }



    private static ServiceFailureStatistics simulateK8DefaultPlacement(ScenarioParamter scenarioParamter) {
        int nodeNum = scenarioParamter.getNodeNum();
        int serviceNum = scenarioParamter.getServiceNum();
        int replicaNum = 1;

        int[] nodeWorkLoad = new int[nodeNum];
        for (int i = 0; i < serviceNum; ++i) {
            for (int j = 0; j < replicaNum; ++j) {
                int bestNode = K8Default.placeService(scenarioParamter, nodeWorkLoad);
                nodeWorkLoad[bestNode] += scenarioParamter.getServiceResource();
            }
        }
        //printArray(nodeWorkLoad);
        ServiceFailureStatistics res = calculateStatistics(scenarioParamter, nodeWorkLoad);
        return res;
    }

    private static void printArray(int[] arr) {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (int i: arr) {
            sb.append(i);
            sb.append(", ");
        }
        sb.append("]");
        log.info(sb.toString());
    }

    private static void printArray(double[] arr) {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (double i: arr) {
            sb.append(i);
            sb.append(", ");
        }
        sb.append("]");
        log.info(sb.toString());
    }

    private static ServiceFailureStatistics simulateAvailabilityAwaredPlacement(ScenarioParamter scenarioParamter) {
        int nodeNum = scenarioParamter.getNodeNum();
        int serviceNum = scenarioParamter.getServiceNum();
        int replicaNum = 1;

        int[] nodeWorkLoad = new int[nodeNum];
        for (int i = 0; i < serviceNum; ++i) {
            for (int j = 0; j < replicaNum; ++j) {

                AvailabilityAwared.placeService(scenarioParamter, nodeWorkLoad);
            }
        }
        //printArray(nodeWorkLoad);
        ServiceFailureStatistics res = calculateStatistics(scenarioParamter, nodeWorkLoad);
        return res;
    }

    private static ServiceFailureStatistics calculateStatistics(ScenarioParamter scenarioParamter, int[] nodeWorkLoad) {
        int[] failure = scenarioParamter.getFailedNode();
        int failedInstance = 0;
        int failedNode = 0;
        int replicaNum = 1;
        for (int i = 0; i < nodeWorkLoad.length; ++i) {
            if (failure[i] != 0) {
                // 0 means live; -1 means dead; -2 means live but unreachable by master
                ++failedNode;
                failedInstance += (nodeWorkLoad[i]);
            }
        }

        ServiceFailureStatistics res = ServiceFailureStatistics.builder()
                .servicePlacementSquaresSum(Arrays.stream(nodeWorkLoad).map(x -> x*x).sum())
                .servicePlacementSum(Arrays.stream(nodeWorkLoad).sum())
                .failedInstance(failedInstance)
                .instance(scenarioParamter.getServiceNum() * replicaNum)
                .failedNode(failedNode)
                .node(scenarioParamter.getNodeNum())
                .build();

        return res;
    }


    public static void main(String[] args) {


        //simulateOnce();
//        int times = 1000;
//        int nodeNum = 15;
//        int nodeCapacity = 40;
//        int serviceNum = 45;
//        double ava = 0.95;
//        for (int i = 0; i < 5; i++) {
//            ava = 0.95 - 0.05 * i;
//            simulate(times, nodeNum, nodeCapacity, serviceNum, ava);
//        }

    }
}
