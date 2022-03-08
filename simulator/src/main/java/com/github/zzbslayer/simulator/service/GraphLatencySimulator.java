package com.github.zzbslayer.simulator.service;

import com.github.zzbslayer.simulator.config.RequestSimulatorConfig;
import com.github.zzbslayer.simulator.core.latency.GraphLatencySimulation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class GraphLatencySimulator {
    @Autowired
    RequestSimulatorConfig requestSimulatorConfig;

    public void run(){
        GraphLatencySimulation graphLatencySimulation = GraphLatencySimulation.newInstance(requestSimulatorConfig.DATASET_PATH);

        try {
            graphLatencySimulation.processCsv();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
