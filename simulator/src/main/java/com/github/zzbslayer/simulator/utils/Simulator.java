package com.github.zzbslayer.simulator.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Simulator {
    private final String datasetPath;
    private final String host;

    public Simulator(String datasetPath, String host) {
        this.datasetPath = datasetPath;
        this.host = host;
    }

    public void run() {
      log.info("Simulator run start");
      log.info("SImulator run end");
    }
}
