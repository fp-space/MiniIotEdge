package com.iothub.message.application.core.listener;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class CustomMqttCallback implements MqttCallback {
    
    private final MqttAsyncClient client;  // 用于保存 MQTT 客户端实例
    private final MqttConnectionOptions mqttConnectionOptions;
    
    // 使用 AtomicBoolean 标记是否正在重连，防止多次重连
    private final AtomicBoolean reconnecting = new AtomicBoolean(false);
    
    // 通过构造器注入已经初始化好的 MQTT 客户端
    public CustomMqttCallback(MqttAsyncClient client, MqttConnectionOptions mqttConnectionOptions) {
        this.client = client;  // 使用传入的 MqttAsyncClient 实例
        this.mqttConnectionOptions = mqttConnectionOptions;
    }
    
    @SneakyThrows
    @Override
    public void disconnected(MqttDisconnectResponse mqttDisconnectResponse) {
        // 处理连接丢失，自动重连
        log.error("MQTT连接丢失，响应码: {}，正在重连...", mqttDisconnectResponse.getException().getReasonCode());
        
        // 如果没有在进行重连，才启动重连逻辑
        if (reconnecting.compareAndSet(false, true)) {
            reconnect();  // 调用重连逻辑
        }
    }
    
    @Override
    public void mqttErrorOccurred(MqttException e) {
        log.error("MQTT错误: {}", e.getMessage());
    }
    
    @Override
    public void messageArrived(String topic, MqttMessage message) {
        // 收到消息时的处理
        log.info("收到消息，Topic: {}, Message: {}", topic, new String(message.getPayload()));
    }
    
    @Override
    public void deliveryComplete(IMqttToken token) {
        log.info("消息发布完成，Token: {}", token.getResponse());
    }
    
    @Override
    public void connectComplete(boolean b, String s) {
        if (b) {
            log.info("MQTT连接成功，连接ID: {}", s);
        } else {
            log.error("MQTT连接失败，原因: {}", s);
        }
    }
    
    @Override
    public void authPacketArrived(int i, MqttProperties mqttProperties) {
        log.info("MQTT认证信息包到达，状态码: {}", i);
    }
    
    // 重连逻辑
    private synchronized void reconnect() {
        try {
            // 如果当前客户端已连接，直接返回
            if (client.isConnected()) {
                log.info("客户端已连接，无需重连");
                return;
            }
            
            log.info("尝试重新连接 MQTT...");
            client.connect(mqttConnectionOptions);
        } catch (MqttException e) {
            log.error("MQTT重新连接失败，错误信息: {}", e.getMessage());
            try {
                TimeUnit.SECONDS.sleep(5);  // 等待 5 秒后重试
                reconnect();  // 递归重试
            } catch (InterruptedException interruptedException) {
                log.error("重试时发生中断异常: {}", interruptedException.getMessage());
            }
        } finally {
            reconnecting.set(false);  // 重连完成后将标志位恢复
        }
    }
}