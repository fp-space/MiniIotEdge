package com.iothub.message.broker.module.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ConfigurationProperties(prefix = "spring.mqtt")
public record MqttConfigProperties(
        String brokerUrl,
        String clientId,
        String username,
        String password,
        String connectionTimeout,
        String keepAliveInterval,
        String defaultTopic,
        List<String> subscriptions,
        Integer qos
) {
    
    @ConstructorBinding
    public MqttConfigProperties(String brokerUrl,
                                String clientId,
                                String username,
                                String password,
                                String connectionTimeout,
                                String keepAliveInterval,
                                String defaultTopic,
                                List<String> subscriptions, Integer qos) {
        this.brokerUrl = Optional.ofNullable(brokerUrl).orElse("tcp://localhost:1883");
        this.clientId = Optional.ofNullable(clientId).orElse("defaultClient");
        this.username = Optional.ofNullable(username).orElse("guest");
        this.password = Optional.ofNullable(password).orElse("guest");
        this.connectionTimeout = Optional.ofNullable(connectionTimeout).orElse("10s");
        this.keepAliveInterval = Optional.ofNullable(keepAliveInterval).orElse("30s");
        this.defaultTopic = Optional.ofNullable(defaultTopic).orElse("defaultTopic");
        this.subscriptions = Optional.ofNullable(subscriptions).orElse(new ArrayList<>());
        this.qos = Optional.ofNullable(qos).orElse(1);
    }
}
