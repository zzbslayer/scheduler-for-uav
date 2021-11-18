package com.github.zzbslayer.simulator.service;

import com.github.zzbslayer.simulator.config.SimulatorConfig;
import com.github.zzbslayer.simulator.utils.CsvReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class SimulatorService {
    @Autowired
    SimulatorConfig simulatorConfig;

    @Autowired
    @Qualifier("simulator-executor")
    Executor executor;

    public void run(){
        String dataset = simulatorConfig.DATASET_PATH;
        String host = simulatorConfig.HOST;
        CsvReader csvReader = new CsvReader() {
            @Override
            public void processHead(String head) {

            }

            @Override
            public void processLine(String line) {
                String[] items = line.split(",");
                String source = items[0];
                long timestamp = (long) Double.parseDouble(items[1]);
                String http = items[2];
                log.debug("Source: {}  Timestamp: {}  http: {}", source, timestamp, http);
            }
        };
        try {
            csvReader.processCsv(dataset);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Date date = new Date(1548100574000L);
        System.out.println(date);
    }
}
