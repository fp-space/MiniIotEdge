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
    public void doReportProperty(String propertyName, Object value) {
    
    }
    
    @Override
    public void doReportEvent(String event, Map<String, Object> payload) {
    
    }
    
    @Override
    public void handleExecError(String command, Exception e) {
    
    }
}