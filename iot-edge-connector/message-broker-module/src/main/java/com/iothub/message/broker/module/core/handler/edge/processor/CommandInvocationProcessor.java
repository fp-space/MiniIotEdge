package com.iothub.message.broker.module.core.handler.edge.processor;

import com.google.gson.Gson;
import com.iothub.message.broker.module.core.handler.edge.connector.DefaultDeviceConnector;
import com.iothub.message.broker.module.core.handler.interfaces.IotMessageProcessor;
import com.iothub.message.broker.module.domain.Device;
import com.iothub.message.broker.module.domain.MessageRequest;
import com.iothub.message.broker.module.enums.MessageTypeEnum;
import com.iothub.message.broker.module.core.handler.edge.manager.DeviceConnectorManager;
import com.iothub.message.broker.module.core.handler.edge.manager.DeviceRegistry;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class CommandInvocationProcessor  implements IotMessageProcessor {
    
    private static final String TOPIC_REGEX = "^/[^/]+/([^/]+)$";
    @Resource
    private DeviceConnectorManager deviceConnectorManager;
    @Resource
    private DeviceRegistry deviceRegistry;
    
    @Override
    public void process(String topic, String content) {
        // 事件回复的处理逻辑
        log.info("Processing EVENT_REPLY from Topic: {}, Content: {}", topic, content);
        
        String deviceCode = extractDeviceCodeFromTopic(topic);
        if (deviceCode == null) {
            log.warn("Failed to extract device code from topic: {}", topic);
            return;
        }
        
        Device device = getDevice(deviceCode);
        if (device == null) {
            log.warn("Device not found for device code: {}", deviceCode);
            return;
        }
        
        DefaultDeviceConnector connector = initConnector(device);
        
        if (connector == null) {
            log.error("Failed to initialize device connector for device: {}", deviceCode);
            return;
        }
        
        try {
            executeCommand(content, connector);
        } catch (Exception e) {
            log.error("Error executing control command for device: {}", deviceCode, e);
        } finally {
            connector.clearDevice();
        }
    }
    
    private String extractDeviceCodeFromTopic(String topic) {
        // 使用正则提取设备编码
        Pattern pattern = Pattern.compile(TOPIC_REGEX);
        Matcher matcher = pattern.matcher(topic);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }
    
    private Device getDevice(String deviceCode) {
        return deviceRegistry.getDevice(deviceCode);
    }
    
    private DefaultDeviceConnector initConnector(Device device) {
        String identify = device.model();
        DefaultDeviceConnector connector = deviceConnectorManager.getConnectorByIdentify(identify);
        if (connector == null) {
            return null;
        }
        connector.setDevice(device);
        return connector;
    }
    
    private void executeCommand(String content, DefaultDeviceConnector connector) {
        
        // 创建 Gson 对象
        Gson gson = new Gson();
        // 使用 Gson 反序列化 JSON 字符串为 MessageRequest 对象
        MessageRequest request = gson.fromJson(content, MessageRequest.class);
        
        if (Objects.nonNull(request)) {
            // 执行命令
            log.info("request parameters: {}", request);
            connector.exec(request);
        } else {
            // 参数缺失的错误处理
            log.error("Missing required parameters: request");
        }
    }
    
    @Override
    public MessageTypeEnum getMessageType() {
        return MessageTypeEnum.COMMAND_INVOCATION;
    }
}