package com.iothub.module.config;

import com.iothub.module.handler.MqttMessageSenderHandler;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.Mqttv5ClientManager;
import org.springframework.integration.mqtt.inbound.Mqttv5PahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.Mqttv5PahoMessageHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.SmartMessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;

@Configuration
@Slf4j
@EnableConfigurationProperties(MqttConfigProperties.class)
public class MqttAdapterConfig {
    private final MqttConfigProperties mqttConfigProperties;
    
    public MqttAdapterConfig(MqttConfigProperties mqttConfigProperties) {
        this.mqttConfigProperties = mqttConfigProperties;
    }
    
    /**
     * 配置 MQTT 连接选项
     */
    private MqttConnectionOptions createMqttConnectionOptions() {
        MqttConnectionOptions options = new MqttConnectionOptions();
        options.setServerURIs(new String[]{mqttConfigProperties.brokerUrl()});
        options.setCleanStart(true);
        options.setConnectionTimeout(30); // 默认连接超时30秒
        options.setKeepAliveInterval(60); // 默认保持活动时间60秒
        options.setUserName(mqttConfigProperties.username());
        options.setPassword(mqttConfigProperties.password().getBytes());
//        options.setAutomaticReconnect(true);
        return options;
    }
    
    /**
     * 提取 Mqttv5ClientManager 为单独的 @Bean
     */
    @Bean
    public Mqttv5ClientManager mqttv5ClientManager() {
        MqttConnectionOptions options = createMqttConnectionOptions();
        return new Mqttv5ClientManager(options, mqttConfigProperties.clientId());
    }
    
    /**
     * 接收消息的通道
     */
    @Bean
    public MessageChannel mqttInputChannel() {
        return new PublishSubscribeChannel();
    }
    
    /**
     * 配置 MQTT 5 客户端管理器及消息适配器
     */
    @Bean
    public MessageProducer inbound() {
        String[] subTopicArr = mqttConfigProperties.subscriptions().toArray(new String[0]);
        
        // 使用 clientManager 获取客户端并创建消息适配器
        Mqttv5PahoMessageDrivenChannelAdapter adapter = new Mqttv5PahoMessageDrivenChannelAdapter(
                createMqttConnectionOptions(),
                mqttConfigProperties.clientId(),
                subTopicArr
        );
        
        adapter.setCompletionTimeout(Integer.parseInt(mqttConfigProperties.connectionTimeout().replace("s", "")) * 1000L);
        
        adapter.setQos(mqttConfigProperties.qos() != null ? mqttConfigProperties.qos() : 1);  // 设置 QoS
        adapter.setOutputChannel(mqttInputChannel());  // 设置输出通道
        
        // 使用 SmartMessageConverter 替换默认的消息转换器
        SmartMessageConverter smartMessageConverter = new StringMessageConverter();
        adapter.setMessageConverter(smartMessageConverter);   // 设置消息转换器
        
        log.info("MQTT subscribe Topics = {}", (Object) subTopicArr);
        return adapter;  // 返回消息适配器
    }
    
    /**
     * 配置发送消息的处理器
     */
    @Bean
    public Mqttv5PahoMessageHandler mqttMessageHandler(Mqttv5ClientManager clientManager) {
        Mqttv5PahoMessageHandler handler = new Mqttv5PahoMessageHandler(clientManager);
        handler.setDefaultTopic(mqttConfigProperties.defaultTopic());
        handler.setAsync(true);
        return handler;
    }
    
    /**
     * 配置消息发送处理器
     */
    @Bean
    public MqttMessageSenderHandler mqttMessageSenderHandler(Mqttv5PahoMessageHandler mqttMessageHandler) {
        return new MqttMessageSenderHandler(mqttMessageHandler);
    }
}
