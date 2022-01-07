package com.github.zzbslayer.simulator.service;

import com.github.zzbslayer.simulator.config.RequestSimulatorConfig;
import com.github.zzbslayer.simulator.core.request.SimulatorCsvReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class RequestSimulator {
    @Autowired
    RequestSimulatorConfig requestSimulatorConfig;

    public void run(){
        SimulatorCsvReader simulatorCsvReader = new SimulatorCsvReader(requestSimulatorConfig.DATASET_PATH);

        try {
            simulatorCsvReader.processCsv();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
