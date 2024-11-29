package com.iothub.message.application.utils.queue;

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
    
    /**
     * 获取元素
     */
    public T poll(long timeout, TimeUnit unit) throws InterruptedException {
        return queue.poll(timeout, unit);
    }
}
