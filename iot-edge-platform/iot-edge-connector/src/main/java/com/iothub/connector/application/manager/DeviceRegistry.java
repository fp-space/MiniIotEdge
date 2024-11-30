package com.iothub.connector.application.manager;

import com.iothub.message.application.domain.Device;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class DeviceRegistry {
    
    private final Map<String, Device> deviceRegistry = new ConcurrentHashMap<>();
    
    // 添加或更新设备
    public void addOrUpdateDevice(Device device) {
        deviceRegistry.put(device.code(), device);
    }
    
    // 获取单个设备
    public Device getDevice(String deviceCode) {
        return deviceRegistry.get(deviceCode);
    }
    
    // 批量获取设备列表
    public List<Device> getAllDeviceList() {
        return new ArrayList<>(deviceRegistry.values());
    }
    
    // 获取所有设备的 Map
    public Map<String, Device> getAllDeviceMap() {
        return Collections.unmodifiableMap(deviceRegistry);
    }
    
    // 根据条件获取设备列表
    public List<Device> getDevicesByCondition(Predicate<Device> condition) {
        return deviceRegistry.values().stream()
                .filter(condition)
                .collect(Collectors.toList());
    }
    
    // 根据条件获取设备的 Map（返回符合条件的设备）
    public Map<String, Device> getDevicesByConditionAsMap(Predicate<Device> condition) {
        return deviceRegistry.entrySet().stream()
                .filter(entry -> condition.test(entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    // 获取设备数量
    public long getDeviceCount() {
        return deviceRegistry.size();
    }
    
    // 检查设备是否存在
    public boolean containsDevice(String deviceCode) {
        return deviceRegistry.containsKey(deviceCode);
    }
    
    // 清除所有设备
    public void clearAllDevices() {
        deviceRegistry.clear();
    }
    
    // 清除符合条件的设备
    public void removeDevicesByCondition(Predicate<Device> condition) {
        deviceRegistry.entrySet().removeIf(entry -> condition.test(entry.getValue()));
    }
}
