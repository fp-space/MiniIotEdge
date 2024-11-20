package com.iothub.message.broker.module.service.impl;

import com.iothub.message.broker.module.enums.MessageTypeEnum;
import com.iothub.message.broker.module.service.IotMessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EventReplyProcessor implements IotMessageProcessor {
    @Override
    public void process(String topic, String content) {
        // 事件回复的处理逻辑
        log.info("Processing EVENT_REPLY from Topic: {}, Content: {}", topic, content);
    }
    
    @Override
    public MessageTypeEnum getMessageType() {
        return MessageTypeEnum.EVENT_REPLY;
    }
}

