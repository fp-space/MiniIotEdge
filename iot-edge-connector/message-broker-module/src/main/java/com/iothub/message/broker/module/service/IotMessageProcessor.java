package com.iothub.message.broker.module.service;

import com.iothub.message.broker.module.enums.MessageTypeEnum;

public interface IotMessageProcessor {
    void process(String topic, String content);
    
    public MessageTypeEnum getMessageType();
}