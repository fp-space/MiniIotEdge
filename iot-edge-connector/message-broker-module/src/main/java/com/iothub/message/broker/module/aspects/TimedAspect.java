package com.iothub.message.broker.module.aspects;

import cn.hutool.core.date.StopWatch;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TimedAspect {
    
    // 初始化 StopWatch 实例
    // 使用 ThreadLocal 保证每个线程有自己独立的计时器实例
    private static final ThreadLocal<StopWatch> stopWatchThreadLocal = ThreadLocal.withInitial(StopWatch::new);
    
    @Pointcut("@annotation(com.iothub.message.broker.module.annotations.Timed)")  // 指定哪些方法要切入
    public void timedMethods() {}
    
    @Before("timedMethods()")  // 在方法执行前开始计时
    public void startTimer() {
        StopWatch stopWatch = new StopWatch();  // 每次开始时创建新的 StopWatch 实例
        stopWatchThreadLocal.set(stopWatch);    // 将其放入 ThreadLocal 中，确保每个线程都有独立实例
        stopWatch.start();  // 开始计时
    }
    
    @After("timedMethods()")  // 在方法执行后结束计时
    public void stopTimer() {
        StopWatch stopWatch = stopWatchThreadLocal.get();  // 获取当前线程的 StopWatch 实例
        stopWatch.stop();  // 停止计时
        double time = stopWatch.getTotalTimeSeconds();  // 获取总耗时（秒）
        System.out.println("Method executed in: " + time + " seconds");
        stopWatchThreadLocal.remove();
    }
}
