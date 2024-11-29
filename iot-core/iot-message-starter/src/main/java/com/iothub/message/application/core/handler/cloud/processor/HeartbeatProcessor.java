package com.iothub.message.application.core.handler.cloud.processor;

import com.iothub.message.application.core.handler.interfaces.IotMessageProcessor;
import com.iothub.message.application.enums.MessageTypeEnum;

public class HeartbeatProcessor implements IotMessageProcessor {
    @Override
    public void process(String topic, String content) {
    
    }
    
    @Override
    public MessageTypeEnum getMessageType() {
        return MessageTypeEnum.HEARTBEAT;
    }
}
