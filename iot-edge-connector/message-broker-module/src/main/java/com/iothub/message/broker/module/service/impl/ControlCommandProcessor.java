package com.iothub.message.broker.module.service.impl;

import cn.hutool.json.JSONUtil;
import com.iothub.message.broker.module.connector.CustomConnector;
import com.iothub.message.broker.module.connector.DefaultDeviceConnector;
import com.iothub.message.broker.module.connector.DeviceConnectorManager;
import com.iothub.message.broker.module.entity.Device;
import com.iothub.message.broker.module.enums.MessageTypeEnum;
import com.iothub.message.broker.module.service.IotMessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component
@Slf4j
public class ControlCommandProcessor implements IotMessageProcessor {
    
    @Resource
    private DeviceConnectorManager deviceConnectorManager;
    
    @Override
    public void process(String topic, String content) {
        // 事件回复的处理逻辑
        log.info("Processing EVENT_REPLY from Topic: {}, Content: {}", topic, content);
        
        Device device = getDevice();
        
        DefaultDeviceConnector connector = initConnector(device);
        doExecute(content, connector);
        
        connector.clearDevice();
    }
    
    private static Device getDevice() {
        return Device.builder()
                .id(1L)
                .code("test").build();
    }
    
    private DefaultDeviceConnector initConnector(Device device) {
        String tag = CustomConnector.class.getSimpleName();  // 获取 "CustomConnector"
        tag = tag.substring(0, 1).toLowerCase() + tag.substring(1);  // 将首字母转换为小写，结果是 "customConnector"
        DefaultDeviceConnector connector = deviceConnectorManager.getConnectorByTag(tag);
        connector.setDevice(device);
        return connector;
    }
    
    private static void doExecute(String content, DefaultDeviceConnector connector) {
        Map<String, Object> params = JSONUtil.toBean(content, Map.class);
        
        // 检查并获取 "identify" 和 "inputParams" 值
        String identify = (String) params.get("identify");
        Map<String, Object> inputParams = (Map<String, Object>) params.get("inputParams");
        
        // 确保 identify 和 inputParams 不为 null
        if (identify != null && inputParams != null) {
            // 执行命令
            connector.exec(identify, inputParams);
        } else {
            // 如果参数缺失，做适当的错误处理
            log.error("Missing required parameters: identify or inputParams");
        }
    }
    
    @Override
    public MessageTypeEnum getMessageType() {
        return MessageTypeEnum.CONTROL_COMMAND;
    }
}

