package com.iothub.message.broker.module.core.processor.cloud;

import com.iothub.message.broker.module.core.processor.IotMessageProcessor;
import com.iothub.message.broker.module.enums.MessageTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PropertyProcessor implements IotMessageProcessor {
    
    @Override
    public void process(String topic, String content) {
        // 属性上报的处理逻辑
        log.info("Processing ATTRIBUTE_REPORT from Topic: {}, Content: {}", topic, content);
        // 可以在这里根据内容解析属性，存储数据等
    }
    
    @Override
    public MessageTypeEnum getMessageType() {
        return MessageTypeEnum.PROPERTY;
    }
}

