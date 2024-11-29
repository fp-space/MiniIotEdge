package com.iothub.message.application.core.config;

import com.iothub.message.application.core.handler.connector.DefaultDeviceConnector ;
import com.iothub.message.application.core.handler.interfaces.IotMessageProcessor;
import com.iothub.message.application.enums.MessageTypeEnum;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class MessageProcessorConfig {
    
    @Bean
    public Map<MessageTypeEnum, IotMessageProcessor> messageProcessors(List<IotMessageProcessor> iotMessageProcessorList) {
        Map<MessageTypeEnum, IotMessageProcessor> processors = new HashMap<>();
        iotMessageProcessorList.forEach(iotMessageProcessor -> processors.put(iotMessageProcessor.getMessageType(), iotMessageProcessor));
        return processors;
    }
    
    @Bean
    public Map<String, String> identifyConnectorMap(List<DefaultDeviceConnector> connectorList) {
        Map<String, String> identifyMap = new HashMap<>();
        connectorList.forEach(connector -> {
            String tag = connector.getTag();
            tag = tag.substring(0, 1).toLowerCase() + tag.substring(1);
            identifyMap.put(connector.getIdentify(), tag);
        });
        return identifyMap;
    }
}
