package com.rapeech.vbc.vbgw_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@ComponentScan(basePackages = { "com.rapeech.vbc" })
@EnableScheduling
public class VbgwMangerTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(VbgwMangerTestApplication.class, args);
		System.err.println("Hello, World!");
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}