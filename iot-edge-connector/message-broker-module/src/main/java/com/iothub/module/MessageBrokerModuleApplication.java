package com.iothub.module;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.integration.config.EnableIntegration;

@SpringBootApplication
@EnableIntegration
public class MessageBrokerModuleApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessageBrokerModuleApplication.class, args);
	}

}