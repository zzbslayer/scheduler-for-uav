package com.github.zzbslayer.autoscaling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AutoScalingApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutoScalingApplication.class, args);
	}

}
