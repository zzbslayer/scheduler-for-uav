package com.github.zzbslayer.simulator.core.latency.prediction.sprediction;

import com.github.zzbslayer.simulator.config.LatencyExperimentConfig;
import com.github.zzbslayer.simulator.core.latency.prediction.mapper.AccessInstanceMapper;
import com.github.zzbslayer.simulator.core.latency.prediction.ServicePrediction;
import com.github.zzbslayer.simulator.core.markov.FileHelper;

import java.util.Deque;
import java.util.Random;

public class MarkovPrediction extends ServicePrediction {
    private double[][] markovState;
    private Random random = new Random();
    private AccessInstanceMapper mapper = AccessInstanceMapper.getMapper();

    public MarkovPrediction(int nodeNum, int serviceNum) {
        super(nodeNum, serviceNum);
        this.markovState = FileHelper.readStateFromFile();
    }

    /**
     * input access so output access
     */
    @Override
    protected int predictOne(int service, Deque<Integer> queue) {
        int lastValue = queue.getLast();
        int lastState = mapper.accessToInstance(lastValue);

        double[] probabilities = markovState[lastState];
        double randomNext = random.nextDouble();
        int nextState = -1;
        for (int i = 0; i < LatencyExperimentConfig.MARKOV_MATRIX_SIZE; ++i) {
            if (randomNext < probabilities[i]) {
                nextState = i;
                break;
            }
        }
        if (nextState == -1) {
            nextState = lastState; // markov fail
        }
        return mapper.instanceToAccess(nextState);
    }
}
