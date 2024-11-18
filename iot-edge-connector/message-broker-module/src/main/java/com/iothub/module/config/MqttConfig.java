package com.iothub.module.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;

@Configuration
@EnableConfigurationProperties(MqttConfigProperties.class)
public class MqttConfig {
    
    private final MqttConfigProperties mqttConfigProperties;
    
    // 注入 MqttConfigProperties 配置类
    public MqttConfig(MqttConfigProperties mqttConfigProperties) {
        this.mqttConfigProperties = mqttConfigProperties;
    }
    
    /**
     * 配置 MQTT 客户端工厂
     */
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        
        // 使用外部配置
        options.setServerURIs(new String[]{mqttConfigProperties.brokerUrl()});
        options.setUserName(mqttConfigProperties.username());
        options.setPassword(mqttConfigProperties.password().toCharArray());
        options.setConnectionTimeout(Integer.parseInt(mqttConfigProperties.connectionTimeout().replace("s", "")));  // 连接超时（单位：秒）
        options.setKeepAliveInterval(Integer.parseInt(mqttConfigProperties.keepAliveInterval().replace("s", "")));  // 保持连接的间隔（单位：秒）
        
        factory.setConnectionOptions(options);
        return factory;
    }
}
