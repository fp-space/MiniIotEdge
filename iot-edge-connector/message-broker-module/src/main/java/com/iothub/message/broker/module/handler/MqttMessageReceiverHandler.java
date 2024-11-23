package com.iothub.message.broker.module.handler;

import com.iothub.message.broker.module.annotations.Timed;
import com.iothub.message.broker.module.queue.MqttMessageQueue;
import com.iothub.message.broker.module.service.IotMessageProcessor;
import com.iothub.message.broker.module.enums.MessageTypeEnum;
import com.iothub.message.broker.module.utils.TimerUtil;
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
    private final MqttMessageQueue<Message<?>> messageQueue;
    
    public MqttMessageReceiverHandler(Map<MessageTypeEnum, IotMessageProcessor> processors, MqttMessageQueue<Message<?>> messageQueue) {
        this.processors = processors;
        this.messageQueue = messageQueue;
        
        startMessageProcessor(); // 启动队列消费线程
    }
    
    @Timed
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {
        messageQueue.put(message);
    }
    
    private void startMessageProcessor() {
        // 创建一个虚拟线程来处理消息
        Runnable task = () -> {
            while (true) {
                try {
                    // 从队列中获取消息，等待超时时间为 TIMEOUT
                    Message<?> message = messageQueue.poll(TIMEOUT, TimeUnit.SECONDS);
                    
                    if (Objects.nonNull(message)) {
                        TimerUtil.startTimer("默认测试");
                        // 如果成功获取到消息，进行处理
                        processMessage(message);
                        TimerUtil.stopTimer("默认测试");
                        
                    } else {
                        // 如果超时未获取到消息，可以选择打印调试日志或执行其他操作
                        log.debug("No messages in queue within the timeout period.");
                    }
                } catch (InterruptedException e) {
                    // 捕获中断异常，重新设置中断标志，并打印日志
                    Thread.currentThread().interrupt();
                    log.error("Message processing thread was interrupted", e);
                } catch (Exception e) {
                    // 捕获其他异常，防止线程崩溃
                    log.error("Unexpected error in message processing", e);
                }
            }
        };
        
        // 启动线程
        Thread.ofVirtual().start(task);
    }
    
    /**
     * 处理单条消息
     */
    private void processMessage(Message<?> message) {
        byte[] payload = (byte[]) message.getPayload();
        String content = new String(payload); // 转换负载为字符串
        String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC, String.class);
        
        log.info("Processing message from topic: {} with content: {}", topic, content);
        
        String messageTypeHeader = message.getHeaders().get("MessageType", String.class);
        MessageTypeEnum messageType = MessageTypeEnum.match(messageTypeHeader);
        
        log.info("MessageType extracted: {}", messageType);
        
        IotMessageProcessor processor = processors.get(messageType);
        
        if (Objects.nonNull(processor)) {
            log.info("Found processor: {}. Processing message...", processor.getClass().getName());
            processWithTimeout(processor, content, topic);
        } else {
            log.error("No processor found for MessageType: {}", messageType);
        }
    }
    
    /**
     * 使用虚拟线程处理消息并设置超时时间
     */
    private void processWithTimeout(IotMessageProcessor processor, String content, String topic) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> processor.process(topic, content), executor)
                    .completeOnTimeout(null, TIMEOUT, TimeUnit.SECONDS)
                    .exceptionally(ex -> {
                        log.error("Processing task failed or timed out: {}", ex.getMessage());
                        return null;
                    });
            future.join(); // 等待任务完成
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage());
        }
    }
}
