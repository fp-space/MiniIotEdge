package com.iothub.message.broker.module.logic.manager;

import com.iothub.message.broker.module.domain.Device;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

@Component
@Slf4j
public class DeviceManager {
    
    private static final int RELOAD_INTERVAL_SECONDS = 60; // 定时同步设备信息的间隔时间（秒）
    private final DeviceRegistry deviceRegistry;
    private ScheduledExecutorService reloadService;
    
    // 通过构造函数注入一个共享的 ScheduledExecutorService 实例
    public DeviceManager(DeviceRegistry deviceRegistry) {
        this.deviceRegistry = deviceRegistry;
    }
    
    // 静态方法：通过构造函数，直接操作设备管理器中的设备
    public static void addOrUpdateDeviceStatic(DeviceRegistry deviceRegistry, Device device) {
        deviceRegistry.addOrUpdateDevice(device);
    }
    
    // 静态方法：批量更新设备
    public static void updateDevicesStatic(DeviceRegistry deviceRegistry, List<Device> devices) {
        devices.forEach(deviceRegistry::addOrUpdateDevice);
        log.info("Static batch update complete: " + devices.size() + " devices updated.");
    }
    
    // 静态方法：检查设备健康状态
    public static void checkDeviceHealthStatic(DeviceRegistry deviceRegistry) {
        List<Device> unhealthyDevices = deviceRegistry.getDevicesByCondition(device -> !device.isActive());
        if (!unhealthyDevices.isEmpty()) {
            log.info("Static unhealthy devices found: " + unhealthyDevices);
            // 执行必要的操作，例如发送警报、修复或更新设备状态
        }
    }
    
    // 在初始化时启动定时任务
    @PostConstruct
    public void init() {
        reloadService = Executors.newScheduledThreadPool(1);  // 单线程池来执行定时任务
        
        // 首先立即执行一次任务
        reloadDevices();
        
        // 定期重新加载设备信息
        reloadService.scheduleAtFixedRate(this::reloadDevices, RELOAD_INTERVAL_SECONDS, RELOAD_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }
    
    // 定期重新加载设备信息
    private void reloadDevices() {
        log.info("Reloading devices...");
        List<Device> newDevices = loadDevicesFromExternalSystem();
        updateDevices(newDevices); // 批量更新设备
    }
    
    // 模拟从外部系统加载设备信息
    private List<Device> loadDevicesFromExternalSystem() {
        List<Device> devices = new ArrayList<>();
        
        // 使用全参构造器，设置每个字段
        devices.add(new Device(1L, "device-001", "did-001", "Device 1", true, true,
                "mfg-001", "product-001", "custom", "2023-10-01"));
        
        devices.add(new Device(2L, "device-002", "did-002", "Device 2", true, true,
                "mfg-002", "product-002", "custom", "2023-10-02"));
        
        devices.add(new Device(3L, "device-003", "did-003", "Device 3", true, false,
                "mfg-003", "product-003", "custom", "2023-10-03"));
        
        return devices;
    }
    
    // 批量更新设备
    public void updateDevices(List<Device> devices) {
        devices.forEach(deviceRegistry::addOrUpdateDevice);
        log.info("Batch update complete: " + devices.size() + " devices updated.");
    }
    
    // 添加或更新设备
    public void addOrUpdateDevice(Device device) {
        deviceRegistry.addOrUpdateDevice(device);
    }
    
    // 获取单个设备
    public Device getDevice(String deviceCode) {
        return deviceRegistry.getDevice(deviceCode);
    }
    
    // 根据条件获取设备
    public List<Device> getDevicesByCondition(Predicate<Device> condition) {
        return deviceRegistry.getDevicesByCondition(condition);
    }
    
    // 检查设备健康状态
    public void checkDeviceHealth() {
        List<Device> unhealthyDevices = deviceRegistry.getDevicesByCondition(device -> !device.isActive());
        if (!unhealthyDevices.isEmpty()) {
            log.info("Unhealthy devices found: " + unhealthyDevices);
            // 执行必要的操作，例如发送警报、修复或更新设备状态
        }
    }
    
    // 批量操作示例：批量激活设备
    public void activateDevices(List<String> deviceCodes) {
        deviceCodes.forEach(code -> {
            Device device = deviceRegistry.getDevice(code);
            
            if (device != null) {
                device.isActive(true);
                deviceRegistry.addOrUpdateDevice(device);
            }
        });
        log.info("Batch activate complete: " + deviceCodes.size() + " devices activated.");
    }
    
    // 在类销毁时关闭 ScheduledExecutorService
    @PreDestroy
    public void shutdown() {
        if (reloadService != null && !reloadService.isShutdown()) {
            reloadService.shutdown(); // 关闭线程池
            System.out.println("ScheduledExecutorService shutdown completed.");
        }
    }
}
