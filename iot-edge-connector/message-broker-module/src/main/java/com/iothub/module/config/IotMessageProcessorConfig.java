package com.iothub.module.config;

import com.iothub.module.enums.MessageType;
import com.iothub.module.service.IotMessageProcessor;
import com.iothub.module.service.impl.AttributeReportProcessor;
import com.iothub.module.service.impl.EventReplyProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class IotMessageProcessorConfig {
    
    @Bean
    public Map<MessageType, IotMessageProcessor> messageProcessors(
            AttributeReportProcessor attributeReportProcessor,
            EventReplyProcessor eventReplyProcessor) {
        Map<MessageType, IotMessageProcessor> processors = new HashMap<>();
        // 注册所有的处理器
        processors.put(attributeReportProcessor.getMessageType(), attributeReportProcessor);
        processors.put(eventReplyProcessor.getMessageType(), eventReplyProcessor);
        return processors;
    }
}
