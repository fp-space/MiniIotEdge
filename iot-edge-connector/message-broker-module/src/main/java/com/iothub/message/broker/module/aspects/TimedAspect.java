package com.iothub.message.broker.module.aspects;

import com.iothub.message.broker.module.utils.TimerUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Aspect
@Component
public class TimedAspect {
    
    // 定义切点，指定注解 @Timed 的方法
    @Pointcut("@annotation(com.iothub.message.broker.module.annotations.Timed)")
    public void timedMethods() {
    }
    
    // 环绕通知：记录方法执行时间
    @Around("timedMethods()")
    public Object timeMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String tag = UUID.randomUUID().toString();  // 为每个方法调用生成唯一标识符
        try {
            TimerUtil.startTimer(tag);  // 开始计时
            
            // 执行目标方法
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            // 处理异常
            log.error("Exception occurred in method: {}. Error: {}", joinPoint.getSignature().getName(), throwable.getMessage(), throwable);
            throw throwable;  // 抛出异常，保证方法抛出原有异常
        } finally {
            TimerUtil.stopTimer(tag);  // 停止计时并记录
        }
    }
    
}

