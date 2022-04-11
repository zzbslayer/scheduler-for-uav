package com.github.zzbslayer.simulator.markov;

import com.github.zzbslayer.simulator.config.LatencyExperimentConfig;
import com.github.zzbslayer.simulator.core.latency.prediction.strategies.markov.FileHelper;
import com.github.zzbslayer.simulator.core.latency.prediction.strategies.markov.MarkovTrainer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MarkovTest {
    @Test
    public void test() {
        MarkovTrainer markovTrainer = new MarkovTrainer();
        markovTrainer.execute();
        markovTrainer.writeStateToFile();

        double[][] writeState = markovTrainer.getMarkovState();
        double[][] readState = FileHelper.readStateFromFile();

        for (int i = 0; i < LatencyExperimentConfig.MARKOV_MATRIX_SIZE; ++i) {
            for (int j = 0; j < LatencyExperimentConfig.MARKOV_MATRIX_SIZE; ++j) {
                Assertions.assertEquals(writeState[i][j], readState[i][j]);
            }
        }
    }
}
