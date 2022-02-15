package com.github.zzbslayer.simulator.service;

import com.github.zzbslayer.simulator.core.availability.GraphAvailabilitySimulation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GraphAvailabilitySimulator {


    public void run() {
        GraphAvailabilitySimulation.simulate(1000);
    }


}
