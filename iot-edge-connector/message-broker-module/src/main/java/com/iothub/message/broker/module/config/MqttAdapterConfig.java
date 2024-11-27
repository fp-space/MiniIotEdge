package com.iothub.message.broker.module.config;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 配置 MQTT 适配器，用于接收和发送消息
 */
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
        return options;
    }
    
    /**
     * 配置 MQTT 客户端
     * @param mqttConnectionOptions MQTT 连接选项
     * @return MqttClient 客户端实例
     * @throws MqttException MQTT 异常
     */
    @Bean
    public MqttAsyncClient mqttAsyncClient(MqttConnectionOptions mqttConnectionOptions) throws MqttException {
        String clientId = mqttConfigProperties.clientId() + "-" + System.currentTimeMillis();
        String brokerUrl = mqttConfigProperties.brokerUrl();
        
        // 创建 MQTT 客户端实例
        MqttAsyncClient client = new MqttAsyncClient(brokerUrl, clientId);
        
        // 使用 AtomicBoolean 标记是否正在重连，防止多次重连
        AtomicBoolean reconnecting = new AtomicBoolean(false);
        
        // 设置回调处理
        client.setCallback(new MqttCallback() {
            @Override
            public void disconnected(MqttDisconnectResponse mqttDisconnectResponse) {
                // 处理连接丢失，自动重连
                log.error("MQTT连接丢失，正在重连...");
                
                // 如果没有在进行重连，才启动重连逻辑
                if (reconnecting.compareAndSet(false, true)) {
                    reconnect(client, mqttConnectionOptions, reconnecting);
                }
            }
            
            @Override
            public void mqttErrorOccurred(MqttException e) {
                log.error("MQTT错误: {}", e.getMessage());
            }
            
            @Override
            public void messageArrived(String topic, org.eclipse.paho.mqttv5.common.MqttMessage message) {
                // 处理消息
            }
            
            @Override
            public void deliveryComplete(IMqttToken iMqttToken) {
                // 消息发送完成后的处理
            }
            
            @Override
            public void connectComplete(boolean b, String s) {
                // 连接成功后的处理
                log.info("MQTT连接完成: {}", s);
            }
            
            @Override
            public void authPacketArrived(int i, MqttProperties mqttProperties) {
                // 认证信息包到达时的处理
            }
        });
        
        client.connect(mqttConnectionOptions);
        return client;
    }
    
    // 自动重连机制
    private void reconnect(MqttAsyncClient client, MqttConnectionOptions options, AtomicBoolean reconnecting) {
        try {
            if (!client.isConnected()) {
                log.info("尝试重新连接 MQTT...");
                client.connect(options);
                log.info("MQTT连接成功");
            }
        } catch (MqttException e) {
            log.error("MQTT重新连接失败，等待5秒后重试...");
            try {
                TimeUnit.SECONDS.sleep(5);  // 等待一段时间后重试
                reconnect(client, options, reconnecting);  // 递归重试
            } catch (InterruptedException interruptedException) {
                log.error("重试时发生中断异常: {}", interruptedException.getMessage());
            }
        } finally {
            // 重连完成后将 reconnecting 标志恢复为 false
            reconnecting.set(false);
        }
    }
    
    /**
     * 配置 Mqttv5 客户端管理器
     * @param mqttAsyncClient MQTT 客户端
     * @param mqttConnectionOptions 连接选项
     * @return Mqttv5ClientManager 管理器实例
     */
    @Bean
    public Mqttv5ClientManager mqttv5ClientManager(MqttAsyncClient mqttAsyncClient, MqttConnectionOptions mqttConnectionOptions) {
        Mqttv5ClientManager mqttv5ClientManager = new Mqttv5ClientManager(mqttConnectionOptions, mqttAsyncClient.getClientId());
        mqttv5ClientManager.start();
        return mqttv5ClientManager;
    }
    
    /**
     * 配置接收消息的通道
     * @return MessageChannel 接收消息通道
     */
    @Bean
    public MessageChannel mqttInputChannel() {
        return new PublishSubscribeChannel();  // 发布-订阅通道，用于接收消息
    }
    
    /**
     * 配置 MQTT 订阅适配器，接收消息并发送到输入通道
     * @param mqttConnectionOptions 连接选项
     * @return MessageProducer 订阅适配器
     */
    @Bean
    public MessageProducer inbound(MqttConnectionOptions mqttConnectionOptions) {
        String[] subTopicArr = mqttConfigProperties.subscriptions().toArray(new String[0]);
        
        Mqttv5PahoMessageDrivenChannelAdapter adapter = new Mqttv5PahoMessageDrivenChannelAdapter(
                mqttConnectionOptions, mqttConfigProperties.clientId(), subTopicArr);
        
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
     * 配置发送消息的通道
     * @return MessageChannel 发送消息通道
     */
    @Bean
    public MessageChannel mqttOutputChannel() {
        return new DirectChannel();  // 直接通道，用于发送消息
    }
    
    /**
     * 配置发送消息的处理器
     */
    @Bean
    public Mqttv5PahoMessageHandler mqttv5PahoMessageHandler(Mqttv5ClientManager mqttv5ClientManager) {
        String clientId = "mqttOutbound-" + mqttConfigProperties.clientId();  // 生成唯一的客户端ID
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
