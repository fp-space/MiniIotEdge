package com.iothub.message.application.core.handler.interfaces;

import com.iothub.message.application.enums.MessageTypeEnum;

public interface IotMessageProcessor {
    void process(String topic, String content);
    
    MessageTypeEnum getMessageType();
}