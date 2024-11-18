package com.iothub.module.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.integration.core.MessageProducer;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

@Configuration
@Slf4j
public class MqttAdapterConfig {
    
    private final MqttConfigProperties mqttConfigProperties;
    
    // 构造函数注入配置类
    public MqttAdapterConfig(MqttConfigProperties mqttConfigProperties) {
        this.mqttConfigProperties = mqttConfigProperties;
    }
    
    /**
     * 配置工厂
     */
    @Bean
    public MqttPahoClientFactory mqttSubscriberFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(mqttConnectOptions());
        return factory;
    }
    
    
    /**
     * 创建 MqttConnectOptions 实例
     */
    private MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{mqttConfigProperties.brokerUrl()});
        options.setUserName(mqttConfigProperties.username());   // 设置用户名
        options.setPassword(mqttConfigProperties.password().toCharArray());   // 设置密码
        options.setConnectionTimeout(Integer.parseInt(mqttConfigProperties.connectionTimeout().replace("s", "")));  // 设置连接超时（秒）
        options.setKeepAliveInterval(Integer.parseInt(mqttConfigProperties.keepAliveInterval().replace("s", "")));  // 设置保持连接时间（秒）
        return options;
    }
    
    /**
     * 接收通道
     */
    @Bean
    public MessageChannel mqttInputChannel() {
        // 点对点（一个消费者接收一条消息） - 每次只有一个消费者处理消息
//        return new DirectChannel();
        
        // 广播（所有订阅者接收消息）
        return new PublishSubscribeChannel();
    }
    
    /**
     * 配置client, 监听的topic
     */
    @Bean
    public MessageProducer inbound() {
        // 从 mqttConfigProperties 获取订阅的主题
        String[] subTopicArr = mqttConfigProperties.subscriptions().toArray(new String[0]);
        
        // 创建适配器，订阅多个主题
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                mqttConfigProperties.clientId() + "_inbound",
                mqttSubscriberFactory(), subTopicArr
        );
        
        // 设置连接超时时间
        adapter.setCompletionTimeout(Integer.parseInt(mqttConfigProperties.connectionTimeout().replace("s", "")) * 1000L);
        
        // 使用默认的消息转换器，转换消息负载
        DefaultPahoMessageConverter converter = new DefaultPahoMessageConverter();
        converter.setPayloadAsBytes(true);
        adapter.setConverter(converter);
        
        // 设置QoS
        adapter.setQos(mqttConfigProperties.qos() != null ? mqttConfigProperties.qos() : 1);
        
        // 配置输出通道
        adapter.setOutputChannel(mqttInputChannel());
        log.info("MQTT subscribe Topics = {}", (Object) subTopicArr);
        return adapter;
    }
}
