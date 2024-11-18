package com.iothub.module.service;

import com.iothub.module.enums.MessageType;

public interface IotMessageProcessor {
    void process(String topic, String content);
    
    public MessageType getMessageType();
}