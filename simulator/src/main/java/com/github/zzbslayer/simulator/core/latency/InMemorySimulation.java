package com.github.zzbslayer.simulator.core.latency;

import com.github.zzbslayer.simulator.core.availability.utils.ScenarioParamter;
import com.github.zzbslayer.simulator.core.dataset.DatasetProcessor;
import com.github.zzbslayer.simulator.config.LatencyExperimentConfig;
import com.github.zzbslayer.simulator.core.latency.prediction.*;
import com.github.zzbslayer.simulator.core.latency.prediction.sprediction.MarkovPrediction;
import com.github.zzbslayer.simulator.core.latency.scheduler.SimulatedScheduler;
import com.github.zzbslayer.simulator.utils.DataMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class InMemorySimulation extends DatasetProcessor {

    ScenarioParamter scenarioParamter;
    SimulatedScheduler simulatedScheduler;

    private final static int SCHEDULE_CYCLE = LatencyExperimentConfig.SCHEDULE_CYCLE;
    private final static String DATASET_PATH = LatencyExperimentConfig.DATASET_PATH;

    private long lastMillis = 0;
    private long nextScheduleMillis = 0;

    public InMemorySimulation(ScenarioParamter scenarioParamter, Prediction prediction) {
        super(DATASET_PATH);
        this.scenarioParamter = scenarioParamter;
        simulatedScheduler = new SimulatedScheduler(scenarioParamter, prediction);
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
        }


        invokeRequest(sourceNode, service);
    }

    @Override
    protected void end() {
        System.out.println("================================");
        simulatedScheduler.print();
    }

    protected void invokeRequest(int sourceNode, int service) {

        simulatedScheduler.access(sourceNode, service);
    }

    private void predictAndSchedule() {
        System.out.println("================================");

        // predict
        simulatedScheduler.predict();
        simulatedScheduler.schedule();

        nextScheduleMillis += SCHEDULE_CYCLE;

    }

    public static void main(String[] args) {

        ScenarioParamter scenarioParamter = LatencyExperimentConfig.SCENARIO_PARAMTER;
        //LastValuePrediction lastValuePrediction = new LastValuePrediction(LatencyExperimentConfig.NODE_NUM, LatencyExperimentConfig.SERVICE_NUM);
        //ExponentialMovingAveragePrediction exponentialMovingAveragePrediction = new ExponentialMovingAveragePrediction(LatencyExperimentConfig.NODE_NUM, LatencyExperimentConfig.SERVICE_NUM);
        MarkovPrediction markovPrediction = new MarkovPrediction(LatencyExperimentConfig.NODE_NUM, LatencyExperimentConfig.SERVICE_NUM);

        InMemorySimulation simulation = new InMemorySimulation(scenarioParamter, markovPrediction);
        simulation.execute();
    }
}
