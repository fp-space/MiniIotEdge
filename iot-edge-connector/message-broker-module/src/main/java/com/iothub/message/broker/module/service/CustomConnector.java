package com.iothub.message.broker.module.service;

import com.iothub.message.broker.module.domain.DeviceStatus;
import com.iothub.message.broker.module.logic.connector.DefaultDeviceConnector;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CustomConnector extends DefaultDeviceConnector {
    
    @Override
    public DeviceStatus fetchDeviceStatus() {
        return null;
    }
    
    @Override
    public Object doReportEvent() {
        return Map.of("key", "value");
    }
    
    @Override
    public Object doReportProperty() {
        return Map.of("key", "value");
    }
    
    @Override
    public Object executeControlCommand(String identify, Map<String, Object> params) {
        return null;
    }
    
    @Override
    public void handleExecError(String command, Exception e) {
    
    }
    
    @Override
    public String getIdentify() {
        return "custom";
    }
}