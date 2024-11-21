package com.iothub.device.connection.module.service;

import com.iothub.device.connection.module.entity.Device;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 设备管理器，负责管理设备服务实例
 */
@Component
public class DeviceManager {
    
    private final Map<String, Device> deviceExecutors = new HashMap<>();
    
    /**
     * 注册设备执行器
     *
     * @param device       设备
     * @param deviceCode  设备编码
     */
    public void registerDeviceExecutor(Device device, String deviceCode) {
        deviceExecutors.put(deviceCode, device);
    }
    
    /**
     * 获取设备执行器
     *
     * @param deviceCode 设备编码
     * @return           设备执行器实例
     */
    public Device getDeviceExecutor(String deviceCode) {
        return deviceExecutors.get(deviceCode);
    }
}