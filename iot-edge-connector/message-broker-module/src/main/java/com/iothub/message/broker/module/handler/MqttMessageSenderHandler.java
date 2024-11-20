package com.iothub.message.broker.module.handler;

import com.iothub.message.broker.module.enums.MessageTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.eclipse.paho.mqttv5.common.packet.UserProperty;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 发送 MQTT 消息的处理类，支持多种发送方式
 */
@Slf4j
@Component
public class MqttMessageSenderHandler {
    
    private final MessageHandler mqttMessageHandler;
    private final MqttClient mqttClient;
    
    // 构造函数通过依赖注入获取 Mqttv5PahoMessageHandler 和 MqttClient
    public MqttMessageSenderHandler(@Qualifier("mqttOutbound") MessageHandler mqttMessageHandler, MqttClient mqttClient) {
        this.mqttMessageHandler = mqttMessageHandler;
        this.mqttClient = mqttClient;
    }
    
    /**
     * 发送带有指定消息类型的 MQTT 消息
     *
     * @param topic     MQTT 主题
     * @param content   消息内容
     * @param messageType 消息类型
     */
    public void sendMessage(String topic, String content, @NonNull MessageTypeEnum messageType) {
        Message<String> message = createMessage(topic, content, messageType);
        sendUsingMessageHandler(message, topic);
    }
    
    /**
     * 发送带有默认消息类型的 MQTT 消息
     *
     * @param topic     MQTT 主题
     * @param content   消息内容
     */
    public void sendMessage(String topic, String content) {
        sendMessage(topic, content, MessageTypeEnum.UNKNOWN);
    }
    
    /**
     * 发送带有用户自定义属性的 MQTT 消息
     *
     * @param topic     MQTT 主题
     * @param content   消息内容
     */
    public void sendMessageWithUserAttrs(String topic, String content, MessageTypeEnum messageType) {
        MqttMessage mqttMessage = createMqttMessage(content);
        addUserProperties(mqttMessage, messageType);
        sendUsingMqttClient(topic, mqttMessage);
    }
    
    /**
     * 创建 Spring Integration 消息
     *
     * @param topic     MQTT 主题
     * @param content   消息内容
     * @param messageType 消息类型
     * @return 创建的 Spring Integration 消息
     */
    private Message<String> createMessage(String topic, String content, MessageTypeEnum messageType) {
        return MessageBuilder.withPayload(content)
                .setHeader(MqttHeaders.TOPIC, topic)
                .setHeader("MessageType", messageType.getType())
                .setHeader("MessageId", UUID.randomUUID().toString())
                .build();
    }
    
    /**
     * 创建 MQTT 消息对象
     *
     * @param content 消息内容
     * @return 创建的 MqttMessage 对象
     */
    private MqttMessage createMqttMessage(String content) {
        MqttMessage mqttMessage = new MqttMessage(content.getBytes());
        mqttMessage.setQos(1);
        mqttMessage.setRetained(false);
        if (mqttMessage.getProperties() == null) {
            mqttMessage.setProperties(new MqttProperties());
        }
        return mqttMessage;
    }
    
    /**
     * 为 MQTT 消息添加用户自定义属性
     *
     * @param mqttMessage   MqttMessage 对象
     * @param messageType   消息类型
     */
    private void addUserProperties(MqttMessage mqttMessage, MessageTypeEnum messageType) {
        Map<String, String> headers = new HashMap<>();
        headers.put("MessageType", messageType.getType());
        headers.put("MessageId", UUID.randomUUID().toString());
        
        headers.forEach((key, value) -> {
            if (value != null) {
                UserProperty userProperty = new UserProperty(key, value);
                mqttMessage.getProperties().getUserProperties().add(userProperty);
            }
        });
    }
    
    /**
     * 使用 MqttPahoMessageHandler 发送 Spring Integration 消息
     *
     * @param message 消息
     * @param topic   主题
     */
    private void sendUsingMessageHandler(Message<String> message, String topic) {
        try {
            mqttMessageHandler.handleMessage(message);
            log.info("Successfully sent message to topic: {}, message: {}", topic, message);
        } catch (Exception e) {
            log.error("Failed to send message to topic: {}, message: {}", topic, message, e);
        }
    }
    
    /**
     * 使用 MqttClient 发送消息
     *
     * @param topic       主题
     * @param mqttMessage MqttMessage 对象
     */
    private void sendUsingMqttClient(String topic, MqttMessage mqttMessage) {
        try {
            mqttClient.publish(topic, mqttMessage);
            log.info("Successfully sent message to topic: {}, message: {}", topic, mqttMessage);
        } catch (Exception e) {
            log.error("Failed to send message to topic: {}, message: {}", topic, mqttMessage, e);
        }
    }
}
