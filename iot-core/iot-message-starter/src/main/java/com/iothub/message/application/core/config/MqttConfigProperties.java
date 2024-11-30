package com.iothub.message.application.core.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "spring.mqtt")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MqttConfigProperties {
    private String brokerUrl;
    private String clientId;
    private String username;
    private String password;
    private String connectionTimeout;
    private String keepAliveInterval;
    private String defaultTopic;
    private List<String> subscriptions;
    private Integer qos;
}
