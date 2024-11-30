package com.iothub.message.application.utils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.integration.mqtt.outbound.Mqttv5PahoMessageHandler;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
@DependsOn("mqttv5PahoMessageHandler")
public class LaunchStatusMonitor {
    
    @Resource
    private Mqttv5PahoMessageHandler mqttv5PahoMessageHandler;
    
    /**
     * 检查 MQTT 客户端是否存活
     *
     * @return true 如果 MQTT 客户端存活，false 如果客户端不存活
     */
    public boolean isMqttClientAlive() {
        
        if(Objects.isNull(mqttv5PahoMessageHandler)){
            return false;
        }
        
        boolean result = mqttv5PahoMessageHandler.isRunning();
        if(!result) {
            log.info("MQTT client is not alive.");
        }
        return result;
    }
}

