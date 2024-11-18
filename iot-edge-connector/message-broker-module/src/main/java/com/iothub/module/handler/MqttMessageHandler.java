package com.iothub.module.handler;

import com.iothub.module.enums.MessageType;
import com.iothub.module.service.IotMessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class MqttMessageHandler {
    
    // 自动注入处理器 Map，键是 MessageType，值是对应的处理器
    private final Map<MessageType, IotMessageProcessor> processors;
    
    public MqttMessageHandler(Map<MessageType, IotMessageProcessor> processors) {
        this.processors = processors;
    }
    
    // 配置为接收从 mqttInputChannel 发送过来的消息
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {
        // 获取消息的负载（payload），并转换为字符串
        byte[] payload = (byte[]) message.getPayload();
        String content = new String(payload);  // 将字节数组转换为字符串
        String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC, String.class);
        
        // 根据消息类型查找合适的处理器并执行
        MessageType messageType = determineMessageType(Objects.requireNonNull(topic));
        IotMessageProcessor processor = processors.get(messageType);
        
        // 根据消息类型 - 事件回复 或者 正常的属性上报
        if (processor != null) {
            processor.process(topic, content);
        } else {
            log.error("No processor found for MessageType: {}", messageType);
        }
    }
    
    // 判断消息类型的逻辑
    private MessageType determineMessageType(String topic) {
        // 简化逻辑：您可以根据 topic 或 content 判断消息类型
        if (topic.contains("event")) {
            return MessageType.EVENT_REPLY;
        } else {
            return MessageType.ATTRIBUTE_REPORT;
        }
    }
}
