package com.github.zzbslayer.simulator.core.dataset;

import com.github.zzbslayer.simulator.config.LatencyExperimentConfig;
import com.github.zzbslayer.simulator.core.availability.utils.ScenarioParamter;
import com.github.zzbslayer.simulator.utils.DataMapper;

import java.io.IOException;

public abstract class ScheduleCycleDatasetProcessor extends DatasetProcessor {
    protected final static int SCHEDULE_CYCLE = LatencyExperimentConfig.SCHEDULE_CYCLE;
    protected final static ScenarioParamter SCENARIO_PARAMTER = LatencyExperimentConfig.SCENARIO_PARAMTER;
    protected final static String DATASET_PATH = LatencyExperimentConfig.DATASET_PATH;

    protected long lastMillis = 0;
    protected long nextScheduleMillis = 0;

    private ScheduleCycleDatasetProcessor() {}

    public ScheduleCycleDatasetProcessor(String datasetPath) {
        super(datasetPath);
    }

    @Override
    protected void processFirstLine(String line) throws IOException {
        String[] items = line.split(",");
        String source = items[0];
        long timestamp = (long) Double.parseDouble(items[1]);
        String rawUrl = items[2];

        this.lastMillis = timestamp;
        this.nextScheduleMillis = lastMillis + SCHEDULE_CYCLE;

        int sourceNode = DataMapper.mapSourceToNode(source, SCENARIO_PARAMTER.getNodeNum());
        int service = DataMapper.mapUrlToService(rawUrl, SCENARIO_PARAMTER.getNodeNum());

        invokeRequest(sourceNode, service);
    }

    @Override
    protected void processLine(String line) throws IOException {
        String[] items = line.split(",");
        String source = items[0];
        long timestamp = (long) Double.parseDouble(items[1]);
        String rawUrl = items[2];

        int sourceNode = DataMapper.mapSourceToNode(source, SCENARIO_PARAMTER.getNodeNum());
        int service = DataMapper.mapUrlToService(rawUrl, SCENARIO_PARAMTER.getNodeNum());


        if (timestamp > nextScheduleMillis) {
            updateScheduleCycle();
            nextScheduleMillis += SCHEDULE_CYCLE;
        }


        invokeRequest(sourceNode, service);
    }

    protected abstract void invokeRequest(int sourceNode, int service);
    protected abstract void updateScheduleCycle();
}
