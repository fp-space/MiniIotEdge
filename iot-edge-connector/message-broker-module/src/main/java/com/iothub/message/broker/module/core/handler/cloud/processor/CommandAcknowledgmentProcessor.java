package com.iothub.message.broker.module.core.handler.cloud.processor;

import com.iothub.message.broker.module.core.handler.interfaces.IotMessageProcessor;
import com.iothub.message.broker.module.enums.MessageTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CommandAcknowledgmentProcessor  implements IotMessageProcessor {
    
    @Override
    public void process(String topic, String content) {
        // 属性上报的处理逻辑
        log.info("Processing ATTRIBUTE_REPORT from Topic: {}, Content: {}", topic, content);
        // 可以在这里根据内容解析属性，存储数据等
    }
    
    @Override
    public MessageTypeEnum getMessageType() {
        return MessageTypeEnum.COMMAND_ACKNOWLEDGMENT;
    }
}

