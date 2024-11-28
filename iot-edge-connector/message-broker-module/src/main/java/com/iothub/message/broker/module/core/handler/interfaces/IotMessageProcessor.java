package com.iothub.message.broker.module.core.handler.interfaces;

import com.iothub.message.broker.module.enums.MessageTypeEnum;

public interface IotMessageProcessor {
    void process(String topic, String content);
    
    MessageTypeEnum getMessageType();
}