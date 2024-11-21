package com.iothub.device.connection.module.service.impl;

import com.iothub.device.connection.module.entity.DeviceStatus;
import com.iothub.device.connection.module.service.DeviceConnector;

import java.util.Map;

public abstract class DefaultDeviceConnector implements DeviceConnector {
    
    @Override
    public String processControlCommand(String tag, String command, Map<String, Object> params) {
        // 根据业务逻辑处理控制命令
        return executeControlCommand(tag, command, params);
    }
    
    @Override
    public void processPropertyUpdate(String propertyName, Object value) {
        // 根据业务逻辑更新设备属性
        updateDeviceProperty(propertyName, value);
    }
    
    @Override
    public void processEvent(String event, Map<String, Object> payload) {
        // 根据业务逻辑处理事件
        handleDeviceEvent(event, payload);
    }
    
    @Override
    public DeviceStatus getDeviceStatus() {
        // 获取设备的当前状态
        return fetchDeviceStatus();
    }
    
    // 执行控制命令的具体逻辑（子类可以覆盖此方法来提供具体实现）
    protected abstract String executeControlCommand(String tag, String command, Map<String, Object> params);
    
    // 更新设备属性的具体实现
    protected abstract void updateDeviceProperty(String propertyName, Object value);
    
    // 处理设备事件的具体实现
    protected abstract void handleDeviceEvent(String event, Map<String, Object> payload);
    
    // 获取设备状态的具体实现
    protected abstract DeviceStatus fetchDeviceStatus();
}
