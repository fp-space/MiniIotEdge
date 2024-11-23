package com.iothub.message.broker.module.service.impl;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.iothub.message.broker.module.connector.CustomConnector;
import com.iothub.message.broker.module.connector.DefaultDeviceConnector;
import com.iothub.message.broker.module.entity.MessageRequest;
import com.iothub.message.broker.module.manager.DeviceConnectorManager;
import com.iothub.message.broker.module.entity.Device;
import com.iothub.message.broker.module.enums.MessageTypeEnum;
import com.iothub.message.broker.module.manager.DeviceRegistry;
import com.iothub.message.broker.module.service.IotMessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class ControlCommandProcessor implements IotMessageProcessor {
    
    @Resource
    private DeviceConnectorManager deviceConnectorManager;
    
    @Resource
    private DeviceRegistry deviceRegistry;
    
    private static final String TOPIC_REGEX = "^/[^/]+/([^/]+)$";
    
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
        connector.setDevice(device);
        return connector;
    }
    
    private void executeCommand(String content, DefaultDeviceConnector connector) {
        MessageRequest request = JSONUtil.parse(content).toBean(new TypeReference<>(){});
        
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
        return MessageTypeEnum.CONTROL_COMMAND;
    }
}