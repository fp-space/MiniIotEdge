package com.iothub.message.broker.module.handler;

import com.iothub.message.broker.module.annotations.Timed;
import com.iothub.message.broker.module.service.IotMessageProcessor;
import com.iothub.message.broker.module.enums.MessageTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class MqttMessageReceiverHandler {
    
    private static final int TIMEOUT = 10; // 超时时间10秒
    
    private final Map<MessageTypeEnum, IotMessageProcessor> processors;
    
    public MqttMessageReceiverHandler(Map<MessageTypeEnum, IotMessageProcessor> processors) {
        this.processors = processors;
    }
    
    @Timed
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {
        
        byte[] payload = (byte[]) message.getPayload();
        String content = new String(payload);  // 如果负载是字符串
        String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC, String.class);
        
        log.info("Received message from topic: {} with message: {}", topic, message);
        
        String messageTypeHeader = message.getHeaders().get("MessageType", String.class);
        String messageSourceTypeHeader = message.getHeaders().get("MessageSourceType", String.class);
        MessageTypeEnum messageType = MessageTypeEnum.match(messageTypeHeader);
        
        log.info("MessageType extracted from header: {}", messageType);
        
        // 获取对应的处理器
        IotMessageProcessor processor = processors.get(messageType);
        
        if (Objects.nonNull(processor)) {
            log.info("Processing message with processor: {}", processor.getClass().getName());
            task(processor, content, topic);
        } else {
            log.error("No processor found for MessageType: {}", messageType);
        }
        
    }
    
    private static void task(IotMessageProcessor processor, String content, String topic) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> processor.process(topic, content), executor)
                    .completeOnTimeout(null, TIMEOUT, TimeUnit.SECONDS)
                    .exceptionally(ex -> {
                        log.error("Task timed out or failed: " + ex.getMessage());
                        return null;
                    });
            // 等待任务完成或超时
            try {
                future.join();  // 等待处理结果
            } catch (Exception e) {
                log.error("Error waiting for task completion: " + e.getMessage());
            }
        }
    }
    
}
