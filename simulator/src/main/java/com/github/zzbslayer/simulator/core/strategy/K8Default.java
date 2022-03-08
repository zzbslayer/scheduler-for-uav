package com.github.zzbslayer.simulator.core.strategy;

import com.github.zzbslayer.simulator.core.availability.utils.ScenarioParamter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class K8Default {
    public static int placeService(ScenarioParamter scenarioParamter, int[] nodeWorkLoad) {
        int master = scenarioParamter.getClusterHead();
        int nodeNum = scenarioParamter.getNodeNum();
        int serviceResource = scenarioParamter.getServiceResource();
        int[] nodeCapacity = scenarioParamter.getNodeCapacity();

        int bestNode = -1;
        int bestScore = 0;
        for (int i = 0; i < nodeNum; ++i) {
            if (i == master)
                continue;
            int remain = nodeCapacity[i] - nodeWorkLoad[i];
            if (remain < serviceResource)
                continue;
            int score = remain;
            if (score > bestScore) {
                bestNode = i;
                bestScore = score;
            }
        }
        //printArray(nodeWorkLoad);
        if (bestNode != -1) {
            nodeWorkLoad[bestNode] += serviceResource;
        }
        return bestNode;
    }

    private static void printArray(int[] arr) {
        StringBuilder sb = new StringBuilder();
        sb.append("K8s: [ ");
        for (int i: arr) {
            sb.append(i);
            sb.append(", ");
        }
        sb.append("]");
        log.info(sb.toString());
    }
}
