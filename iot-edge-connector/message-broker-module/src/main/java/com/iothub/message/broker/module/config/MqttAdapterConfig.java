package com.iothub.message.broker.module.config;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.Mqttv5ClientManager;
import org.springframework.integration.mqtt.inbound.Mqttv5PahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.Mqttv5PahoMessageHandler;
import org.springframework.integration.mqtt.support.MqttHeaderMapper;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.SmartMessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;

import java.util.Objects;

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
    @Bean
    public MqttConnectionOptions mqttConnectionOptions() {
        MqttConnectionOptions options = new MqttConnectionOptions();
        options.setServerURIs(new String[]{Objects.requireNonNull(mqttConfigProperties).brokerUrl()});
        options.setCleanStart(true);
        options.setConnectionTimeout(30); // 默认连接超时30秒
        options.setKeepAliveInterval(60); // 默认保持活动时间60秒
        options.setUserName(mqttConfigProperties.username());
        options.setPassword(mqttConfigProperties.password().getBytes());
//        options.setAutomaticReconnect(true);
        return options;
    }
    
    // 配置 MQTT 客户端连接信息
    @Bean
    public MqttClient mqttClient(MqttConnectionOptions mqttConnectionOptions) throws MqttException {
        String clientId = mqttConfigProperties.clientId();
        String brokerUrl = mqttConfigProperties.brokerUrl();
        
        // 创建 MQTT 客户端实例
        MqttClient client = new MqttClient(brokerUrl, clientId);
        
        client.connect(mqttConnectionOptions);
        return client;
    }
    
    // 配置 Mqttv5ClientManager
    @Bean
    public Mqttv5ClientManager mqttv5ClientManager(MqttClient mqttClient,MqttConnectionOptions mqttConnectionOptions) {
        Mqttv5ClientManager mqttv5ClientManager = new Mqttv5ClientManager(mqttConnectionOptions, mqttClient.getClientId());
        mqttv5ClientManager.start();
        return mqttv5ClientManager;
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
    public MessageProducer inbound(MqttConnectionOptions mqttConnectionOptions) {
        String[] subTopicArr = mqttConfigProperties.subscriptions().toArray(new String[0]);
        
        // 使用 clientManager 获取客户端并创建消息适配器
        Mqttv5PahoMessageDrivenChannelAdapter adapter = new Mqttv5PahoMessageDrivenChannelAdapter(
                mqttConnectionOptions,
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
     * 配置发送消息的通道（生产者）
     */
    @Bean
    public MessageChannel mqttOutputChannel() {
        return new DirectChannel();  // 直接通道
    }
    
    /**
     * 配置发送消息的处理器
     */
    @Bean(name = "mqttOutbound")
    public Mqttv5PahoMessageHandler mqttOutbound(MqttConnectionOptions  mqttConnectionOptions) {
        log.info("MQTT消息处理器（生产者）clientId:{}", mqttConfigProperties.clientId());

        // 创建消息发送处理器
        Mqttv5PahoMessageHandler messageHandler = new Mqttv5PahoMessageHandler(mqttConnectionOptions,"Out-" +mqttConfigProperties.clientId());
        messageHandler.setHeaderMapper(new MqttHeaderMapper());
        messageHandler.setAsync(true);  // 设置异步发送
        messageHandler.setDefaultQos(mqttConfigProperties.qos());  // 设置默认QoS

        // 使用 SmartMessageConverter 替换默认的消息转换器
        SmartMessageConverter smartMessageConverter = new StringMessageConverter();
        messageHandler.setConverter(smartMessageConverter);   // 设置消息转换器

        return messageHandler;
    }

}
