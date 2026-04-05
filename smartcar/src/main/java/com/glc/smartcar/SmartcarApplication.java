package com.glc.smartcar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class SmartcarApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartcarApplication.class, args);
	}

}
