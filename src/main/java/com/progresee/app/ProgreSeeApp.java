package com.progresee.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ProgreSeeApp {

	public static void main(String[] args) {
		SpringApplication.run(ProgreSeeApp.class, args);
		System.out.println("Go");
	}

	
}
