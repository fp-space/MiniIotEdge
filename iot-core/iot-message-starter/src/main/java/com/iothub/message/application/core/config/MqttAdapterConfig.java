package com.iothub.message.application.core.config;

import com.iothub.message.application.core.listener.CustomMqttCallback;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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

import java.util.UUID;

/**
 * 配置 MQTT 适配器，用于接收和发送消息
 */
@Configuration
@Order(1)
@Slf4j
@EnableConfigurationProperties(MqttConfigProperties.class)
public class MqttAdapterConfig {
    
    @Resource
    private MqttConfigProperties mqttConfigProperties;
    
    /**
     * 配置 MQTT 连接选项
     *
     * @return MqttConnectionOptions 配置项
     */
    @Bean
    public MqttConnectionOptions mqttConnectionOptions() {
        MqttConnectionOptions options = new MqttConnectionOptions();
        options.setServerURIs(new String[]{mqttConfigProperties.brokerUrl()});
        options.setConnectionTimeout(600);  // 连接超时
        options.setKeepAliveInterval(600);  // 保持连接
        options.setUserName(mqttConfigProperties.username());
        options.setPassword(mqttConfigProperties.password().getBytes());
        options.setCleanStart(true);  // 保持会话
        options.setAutomaticReconnect(true);  // 启用自动重连
        return options;
    }
    
    private String getClientId(String tag) {
        return tag + "-" + mqttConfigProperties.clientId() + "-" + UUID.randomUUID();
    }
    
    /**
     * 配置 MQTT 客户端
     *
     * @param mqttConnectionOptions MQTT 连接选项
     * @return MqttClient 客户端实例
     */
    @SneakyThrows
    @Bean
    public MqttAsyncClient mqttAsyncClient(MqttConnectionOptions mqttConnectionOptions) {
        String clientId = this.getClientId("Iot");
        String brokerUrl = mqttConfigProperties.brokerUrl();

        // 创建 MQTT 客户端实例
        MqttAsyncClient client = new MqttAsyncClient(brokerUrl, clientId);

        // 设置回调处理
        client.setCallback(new CustomMqttCallback(client, mqttConnectionOptions));

        client.connect(mqttConnectionOptions);
        return client;
    }
    
    
    /**
     * 配置 Mqttv5 客户端管理器
     *
     * @param mqttConnectionOptions 连接选项
     * @return Mqttv5ClientManager 管理器实例
     */
    @Bean
    public Mqttv5ClientManager mqttv5ClientManager(MqttConnectionOptions mqttConnectionOptions) {
        Mqttv5ClientManager mqttv5ClientManager = new Mqttv5ClientManager(mqttConnectionOptions, this.getClientId("IotEdge"));
        mqttv5ClientManager.start();
        return mqttv5ClientManager;
    }
    
    /**
     * 配置 MQTT 订阅适配器，接收消息并发送到输入通道
     *
     * @param mqttConnectionOptions 连接选项
     * @return MessageProducer 订阅适配器
     */
    @Bean
    public MessageProducer inbound(MqttConnectionOptions mqttConnectionOptions) {
        String[] subTopicArr = mqttConfigProperties.subscriptions().toArray(new String[0]);
        
        Mqttv5PahoMessageDrivenChannelAdapter adapter = new Mqttv5PahoMessageDrivenChannelAdapter(
                mqttConnectionOptions, this.getClientId("In"), subTopicArr);
        
        // 设置 QoS 和完成超时
        adapter.setQos(mqttConfigProperties.qos() != null ? mqttConfigProperties.qos() : 1);
        adapter.setCompletionTimeout(Integer.parseInt(mqttConfigProperties.connectionTimeout().replace("s", "")) * 1000L);
        
        // 设置消息转换器
        SmartMessageConverter smartMessageConverter = new StringMessageConverter();
        adapter.setMessageConverter(smartMessageConverter);
        
        adapter.setOutputChannel(mqttInputChannel());  // 设置输出通道
        
        log.info("MQTT subscribe Topics = {}", (Object) subTopicArr);
        return adapter;  // 返回适配器
    }
    
    /**
     * 配置接收消息的通道
     *
     * @return MessageChannel 接收消息通道
     */
    @Bean
    public MessageChannel mqttInputChannel() {
        return new PublishSubscribeChannel();  // 发布-订阅通道，用于接收消息
    }
    
    /**
     * 配置发送消息的通道
     *
     * @return MessageChannel 发送消息通道
     */
    @Bean
    public MessageChannel mqttOutputChannel() {
        return new DirectChannel();  // 直接通道，用于发送消息
    }
    
    /**
     * 配置发送消息的处理器
     */
    @Bean(value = "mqttv5PahoMessageHandler")
    public Mqttv5PahoMessageHandler mqttv5PahoMessageHandler(Mqttv5ClientManager mqttv5ClientManager) {
        // 使用 UUID 生成唯一的 clientId
        String clientId = this.getClientId("Out");
        log.info("MQTT消息处理器（生产者）clientId:{}", clientId);
        
        // 创建消息发送处理器
        Mqttv5PahoMessageHandler messageHandler = new Mqttv5PahoMessageHandler(mqttv5ClientManager);
        messageHandler.start();
        messageHandler.setHeaderMapper(new MqttHeaderMapper());
        messageHandler.setAsync(true);  // 设置异步发送
        messageHandler.setDefaultQos(mqttConfigProperties.qos());  // 设置默认QoS
        
        return messageHandler;
    }

}
