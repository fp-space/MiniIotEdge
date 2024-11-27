package com.iothub.message.broker.module.utils;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.MqttAsyncClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.mqtt.outbound.Mqttv5PahoMessageHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MqttUtil {
    
    
    private static Mqttv5PahoMessageHandler mqttv5PahoMessageHandler;
    
    // 静态方法：通过 Spring 上下文来初始化 Mqttv5PahoMessageHandler
    @Autowired
    public MqttUtil(Mqttv5PahoMessageHandler mqttv5PahoMessageHandler) {
        // 使用构造函数注入，保存静态引用
        MqttUtil.mqttv5PahoMessageHandler = mqttv5PahoMessageHandler;
    }
    
    /**
     * 检查 MQTT 客户端是否存活
     *
     * @return true 如果 MQTT 客户端存活，false 如果客户端不存活
     */
    public static boolean isMqttClientAlive() {
        boolean result = mqttv5PahoMessageHandler.isRunning();
        if(!result) {
            log.info("MQTT client is not alive.");
        }
        return result;
    }
}

