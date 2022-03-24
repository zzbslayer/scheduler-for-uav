package com.github.zzbslayer.simulator.markov;

import com.github.zzbslayer.simulator.config.LatencyExperimentConfig;
import com.github.zzbslayer.simulator.core.markov.FileHelper;
import com.github.zzbslayer.simulator.core.markov.TrainMarkov;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MarkovTest {
    @Test
    public void test() {
        TrainMarkov trainMarkov = new TrainMarkov();
        trainMarkov.execute();
        trainMarkov.writeStateToFile();

        double[][] writeState = trainMarkov.getMarkovState();
        double[][] readState = FileHelper.readStateFromFile();

        for (int i = 0; i < LatencyExperimentConfig.MARKOV_MATRIX_SIZE; ++i) {
            for (int j = 0; j < LatencyExperimentConfig.MARKOV_MATRIX_SIZE; ++j) {
                Assertions.assertEquals(writeState[i][j], readState[i][j]);
            }
        }
    }
}
