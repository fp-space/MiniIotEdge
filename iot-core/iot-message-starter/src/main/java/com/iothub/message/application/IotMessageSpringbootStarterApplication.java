package com.iothub.message.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class IotMessageSpringbootStarterApplication {
    public static void main(String[] args) {
        log.info("Starting IotMessageSpringbootStarterApplication");
        SpringApplication.run(IotMessageSpringbootStarterApplication.class, args);
    }
}
