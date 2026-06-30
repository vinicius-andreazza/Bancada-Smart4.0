package com.smart.appsa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AppsaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppsaApplication.class, args);
	}

}
