package com.iothub.message.broker.module.config;

import com.iothub.message.broker.module.service.IotMessageProcessor;
import com.iothub.message.broker.module.enums.MessageTypeEnum;
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
}
