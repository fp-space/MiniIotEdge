package com.iothub.module.handler;

import com.iothub.module.enums.MessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.outbound.Mqttv5PahoMessageHandler;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
/**
 * 发送消息的类，利用 MqttPahoMessageHandler 发送消息
 */
@Slf4j
public class MqttMessageSenderHandler {
    
    private final Mqttv5PahoMessageHandler mqttMessageHandler;
    
    // 构造函数注入 MqttPahoMessageHandler
    public MqttMessageSenderHandler(Mqttv5PahoMessageHandler mqttMessageHandler) {
        this.mqttMessageHandler = mqttMessageHandler;
    }
    
    /**
     * 发送 MQTT 消息
     *
     * @param topic   MQTT 主题
     * @param content 消息内容
     * @param messageType 消息类型
     */
    public void send(String topic, String content, MessageType messageType) {
        // 创建消息头，加入 MessageType 信息
        Message<String> message = MessageBuilder.withPayload(content)
                .setHeader(MqttHeaders.TOPIC, topic)   // 设置主题
                .setHeader("MessageType", messageType.getType()) // 设置消息类型
                .build();
        
        // 使用 MqttPahoMessageHandler 发送消息
        try {
            mqttMessageHandler.handleMessage(message);
            log.info("Sent message to Topic: {}, Type: {}", topic, messageType);
        } catch (Exception e) {
            log.error("Failed to send message to Topic: {}, Type: {}", topic, messageType, e);
        }
    }
}