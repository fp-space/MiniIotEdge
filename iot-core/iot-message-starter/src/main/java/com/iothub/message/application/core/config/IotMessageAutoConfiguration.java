package com.iothub.message.application.core.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.iothub.message.application")
@EnableConfigurationProperties({MqttConfigProperties.class, IotMessageConfigProperties.class})
public class IotMessageAutoConfiguration {
}
