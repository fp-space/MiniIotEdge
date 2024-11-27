package com.iothub.message.broker.module.core.handler;

import com.iothub.message.broker.module.annotations.Timed;
import com.iothub.message.broker.module.enums.MessageTypeEnum;
import com.iothub.message.broker.module.utils.queue.MqttMessageQueue;
import com.iothub.message.broker.module.core.processor.IotMessageProcessor;
import com.iothub.message.broker.module.utils.TimerUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

@Component
@Slf4j
public class MqttMessageReceiverHandler {
    
    private static final int TIMEOUT = 10; // 超时时间10秒
    private static final int PROCESS_TIMEOUT = 5; // 消息处理的超时时间5秒
    
    private final Map<MessageTypeEnum, IotMessageProcessor> processors;
    private final MqttMessageQueue<Message<?>> messageQueue;
    private final ExecutorService virtualThreadPool; // 虚拟线程池，用于处理消息
    
    public MqttMessageReceiverHandler(Map<MessageTypeEnum, IotMessageProcessor> processors, MqttMessageQueue<Message<?>> messageQueue) {
        this.processors = processors;
        this.messageQueue = messageQueue;
        this.virtualThreadPool = Executors.newVirtualThreadPerTaskExecutor(); // 创建虚拟线程池
    }
    
    @Timed
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {
        messageQueue.put(message); // 将消息放入队列
    }
    
    @PostConstruct
    private void startMessageProcessor() {
        Runnable task = () -> {
            while (true) {
                try {
                    Message<?> message = messageQueue.poll(TIMEOUT, TimeUnit.SECONDS);
                    if (Objects.nonNull(message)) {
                        TimerUtil.startTimer("消息处理");
                        processMessage(message); // 处理消息
                        TimerUtil.stopTimer("消息处理");
                    } else {
                        log.debug("No messages in queue within the timeout period.");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("Message processing thread was interrupted", e);
                } catch (Exception e) {
                    log.error("Unexpected error in message processing", e);
                }
            }
        };
        
        // 启动虚拟线程池中的任务
        virtualThreadPool.submit(task);
    }
    
    /**
     * 处理单条消息
     */
    private void processMessage(Message<?> message) {
        String content = extractContent(message);
        String topic = extractTopic(message);
        MessageTypeEnum messageType = extractMessageType(message);
        
        if (messageType == null) {
            log.error("MessageType is missing or invalid in message.");
            return;
        }
        
        IotMessageProcessor processor = processors.get(messageType);
        if (processor != null) {
            log.info("Processing message with processor: {}", processor.getClass().getName());
            processWithTimeout(processor, content, topic);
        } else {
            log.error("No processor found for MessageType: {}", messageType);
        }
    }
    
    /**
     * 提取消息内容
     */
    private String extractContent(Message<?> message) {
        byte[] payload = (byte[]) message.getPayload();
        return new String(payload); // 转换负载为字符串
    }
    
    /**
     * 提取消息的主题
     */
    private String extractTopic(Message<?> message) {
        return message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC, String.class);
    }
    
    /**
     * 提取消息类型
     */
    private MessageTypeEnum extractMessageType(Message<?> message) {
        String messageTypeHeader = message.getHeaders().get("MessageType", String.class);
        return MessageTypeEnum.match(messageTypeHeader);
    }
    
    /**
     * 使用虚拟线程池处理消息，并设置超时时间
     */
    private void processWithTimeout(IotMessageProcessor processor, String content, String topic) {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                processor.process(topic, content); // 处理消息
            } catch (Exception e) {
                log.error("Error during message processing: {}", e.getMessage());
            }
        }, virtualThreadPool).completeOnTimeout(null, PROCESS_TIMEOUT, TimeUnit.SECONDS);
        
        // 等待任务完成或超时
        try {
            future.join();
        } catch (CompletionException ex) {
            log.error("Message processing failed or timed out", ex.getCause());
        }
    }
    
    @PreDestroy
    public void shutdown() {
        if (virtualThreadPool != null && !virtualThreadPool.isShutdown()) {
            virtualThreadPool.shutdown();
        }
    }
}
