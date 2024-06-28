package com.sds.animalapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AnimalAppApplication {
	public static void main(String[] args) {
		SpringApplication.run(AnimalAppApplication.class, args);
	}

}
