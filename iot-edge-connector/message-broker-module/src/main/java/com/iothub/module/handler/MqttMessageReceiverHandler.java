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
public class MqttMessageReceiverHandler {
    
    private final Map<MessageType, IotMessageProcessor> processors;
    
    public MqttMessageReceiverHandler(Map<MessageType, IotMessageProcessor> processors) {
        this.processors = processors;
    }
    
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {
        // 日志记录收到的消息内容和主题
        byte[] payload = (byte[]) message.getPayload();
        String content = new String(payload);  // 如果负载是字符串
        String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC, String.class);
        
        // 输出收到的消息和主题
        log.info("Received message from topic: {} with payload: {}", topic, content);
        
        // 从消息头中提取 MessageType
        String messageTypeHeader = message.getHeaders().get("MessageType", String.class);
        MessageType messageType = MessageType.match(messageTypeHeader);
        
        log.info("MessageType extracted from header: {}", messageType);
        
        // 获取对应的处理器
        IotMessageProcessor processor = processors.get(messageType);
        
        if (Objects.nonNull(processor)) {
            log.info("Processing message with processor: {}", processor.getClass().getName());
            processor.process(topic, content);
        } else {
            log.error("No processor found for MessageType: {}", messageType);
        }
    }

}
