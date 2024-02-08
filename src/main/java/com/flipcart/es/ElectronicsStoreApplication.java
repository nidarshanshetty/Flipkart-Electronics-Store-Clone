package com.flipcart.es;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableAsync(proxyTargetClass = true	)
public class ElectronicsStoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElectronicsStoreApplication.class, args);
	}

}
