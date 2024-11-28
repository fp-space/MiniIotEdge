package com.iothub.message.broker.module.core.handler.cloud.processor;

import com.iothub.message.broker.module.core.handler.interfaces.IotMessageProcessor;
import com.iothub.message.broker.module.enums.MessageTypeEnum;

public class HeartbeatProcessor implements IotMessageProcessor {
    @Override
    public void process(String topic, String content) {
    
    }
    
    @Override
    public MessageTypeEnum getMessageType() {
        return MessageTypeEnum.HEARTBEAT;
    }
}
