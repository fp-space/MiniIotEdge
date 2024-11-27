package com.iothub.message.broker.module.service.impl;

import com.iothub.message.broker.module.enums.MessageTypeEnum;
import com.iothub.message.broker.module.service.IotMessageProcessor;

public class HeartbeatDetectionProcessor implements IotMessageProcessor {
    @Override
    public void process(String topic, String content) {
    
    }
    
    @Override
    public MessageTypeEnum getMessageType() {
        return MessageTypeEnum.HEARTBEAT_RESPONSE;
    }
}
