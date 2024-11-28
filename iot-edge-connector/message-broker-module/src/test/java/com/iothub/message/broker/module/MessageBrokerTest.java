package com.iothub.message.broker.module;

import com.iothub.message.broker.module.enums.MessageTypeEnum;
import com.iothub.message.broker.module.core.handler.MqttMessageSenderHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageBrokerTest {
    
    @Autowired
    private MqttMessageSenderHandler mqttMessageSenderHandler;
    
    @Test
    public void testMqttSendMessage() {
        String content = "Hello, World!";
        String topic = "/topic";
        mqttMessageSenderHandler.publish(topic, content, MessageTypeEnum.EVENT);
    }
    
    @Test
    public void testConnector() {
        String content = """
                {
                  "identify": "hello",
                  "inputParams": {
                    "a": "b"
                  }
                }
                """;
        String topic = "/topic";
        mqttMessageSenderHandler.publish(topic, content, MessageTypeEnum.COMMAND_INVOCATION);
    }
}


