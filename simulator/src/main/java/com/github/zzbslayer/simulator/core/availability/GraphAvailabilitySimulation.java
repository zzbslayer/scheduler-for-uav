package com.github.zzbslayer.simulator.core.availability;

import com.github.zzbslayer.simulator.core.availability.graph.Graph;
import com.github.zzbslayer.simulator.core.availability.graph.GraphAvailability;
import com.github.zzbslayer.simulator.core.availability.utils.ScenarioParamter;
import com.github.zzbslayer.simulator.core.availability.utils.ServiceFailureStatistics;
import lombok.extern.slf4j.Slf4j;

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

        ServiceFailureStatistics ava = simlulateAvailabilityAwaredPlacement(scenarioParamter);
        log.info("Availability fail node num: {}", ava.getFailedNode());
        log.info("Availability fail instance num: {}", ava.getFailedInstance());
        log.info("Availability fail rate: {}", ava.getFailRate());

        System.out.println();

        ServiceFailureStatistics k8s = simulateK8DefaultPlacement(scenarioParamter);
        log.info("K8S fail node num: {}", k8s.getFailedNode());
        log.info("K8S fail instance num: {}", k8s.getFailedInstance());
        log.info("K8S fail rate: {}",k8s.getFailRate());
    }

    public static void simulate(int times) {
        ServiceFailureStatistics sumAva = new ServiceFailureStatistics();
        ServiceFailureStatistics sumK8s = new ServiceFailureStatistics();
        for (int i = 0; i < times; ++i) {
            ScenarioParamter scenarioParamter = ScenarioParamter.randomNewInstance();
            ServiceFailureStatistics ava = simlulateAvailabilityAwaredPlacement(scenarioParamter);
            ServiceFailureStatistics k8s = simulateK8DefaultPlacement(scenarioParamter);

            sumAva.add(ava);
            sumK8s.add(k8s);
        }

        printStatistics(sumAva);
        printStatistics(sumK8s);
    }

    private static void printStatistics(ServiceFailureStatistics serviceFailureStatistics) {
        log.info("instance num: {}", serviceFailureStatistics.getInstance());
        log.info("fail instance num: {}", serviceFailureStatistics.getFailedInstance());
        log.info("node num: {}", serviceFailureStatistics.getNode());
        log.info("fail node num: {}", serviceFailureStatistics.getFailedNode());

        log.info("fail rate: {}", serviceFailureStatistics.getFailRate());

        System.out.println();
    }

    private static ServiceFailureStatistics simulateK8DefaultPlacement(ScenarioParamter scenarioParamter) {
        int nodeNum = scenarioParamter.getNodeNum();
        int serviceNum = scenarioParamter.getServiceNum();
        int replicaNum = scenarioParamter.getReplicaNum();

        int[] nodeWorkLoad = new int[nodeNum];
        for (int i = 0; i < serviceNum; ++i) {
            for (int j = 0; j < replicaNum; ++j) {
                placeServiceAtSuitableNodeK8Default(scenarioParamter, nodeWorkLoad);
            }
        }
        //printArray(nodeWorkLoad);
        return calculateStatistics(scenarioParamter, nodeWorkLoad);
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

    private static ServiceFailureStatistics simlulateAvailabilityAwaredPlacement(ScenarioParamter scenarioParamter) {
        int nodeNum = scenarioParamter.getNodeNum();
        int serviceNum = scenarioParamter.getServiceNum();
        int replicaNum = scenarioParamter.getReplicaNum();

        int[] nodeWorkLoad = new int[nodeNum];
        for (int i = 0; i < serviceNum; ++i) {
            for (int j = 0; j < replicaNum; ++j) {

                placeServiceAtSuitableNodeAvailabilityAwared(scenarioParamter, nodeWorkLoad);
            }
        }
        //printArray(nodeWorkLoad);
        return calculateStatistics(scenarioParamter, nodeWorkLoad);
    }

    private static ServiceFailureStatistics calculateStatistics(ScenarioParamter scenarioParamter, int[] nodeWorkLoad) {
        int[] failure = scenarioParamter.getFailedNode();
        int failedInstance = 0;
        int failedNode = 0;
        for (int i = 0; i < nodeWorkLoad.length; ++i) {
            if (failure[i] != 0) {
                // 0 means live; -1 means dead; -2 means live but unreachable by master
                ++failedNode;
                failedInstance += (nodeWorkLoad[i]);
            }

        }
        return ServiceFailureStatistics.builder()
                .failedInstance(failedInstance)
                .instance(scenarioParamter.getServiceNum() * scenarioParamter.getReplicaNum())
                .failedNode(failedNode)
                .node(scenarioParamter.getNodeNum())
                .build();
    }

    /**
     * Find node whose remaining resources is more than others
     */
    private static int placeServiceAtSuitableNodeK8Default(ScenarioParamter scenarioParamter, int[] nodeWorkLoad) {
        int nodeNum = scenarioParamter.getNodeNum();
        int serviceResource = scenarioParamter.getServiceResource();
        int[] nodeCapacity = scenarioParamter.getNodeCapacity();

        int bestNode = -1;
        int bestScore = 0;
        for (int i = 0; i < nodeNum; ++i) {
            int remain = nodeCapacity[i] - nodeWorkLoad[i];
            if (remain < serviceResource)
                continue;
            int score = remain;
            if (score > bestScore) {
                bestNode = i;
                bestScore = score;
            }
        }
        if (bestNode != -1) {
            nodeWorkLoad[bestNode] += serviceResource;
        }
        return bestNode;
    }

    /**
     * Find node whose availability is the largest
     */
    private static int placeServiceAtSuitableNodeAvailabilityAwared(ScenarioParamter scenarioParamter, int[] nodeWorkLoad) {
        int nodeNum = scenarioParamter.getNodeNum();
        int serviceResource = scenarioParamter.getServiceResource();
        int clusterHead = scenarioParamter.getClusterHead();
        int[][] graph = scenarioParamter.getGraph();
        double[] availabilities = scenarioParamter.getAvailabilities();
        int[] nodeCapacity = scenarioParamter.getNodeCapacity();

        int bestNode = -1;
        double bestScore = 0;

        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (int i = 0; i < nodeNum; ++i) {
            int remain = nodeCapacity[i] - nodeWorkLoad[i];
            if (remain < serviceResource)
                continue;
            double availability = GraphAvailability.calculateNodeAvailability(graph, i, clusterHead, availabilities, nodeCapacity[i], nodeWorkLoad[i]);
            sb.append(availability);
            sb.append(", ");
            if (availability > bestScore) {
                bestNode = i;
                bestScore = availability;
            }
        }
        sb.append("]");
//        printArray(nodeWorkLoad);
//        log.info(sb.toString());
        if (bestNode != -1) {
            nodeWorkLoad[bestNode] += serviceResource;
        }
        return bestNode;
    }

    public static void main(String[] args) {
        //simulateOnce();
        simulate(1000);
    }
}
