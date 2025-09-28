package com.example.projeto.api_itau;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication

@ComponentScan(basePackages = "com.desafio") 
public class ApiItauApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiItauApplication.class, args);
	}

}