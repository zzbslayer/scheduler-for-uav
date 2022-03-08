package com.github.zzbslayer.simulator.core.latency.scheduler;

import com.github.zzbslayer.simulator.core.availability.graph.Graph;
import com.github.zzbslayer.simulator.core.availability.utils.ScenarioParamter;
import com.github.zzbslayer.simulator.core.latency.prediction.Prediction;
import com.github.zzbslayer.simulator.core.latency.record.AccessRecord;
import com.github.zzbslayer.simulator.core.latency.record.ServicePlacementRecord;
import com.github.zzbslayer.simulator.core.latency.utils.LatencyAndWorkLoadStatistics;
import com.github.zzbslayer.simulator.core.strategy.AvailabilityAwared;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

@Slf4j
public class SimulatedScheduler {
    ScenarioParamter scenarioParamter;

    int[] actualServiceReplicaNum;
    int[] expectedServiceReplicaNum;

    int[] nodeWorkLoad;

    ServicePlacementRecord servicePlacementRecord;
    AccessRecord currentCycleAccessRecord;
    Prediction prediction;

    AccessRecord predictedAccessRecord;

    LatencyAndWorkLoadStatistics latencyAndWorkLoadStatistics;

    public SimulatedScheduler(ScenarioParamter scenarioParamter) {
        this.scenarioParamter = scenarioParamter;
        this.expectedServiceReplicaNum = new int[scenarioParamter.getServiceNum()];
        this.actualServiceReplicaNum = new int[scenarioParamter.getServiceNum()];
        this.nodeWorkLoad = new int[scenarioParamter.getNodeNum()];
        Arrays.fill(this.expectedServiceReplicaNum, 1);

        this.servicePlacementRecord = new ServicePlacementRecord(scenarioParamter.getGraph(), scenarioParamter.getServiceNum());
        this.currentCycleAccessRecord = new AccessRecord(scenarioParamter.getNodeNum());
        this.prediction = new Prediction(scenarioParamter.getNodeNum(), scenarioParamter.getServiceNum());

        this.latencyAndWorkLoadStatistics = new LatencyAndWorkLoadStatistics();

        this.schedule();
    }

    public void putServiceReplicaNum(int service, int replica) {
        this.expectedServiceReplicaNum[service] = replica;
    }

    /**
     * 从 srcNode 节点 访问 service 服务
     * 返回 srcNode 到 service 所在节点的跳数
     *
     * 记录并更新相应的各项指标
     */
    public void access(int srcNode, int service) {
        this.currentCycleAccessRecord.access(srcNode, service);
        /**
         * 访问最近的包含 service 的节点
         */
        int nearestNode = servicePlacementRecord.findNearestService(srcNode, service);
        //servicePlacementRecord.printServicePlacement();
        //System.out.println(srcNode + " visist " + nearestNode);

        int distance = Graph.calculateDistance(this.scenarioParamter.getGraph(), srcNode, nearestNode);

        /**
         * 更新平均跳数指标
         */
        latencyAndWorkLoadStatistics.addHop(distance);
    }

    public void resetAccessRecord() {
        this.predictedAccessRecord.reset();
    }

    public void predict() {
        /**
         * 更新平均机器负载指标
         */
        this.latencyAndWorkLoadStatistics.addMachineWorkLoad(this.nodeWorkLoad);
        this.predictedAccessRecord = this.prediction.predict(this.currentCycleAccessRecord);
    }

    private void printActualServicePlacement() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (int i: actualServiceReplicaNum) {
            sb.append(i);
            sb.append(", ");
        }
        sb.append("] ");
        log.info(sb.toString());
    }
    /**
     * Place unscheduled services to some nodes
     */
    public void schedule() {

        log.info("Before schedule: ");
        printActualServicePlacement();

        for (int service = 0; service < actualServiceReplicaNum.length; ++service) {
            int actualReplica = actualServiceReplicaNum[service];
            int expectedReplica = expectedServiceReplicaNum[service];
            if (actualReplica < expectedReplica) {
                int newReplica = expectedReplica - actualReplica;
                for (int j = 0; j < newReplica; ++j) {
                    // Update nodeWorkLoad
                    int placedNode = AvailabilityAwared.placeService(this.scenarioParamter, this.nodeWorkLoad);
                    // Record service placement
                    this.servicePlacementRecord.putServiceAtNode(service, placedNode);
                }
            }
            else if (actualReplica > expectedReplica) {
                int removeReplica = expectedReplica - actualReplica;
                for (int j = 0; j < removeReplica; ++j) {
                    // TODO remove
                }
            }

            actualServiceReplicaNum[service] = expectedReplica;
        }

        log.info("After schedule: ");
        printActualServicePlacement();

    }

    public void print() {
        latencyAndWorkLoadStatistics.print();
    }

}
