package com.iothub.message.application.core.handler.mqtt;

import cn.hutool.core.util.StrUtil;
import com.iothub.message.application.core.config.IotMessageConfigProperties;
import com.iothub.message.application.core.handler.connector.IotMessageProcessor;
import com.iothub.message.application.enums.MessageSourceType;
import com.iothub.message.application.enums.MessageTypeEnum;
import com.iothub.message.application.utils.TimerUtil;
import com.iothub.message.application.utils.queue.MqttMessageQueue;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

@Component
@Slf4j
public class MqttMessageReceiverHandler {
    
    private static final int TIMEOUT = 10; // 超时时间10秒
    private static final int PROCESS_TIMEOUT = 5; // 消息处理的超时时间5秒
    

    private final ExecutorService virtualThreadPool; // 虚拟线程池，用于处理消息
    
    @Resource
    private Map<MessageTypeEnum, IotMessageProcessor> processors;
    @Resource
    private MqttMessageQueue<Message<?>> messageQueue;
    @Resource
    private IotMessageConfigProperties iotMessageConfigProperties;
    
    public MqttMessageReceiverHandler() {
        this.virtualThreadPool = Executors.newVirtualThreadPerTaskExecutor(); // 创建虚拟线程池
    }
    
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
        MessageSourceType messageSourceType = extractMessageSourceType(message);
        
        if(Objects.isNull(messageSourceType) || StrUtil.isEmpty(iotMessageConfigProperties.getTag())){
            log.error("请添加标识，当前支持的标识有：{}", Arrays.stream(MessageSourceType.values()).toArray());
            return;
        }
        
        // 需要接收的标识和发送不一致 或者 未知，可能有级联消费情况
        if(messageSourceType.getCode().equals(iotMessageConfigProperties.getTag())
                && !messageSourceType.equals(MessageSourceType.UNKNOWN)){
            return;
        }
        
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
    
    private MessageSourceType extractMessageSourceType(Message<?> message) {
        String messageSourceType = message.getHeaders().get("MessageSourceType", String.class);
        return MessageSourceType.match(messageSourceType);
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
