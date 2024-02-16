package com.rapeech.vbc.vbgw_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = {"com.rapeech.vbc"})
public class VbgwMangerTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(VbgwMangerTestApplication.class, args);
		System.err.println("Hello, World!");
	}

}