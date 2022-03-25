package com.github.zzbslayer.simulator.core.latency;

import com.github.zzbslayer.simulator.core.availability.utils.ScenarioParamter;
import com.github.zzbslayer.simulator.config.LatencyExperimentConfig;
import com.github.zzbslayer.simulator.core.dataset.ScheduleCycleDatasetProcessor;
import com.github.zzbslayer.simulator.core.latency.prediction.*;
import com.github.zzbslayer.simulator.core.latency.prediction.nsprediction.ExponentialMovingAveragePrediction;
import com.github.zzbslayer.simulator.core.latency.prediction.nsprediction.LastValuePrediction;
import com.github.zzbslayer.simulator.core.latency.prediction.sprediction.MarkovPrediction;
import com.github.zzbslayer.simulator.core.latency.scheduler.SimulatedScheduler;
import com.github.zzbslayer.simulator.utils.DataMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class InMemorySimulation extends ScheduleCycleDatasetProcessor {

    ScenarioParamter scenarioParamter;
    SimulatedScheduler simulatedScheduler;

    private final static String DATASET_PATH = LatencyExperimentConfig.DATASET_PATH;

    public InMemorySimulation(ScenarioParamter scenarioParamter, Prediction prediction) {
        super(DATASET_PATH);
        this.scenarioParamter = scenarioParamter;
        simulatedScheduler = new SimulatedScheduler(scenarioParamter, prediction);
    }

    @Override
    protected void end() {
        System.out.println("================================");
        simulatedScheduler.print();
    }

    protected void invokeRequest(int sourceNode, int service) {

        simulatedScheduler.access(sourceNode, service);
    }

    @Override
    protected void updateScheduleCycle() {
        System.out.println("================================");

        // predict
        simulatedScheduler.predict();
        simulatedScheduler.schedule();
    }

    public static void main(String[] args) {

        ScenarioParamter scenarioParamter = LatencyExperimentConfig.SCENARIO_PARAMTER;
        LastValuePrediction lastValuePrediction = new LastValuePrediction(LatencyExperimentConfig.NODE_NUM, LatencyExperimentConfig.SERVICE_NUM);
        ExponentialMovingAveragePrediction exponentialMovingAveragePrediction = new ExponentialMovingAveragePrediction(LatencyExperimentConfig.NODE_NUM, LatencyExperimentConfig.SERVICE_NUM);
        MarkovPrediction markovPrediction = new MarkovPrediction(LatencyExperimentConfig.NODE_NUM, LatencyExperimentConfig.SERVICE_NUM);

        InMemorySimulation simulation = new InMemorySimulation(scenarioParamter, markovPrediction);
        simulation.execute();
    }
}
