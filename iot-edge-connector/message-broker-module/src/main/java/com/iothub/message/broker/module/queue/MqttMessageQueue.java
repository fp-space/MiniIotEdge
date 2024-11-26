package com.iothub.message.broker.module.queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class MqttMessageQueue<T> {
    
    private static final int QUEUE_CAPACITY = 10000; // 队列容量
    private final LocalBufferQueue<T> queue = new LocalBufferQueue<>(QUEUE_CAPACITY);
    
    /**
     * 添加消息到队列
     *
     * @param element 内容
     */
    public void put(T element) {
        if (!queue.put(element)) {
            log.warn("Failed to add element to queue. Queue is full.");
        }
    }
    
    public T poll(long timeout, TimeUnit unit) throws InterruptedException {
        return queue.poll(timeout, unit);
    }
    
    /**
     * 获取并处理消息
     */
    public void processMessages() {
        new Thread(() -> {
            while (true) {
                try {
                    T element = queue.get();
                    log.info("Processing element: {}", element);
                    
                    handle(element);
                    
                    // 这里加入具体的消息处理逻辑
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("Element processing interrupted", e);
                }
            }
        }).start();
    }
    
    public void handle(T element){}
}
