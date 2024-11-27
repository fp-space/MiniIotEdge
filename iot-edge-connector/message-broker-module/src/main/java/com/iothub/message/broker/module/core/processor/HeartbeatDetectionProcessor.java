package com.iothub.message.broker.module.core.processor;

import com.iothub.message.broker.module.enums.MessageTypeEnum;

public class HeartbeatDetectionProcessor implements IotMessageProcessor {
    @Override
    public void process(String topic, String content) {
    
    }
    
    @Override
    public MessageTypeEnum getMessageType() {
        return MessageTypeEnum.HEARTBEAT_RESPONSE;
    }
}
