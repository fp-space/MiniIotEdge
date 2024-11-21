package com.iothub.device.connection.module.service.impl;

import com.iothub.device.connection.module.entity.Device;
import com.iothub.device.connection.module.entity.DeviceStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CustomConnector extends DefaultDeviceConnector {
    
    private final Device device;
    
    @Autowired
    public CustomConnector(Device device) {
        this.device = device;
    }
    
    @Override
    protected String executeControlCommand(String tag, String command, Map<String, Object> params) {
        return null;
    }
    
    @Override
    protected void updateDeviceProperty(String propertyName, Object value) {
    
    }
    
    @Override
    protected void handleDeviceEvent(String event, Map<String, Object> payload) {
    
    }
    
    @Override
    protected DeviceStatus fetchDeviceStatus() {
        // 返回设备的状态
        return new DeviceStatus(device.code(), device.isActive(), device.isOffline(), device.lastHeartbeat());
    }
}