package com.github.zzbslayer.vnfdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class VnfDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(VnfDemoApplication.class, args);
	}

}
