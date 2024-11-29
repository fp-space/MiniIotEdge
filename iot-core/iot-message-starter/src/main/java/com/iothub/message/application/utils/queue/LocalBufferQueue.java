package com.iothub.message.application.utils.queue;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 本地缓冲队列管理类
 * 提供线程安全的 put 和 get 方法，并支持队列容量限制和丢弃策略。
 *
 * @param <T> 队列中存储的元素类型
 */
@Slf4j
public class LocalBufferQueue<T> {
    
    private final BlockingQueue<T> queue;
    
    /**
     * 构造器，指定队列容量
     *
     * @param capacity 队列容量
     */
    public LocalBufferQueue(int capacity) {
        this.queue = new LinkedBlockingQueue<>(capacity);
    }
    
    /**
     * 向队列中添加元素
     *
     * @param element 要添加的元素
     * @return 添加是否成功
     */
    public boolean put(T element) {
        boolean added = queue.offer(element);
        if (!added) {
            log.warn("Queue is full. Dropping element: {}", element);
        }
        return added;
    }
    
    
    /**
     * 尝试从队列中获取消息
     *
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return 获取的元素，如果超时返回 null
     * @throws InterruptedException 如果线程被中断
     */
    public T poll(long timeout, TimeUnit unit) throws InterruptedException {
        return queue.poll(timeout, unit);
    }
    
    /**
     * 从队列中取出元素
     *
     * @return 队列中的元素，如果队列为空则阻塞直到有可用元素
     * @throws InterruptedException 如果被中断
     */
    public T get() throws InterruptedException {
        return queue.take();
    }
    
    /**
     * 获取当前队列大小
     *
     * @return 队列中元素的数量
     */
    public int size() {
        return queue.size();
    }
    
    /**
     * 获取队列的容量
     *
     * @return 队列容量
     */
    public int capacity() {
        return queue.remainingCapacity() + queue.size();
    }
    
    /**
     * 清空队列
     */
    public void clear() {
        queue.clear();
        log.info("Queue has been cleared.");
    }
}
