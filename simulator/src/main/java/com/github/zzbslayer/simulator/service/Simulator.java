package com.github.zzbslayer.simulator.service;

import com.github.zzbslayer.simulator.config.SimulatorConfig;
import com.github.zzbslayer.simulator.core.SimulatorCsvReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class Simulator {
    @Autowired
    SimulatorConfig simulatorConfig;

    public void run(){
        SimulatorCsvReader simulatorCsvReader = new SimulatorCsvReader(simulatorConfig.DATASET_PATH);

        try {
            simulatorCsvReader.processCsv();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
