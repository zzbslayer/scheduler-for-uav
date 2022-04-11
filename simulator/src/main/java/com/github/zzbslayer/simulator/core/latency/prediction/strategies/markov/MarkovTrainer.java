package com.github.zzbslayer.simulator.core.latency.prediction.strategies.markov;

import com.github.zzbslayer.simulator.core.availability.utils.ScenarioParamter;
import com.github.zzbslayer.simulator.config.LatencyExperimentConfig;
import com.github.zzbslayer.simulator.core.dataset.ScheduleCycleDatasetProcessor;
import com.github.zzbslayer.simulator.core.latency.record.AccessRecord;
import com.github.zzbslayer.simulator.core.latency.prediction.mapper.AccessInstanceMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Slf4j
public class MarkovTrainer extends ScheduleCycleDatasetProcessor {

    private final static String DATASET_PATH = LatencyExperimentConfig.DATASET_PATH;

    // node, service, cnt
    private AccessRecord currentAccessRecord = new AccessRecord(SCENARIO_PARAMTER.getNodeNum(), ScenarioParamter.randomNewInstance().getServiceNum());
    private AccessRecord lastAccessRecord = new AccessRecord(SCENARIO_PARAMTER.getNodeNum(), ScenarioParamter.randomNewInstance().getServiceNum());

    private boolean firstCycle = true;
    private AccessInstanceMapper mapper = AccessInstanceMapper.getMapper();

    /**
     *     状态转移，我们将访问量以区间形式划分，每个区间视为一种状态
     *     计算各个区间（状态）之间转移的概率
     *     0~10 -> state 0
     *     10~20 -> state 1
     *     ...
     *     k*10~(k+1) -> state k
      */
    private final static int MARKOV_MATRIX_SIZE = LatencyExperimentConfig.MARKOV_MATRIX_SIZE;
    private double[][] markovState = new double[MARKOV_MATRIX_SIZE][MARKOV_MATRIX_SIZE];

    public MarkovTrainer() {
        super(DATASET_PATH);
    }

    /**
     * debug use
     */
    public double[][] getMarkovState() {
        return markovState;
    }

    @Override
    public void processCsv() throws IOException {

        BufferedReader csvReader = new BufferedReader(new FileReader(this.datasetPath));
        String row = csvReader.readLine();
        processHead(row);

        row = csvReader.readLine();
        processFirstLine(row);

        int cnt = 0;
        while ((row = csvReader.readLine()) != null) {
            if (cnt == LatencyExperimentConfig.TRAINING_SET_START)
                processFirstLine(row);
            else if (cnt > LatencyExperimentConfig.TRAINING_SET_START)
                processLine(row);
            else if (cnt >= 400000)
                break;
            cnt++;
        }

        try {
            Thread.sleep(5000);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        csvReader.close();
    }

    @Override
    protected void end() {

        //printMarkovState();
        // writeStateToFile();
    }

    public void writeStateToFile() {
        convertMarkovMatrixToStackedProbabilityMatrix();
        FileHelper.writeStateToFile(markovState);
    }

    private void convertMarkovMatrixToStackedProbabilityMatrix() {
        for (int i = 0; i < MARKOV_MATRIX_SIZE; ++i) {
            int sum = 0;
            double last = 0;

            for (int j = 0; j < MARKOV_MATRIX_SIZE; ++j) {
                sum += markovState[i][j];
                markovState[i][j] += last;
                last = markovState[i][j];
            }
            if (sum == 0)
                continue;

            /**
             * stacked probability matrix 便于按照概率计算应该转移到哪个状态
             * 示例：
             * 概率矩阵     Stacked 概率矩阵
             * 0.1, 0.9   0.1, 1
             * 0.3, 0.7   0.3, 1
             *
             *     int currentState = 0;
             *     double random = random.nextDouble(); // 0~1
             *     int nextState = -1;
             *     for (int j = 0; j < size; ++j {
             *         if (random < matrix[currentState][j]) {
             *             nextState = j;
             *             break;
             *         }
             *     }
             *
             */
            for (int j = 0; j < MARKOV_MATRIX_SIZE; ++j) {
                markovState[i][j] = markovState[i][j] / sum;
            }
        }
    }

    private void printMarkovState() {
        for (int i = 0; i < MARKOV_MATRIX_SIZE; ++i) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < MARKOV_MATRIX_SIZE; ++j) {
                sb.append(markovState[i][j]);
                sb.append(", ");
            }
            log.info(sb.toString());
        }
    }

    private void updateMarkovState() {
        int nodeNum = SCENARIO_PARAMTER.getNodeNum();
        int serviceNum = SCENARIO_PARAMTER.getServiceNum();

        for (int i = 0; i < nodeNum; ++i) {
            for (int j = 0; j < serviceNum; ++j) {
                int currentAccess = currentAccessRecord.getAccess(i, j);
                int currentState = mapper.accessToInstance(currentAccess);
                int lastAccess = lastAccessRecord.getAccess(i, j);
                int lastState = mapper.accessToInstance(lastAccess);
                markovState[lastState][currentState]++;
            }
        }
    }

    @Override
    protected void updateScheduleCycle() {
        if (firstCycle == false) {
            updateMarkovState();
        }
        else {
            firstCycle = false;
        }

        lastAccessRecord.copyFrom(currentAccessRecord);
        currentAccessRecord.reset();
    }

    protected void invokeRequest(int sourceNode, int service) {
        currentAccessRecord.access(sourceNode, service);
    }


    public static void main(String[] args) {
        MarkovTrainer markovTrainer = new MarkovTrainer();
        markovTrainer.execute();
        markovTrainer.writeStateToFile();
    }
}
