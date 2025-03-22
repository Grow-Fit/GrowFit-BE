package com.project.growfit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class GrowfitApplication {

	public static void main(String[] args) {
		SpringApplication.run(GrowfitApplication.class, args);
	}

}
