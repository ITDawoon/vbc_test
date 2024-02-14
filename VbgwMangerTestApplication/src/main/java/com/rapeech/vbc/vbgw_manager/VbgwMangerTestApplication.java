package com.rapeech.vbc.vbgw_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication()
@ComponentScan(basePackages = {"com.rapeech.vbc"})
@EntityScan(basePackages = {"com.rapeech.vbc.data.entity"})
public class VbgwMangerTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(VbgwMangerTestApplication.class, args);
		System.err.println("Hello, World!");
	}

}