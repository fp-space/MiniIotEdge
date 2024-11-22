package com.iothub.message.broker.module.connector;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DeviceConnectorManager {
    
    private final ApplicationContext applicationContext;
    
    @Autowired
    public DeviceConnectorManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    
    /**
     * 根据 tag 快速获取连接器实例
     * @param tag 连接器的唯一标识
     * @return 对应的 DeviceConnector 实例
     */
    public DefaultDeviceConnector getConnectorByTag(String tag) {
        // 直接通过 beanName 获取连接器实例
        try {
            return (DefaultDeviceConnector) applicationContext.getBean(tag);
        } catch (NoSuchBeanDefinitionException e) {
            log.error("No connector found for tag: {}", tag);
            return null;  // 或者抛出自定义异常
        }
    }
}

