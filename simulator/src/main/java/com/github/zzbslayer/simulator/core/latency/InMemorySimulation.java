package com.github.zzbslayer.simulator.core.latency;

import com.github.zzbslayer.simulator.core.availability.utils.ScenarioParamter;
import com.github.zzbslayer.simulator.core.latency.scheduler.SimulatedScheduler;
import com.github.zzbslayer.simulator.utils.DataMapper;

import java.io.IOException;

public class InMemorySimulation extends GraphLatencySimulation {

    ScenarioParamter scenarioParamter;
    SimulatedScheduler simulatedScheduler;

    private final static int SCHEDULE_CYCLE = 1000 * 60 * 3;
    private long lastMillis = 0;
    private long nextScheduleMillis = 0;


    public InMemorySimulation() {
        scenarioParamter = ScenarioParamter.randomNewInstance();
        simulatedScheduler = new SimulatedScheduler(scenarioParamter);
    }

    protected void processFirstLine(String line) throws IOException{
        String[] items = line.split(",");
        String source = items[0];
        long timestamp = (long) Double.parseDouble(items[1]);
        String rawUrl = items[2];

        this.lastMillis = timestamp;
        this.nextScheduleMillis = lastMillis + SCHEDULE_CYCLE;

        //System.out.println(source+","+rawUrl);

        int sourceNode = DataMapper.mapSourceToNode(source, scenarioParamter.getNodeNum());
        int service = DataMapper.mapUrlToService(rawUrl, scenarioParamter.getNodeNum());

        //System.out.println(sourceNode+","+service);

        invokeRequest(sourceNode, service);
    }

    @Override
    protected void processLine(String line) throws IOException {
        String[] items = line.split(",");
        String source = items[0];
        long timestamp = (long) Double.parseDouble(items[1]);
        String rawUrl = items[2];

        int sourceNode = DataMapper.mapSourceToNode(source, scenarioParamter.getNodeNum());
        int service = DataMapper.mapUrlToService(rawUrl, scenarioParamter.getNodeNum());

        if (timestamp > nextScheduleMillis) {
            predictAndSchedule();
            nextScheduleMillis += SCHEDULE_CYCLE;
        }

        invokeRequest(sourceNode, service);
    }

    @Override
    protected void end() {
        simulatedScheduler.print();
    }

    protected void invokeRequest(int sourceNode, int service) {
        simulatedScheduler.access(sourceNode, service);
    }

    private void predictAndSchedule() {
        // predict
        simulatedScheduler.predict();
        simulatedScheduler.schedule();
        simulatedScheduler.resetAccessRecord();
    }

    public static void main(String[] args) {

    }
}
