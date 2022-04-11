package com.github.zzbslayer.simulator.core.latency.prediction.strategies.lstm;

import com.github.zzbslayer.simulator.config.LatencyExperimentConfig;
import com.github.zzbslayer.simulator.core.dataset.ScheduleCycleDatasetProcessor;
import com.github.zzbslayer.simulator.core.latency.record.AccessRecord;
import lombok.SneakyThrows;

import java.io.*;

public class LstmDatasetMaker extends ScheduleCycleDatasetProcessor {
    AccessRecord currentCycleAccessRecord;
    FileWriter trainWriter;
    FileWriter testWriter;

    int lineCnt = 0;

    public LstmDatasetMaker() {
        super(LatencyExperimentConfig.DATASET_PATH);
        this.currentCycleAccessRecord = new AccessRecord(SCENARIO_PARAMTER.getNodeNum(), SCENARIO_PARAMTER.getServiceNum());

        try {
            newFileAndDeleteIfExist(LatencyExperimentConfig.LSTM_TEST_FILE_PATH);
            newFileAndDeleteIfExist(LatencyExperimentConfig.LSTM_TRAIN_FILE_PATH);

            String header = "Key,Value,\n";
            trainWriter = new FileWriter(LatencyExperimentConfig.LSTM_TRAIN_FILE_PATH);
            testWriter = new FileWriter(LatencyExperimentConfig.LSTM_TEST_FILE_PATH);

            trainWriter.write(header);
            testWriter.write(header);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void newFileAndDeleteIfExist(String filePath) throws IOException {
        File stateFile = new File(filePath);
        if (!stateFile.createNewFile()) {
            stateFile.delete();
            stateFile.createNewFile();
        }
    }



    @Override
    protected void invokeRequest(int sourceNode, int service) {
        lineCnt++;
        currentCycleAccessRecord.access(sourceNode, service);
    }


    @Override
    protected void updateScheduleCycle() {
        StringBuilder sb = new StringBuilder();
        sb.append(lineCnt);
        sb.append(',');
        sb.append(currentCycleAccessRecord.getAccess(1, 1));
        sb.append(",\n");

        String res = sb.toString();
        try {
            if (lineCnt > LatencyExperimentConfig.TRAINING_SET_START) {
                trainWriter.write(res);
            } else {
                testWriter.write(res);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        currentCycleAccessRecord.reset();
    }

    @Override
    protected void end() {
        super.end();
        try {
            this.testWriter.close();
            this.trainWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LstmDatasetMaker lstmDatasetMaker = new LstmDatasetMaker();
        lstmDatasetMaker.execute();
    }
}
