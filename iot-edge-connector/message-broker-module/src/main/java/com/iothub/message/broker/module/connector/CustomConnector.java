package com.iothub.message.broker.module.connector;

import com.iothub.message.broker.module.entity.DeviceStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CustomConnector extends DefaultDeviceConnector {
    
    @Override
    public DeviceStatus fetchDeviceStatus() {
        return null;
    }
    
    @Override
    public Object executeControlCommand(String identify, Map<String, Object> params) {
        return null;
    }
    
    @Override
    public Object doReportProperty() {
        return null;
    }
    
    @Override
    public Object doReportEvent() {
        return null;
    }
    
    @Override
    public String getIdentify() {
        return "custom";
    }
    
    @Override
    public void handleExecError(String command, Exception e) {
    
    }
}