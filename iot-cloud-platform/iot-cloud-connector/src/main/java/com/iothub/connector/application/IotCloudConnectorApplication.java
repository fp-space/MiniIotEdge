package com.iothub.connector.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.config.EnableIntegration;

@SpringBootApplication
@EnableIntegration
public class IotCloudConnectorApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(IotCloudConnectorApplication.class, args);
    }
    
}