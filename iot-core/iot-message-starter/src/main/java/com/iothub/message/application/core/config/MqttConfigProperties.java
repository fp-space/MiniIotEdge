package com.iothub.message.application.core.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
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
     
     public Integer qos(){
         return this.getQos();
     }

    public String brokerUrl() {
        return this.getBrokerUrl();
    }

    public String password() {
        return this.getPassword();
    }

    public String username() {
        return this.getUsername();
    }

    public String clientId() {
         return this.getClientId();
    }

    public List<String> subscriptions() {
         return this.getSubscriptions();
    }

    public String connectionTimeout() {
         return this.getConnectionTimeout();
    }
}
