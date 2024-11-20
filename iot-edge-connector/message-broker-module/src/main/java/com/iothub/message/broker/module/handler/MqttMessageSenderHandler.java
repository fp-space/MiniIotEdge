package com.iothub.message.broker.module.handler;

import com.iothub.message.broker.module.enums.MessageTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * 发送消息的类，利用 MqttPahoMessageHandler 发送消息
 */
@Slf4j
@Component
public class MqttMessageSenderHandler {
    

    private final MessageHandler mqttMessageHandler;
    
    // 通过构造函数注入 Mqttv5PahoMessageHandler
    public MqttMessageSenderHandler(@Qualifier(value = "mqttOutbound") MessageHandler mqttMessageHandler) {
        this.mqttMessageHandler = mqttMessageHandler;
    }
    
    /**
     * 发送 MQTT 消息
     *
     * @param topic   MQTT 主题
     * @param content 消息内容
     * @param messageType 消息类型
     */
    public void send(String topic, String content, @NonNull MessageTypeEnum messageType) {
        // 创建消息头，加入 MessageType 信息
        Message<String> message = MessageBuilder.withPayload(content)
                .setHeader(MqttHeaders.TOPIC, topic)   // 设置主题
                .setHeader("MessageType", messageType.getType()) // 设置消息类型
                .setHeader("MessageId", UUID.randomUUID().toString())
                .build();
        
        log.info("message:{}", message);
        
        // 使用 MqttPahoMessageHandler 发送消息
        try {
            mqttMessageHandler.handleMessage(message);
            log.info("Sent message to Topic: {}, Type: {}", topic, messageType);
        } catch (Exception e) {
            log.error("Failed to send message to Topic: {}, Type: {}", topic, messageType, e);
        }
    }
    
    /**
     * 发送 MQTT 消息
     *
     * @param topic   MQTT 主题
     * @param content 消息内容
     */
    public void send(String topic, String content) {
        // 创建消息头，加入 MessageType 信息
        Message<String> message = MessageBuilder.withPayload(content)
                .setHeader(MqttHeaders.TOPIC, topic)   // 设置主题
                .setHeader("MessageId", UUID.randomUUID().toString())
                .build();
        
        // 使用 MqttPahoMessageHandler 发送消息
        try {
            mqttMessageHandler.handleMessage(message);
            log.info("Sent message to Topic: {}", topic);
        } catch (Exception e) {
            log.error("Failed to send message to Topic: {}, {}", topic, e.getMessage());
        }
    }
}