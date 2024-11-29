package com.iothub.message.application.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

@Slf4j
public class TimerUtil {
    
    // 使用 ThreadLocal 保证每个线程有自己独立的计时器实例
    private static final ThreadLocal<StopWatch> stopWatchThreadLocal = ThreadLocal.withInitial(StopWatch::new);
    
    /**
     * 执行某个任务并计算时间
     *
     * @param taskName 任务名称
     * @param task     执行的任务
     * @return 执行任务的耗时（单位：秒）
     */
    public static double executeWithTiming(String taskName, Runnable task) {
        log.info("Executing task: {}", taskName);
        startTimer(taskName);
        task.run();
        return stopTimer(taskName);
    }
    
    /**
     * 开始计时
     */
    public static void startTimer(String taskName) {
        StopWatch stopWatch = new StopWatch();
        stopWatchThreadLocal.set(stopWatch);
        stopWatch.start();
        log.info("Start timing task: {}", taskName);
    }
    
    /**
     * 停止计时并返回时间（单位：秒）
     */
    public static double stopTimer(String taskName) {
        StopWatch stopWatch = stopWatchThreadLocal.get();
        stopWatch.stop();
        double time = stopWatch.getTotalTimeSeconds();
        log.info("Task '{}' completed in: {} seconds", taskName, time);
        stopWatchThreadLocal.remove();  // 清理 ThreadLocal 中的 StopWatch 实例
        return time;
    }
    
    /**
     * 打印方法开始和结束的详细日志，适用于多步骤的任务
     */
    public static void logMethodExecutionTime(String taskName, Runnable task) {
        log.info("Start execution of '{}'", taskName);
        startTimer(taskName);
        try {
            task.run();
        } catch (Exception e) {
            log.error("Exception occurred while executing task '{}': {}", taskName, e.getMessage());
        } finally {
            double elapsedTime = stopTimer(taskName);
            log.info("Finished execution of '{}' in {} seconds", taskName, elapsedTime);
        }
    }
}
