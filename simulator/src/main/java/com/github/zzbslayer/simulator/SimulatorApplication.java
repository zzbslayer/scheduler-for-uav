package com.github.zzbslayer.simulator;

import com.github.zzbslayer.simulator.service.GraphAvailabilitySimulator;
import com.github.zzbslayer.simulator.service.RequestSimulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SimulatorApplication implements CommandLineRunner {
	@Autowired
	RequestSimulator requestSimulator;

	@Autowired
	GraphAvailabilitySimulator graphAvailabilitySimulator;

	@Override
	public void run(String... args) {
		// requestSimulator.run();
		graphAvailabilitySimulator.run();
	}

	public static void main(String[] args) {
		SpringApplication.run(SimulatorApplication.class, args);
	}

}
