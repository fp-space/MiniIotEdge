package com.iothub.message.broker.module;

import com.iothub.message.broker.module.handler.MqttMessageSenderHandler;
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
        String topic = "/topic1";
        mqttMessageSenderHandler.send(topic, content);
    }
}


