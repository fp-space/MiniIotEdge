package com.iothub.module;

import com.iothub.module.config.MqttConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.integration.config.EnableIntegration;

@SpringBootApplication
@EnableIntegration
public class MessageBrokerModuleApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessageBrokerModuleApplication.class, args);
	}

}