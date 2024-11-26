package com.iothub.message.broker.module.manager;

import com.iothub.message.broker.module.connector.DefaultDeviceConnector;
import com.iothub.message.broker.module.entity.Device;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

@Slf4j
@Component
public class ScheduledTaskManager {
    
    private static final int TASK_TIMEOUT = 30; // 每个任务的超时时间（秒）
    private static final int MAX_CONCURRENT_PROPERTY_TASKS = 5000; // 最大属性报告并发任务数
    private static final int MAX_CONCURRENT_EVENT_TASKS = 5000; // 最大事件报告并发任务数
    private static final int PROPERTY_REPORT_INTERVAL = 5; // 属性报告间隔（分钟）
    private static final int EVENT_REPORT_INTERVAL = 10; // 事件报告间隔（分钟）
    private static final int MAX_WAIT_TIME_MS = 10_000; // 最大等待时间（毫秒），10秒
    
    @Resource
    private DeviceConnectorManager deviceConnectorManager;
    
    @Resource
    private DeviceRegistry deviceRegistry;
    
    // 为属性报告和事件报告分别定义虚拟线程池
    private final ExecutorService propertyExecutorService;
    private final ExecutorService eventExecutorService;
    
    // 分别为属性报告和事件报告定义信号量，用于控制并发任务数量
    private final Semaphore propertySemaphore;
    private final Semaphore eventSemaphore;
    
    // 定义调度器
    private final ScheduledExecutorService scheduler;
    
    public ScheduledTaskManager() {
        // 初始化虚拟线程池和信号量
        this.propertyExecutorService = Executors.newVirtualThreadPerTaskExecutor();
        this.eventExecutorService = Executors.newVirtualThreadPerTaskExecutor();
        this.propertySemaphore = new Semaphore(MAX_CONCURRENT_PROPERTY_TASKS);
        this.eventSemaphore = new Semaphore(MAX_CONCURRENT_EVENT_TASKS);
        scheduler = Executors.newScheduledThreadPool(2);
    }
    
    @PostConstruct
    public void init() {
        // 延迟启动10秒钟后执行首次任务
        scheduler.scheduleWithFixedDelay(this::processPropertyReports, 0, PROPERTY_REPORT_INTERVAL, TimeUnit.MINUTES);
        scheduler.scheduleWithFixedDelay(this::processEventReports, 0, EVENT_REPORT_INTERVAL, TimeUnit.SECONDS);
        log.info("Scheduled tasks have been successfully initialized and will start after a delay of 10 seconds.");
    }
    
    // 处理设备的属性报告（每5分钟执行一次）
    private void processPropertyReports() {
        try {
            Map<String, Device> devicesMap = deviceRegistry.getAllDeviceMap();
            List<Device> deviceList = devicesMap.values().stream()
                    .filter(entry -> entry.isActive() && entry.isOffline())
                    .toList();
            
            log.info("执行属性上报任务，当前在线设备数量为:{}", deviceList.size());
            // 将设备分批处理，每批处理 MAX_CONCURRENT_PROPERTY_TASKS 个设备
            for (Device device : deviceList) {
                // 尝试获取属性报告信号量许可
                if (propertySemaphore.tryAcquire(MAX_WAIT_TIME_MS, TimeUnit.MILLISECONDS)) {
                    propertyExecutorService.submit(() -> {
                        try {
                            processDeviceProperty(device);
                        } finally {
                            propertySemaphore.release();
                        }
                    });
                } else {
                    // 如果任务超时未获取到许可，则丢弃任务
                    log.warn("Property report task for device {} was discarded due to timeout", device.code());
                }
            }
            
        } catch (Exception e) {
            log.error("Error processing property reports: {}", e.getMessage());
        }
    }
    
    // 处理设备的事件报告（每10分钟执行一次）
    private void processEventReports() {
        try {
            Map<String, Device> devicesMap = deviceRegistry.getAllDeviceMap();
            List<Device> deviceList = devicesMap.values().stream()
                    .filter(entry -> entry.isActive() && entry.isOffline())
                    .toList();
            
            log.info("执行事件上报任务，当前在线设备数量为:{}", deviceList.size());
            
            // 将设备分批处理，每批处理 MAX_CONCURRENT_EVENT_TASKS 个设备
            for (Device device : deviceList) {
                // 尝试获取事件报告信号量许可
                if (eventSemaphore.tryAcquire(MAX_WAIT_TIME_MS, TimeUnit.MILLISECONDS)) {
                    eventExecutorService.submit(() -> {
                        try {
                            processDeviceEvent(device);
                        } finally {
                            eventSemaphore.release();
                        }
                    });
                } else {
                    // 如果任务超时未获取到许可，则丢弃任务
                    log.warn("Event report task for device {} was discarded due to timeout", device.code());
                }
            }
            
        } catch (Exception e) {
            log.error("Error processing event reports: {}", e.getMessage());
        }
    }
    
    // 处理设备的属性报告
    private void processDeviceProperty(Device device) {
        try {
            DefaultDeviceConnector connector = deviceConnectorManager.getConnectorByIdentify(device.model());
            if (Objects.isNull(connector)) {
                return;
            }
            
            connector.setDevice(device);
            connector.reportProperty(); // 属性报告的处理
            
        } catch (Exception e) {
            log.error("Error processing property report for device {}: {}", device.code(), e.getMessage());
        }
    }
    
    // 处理设备的事件报告
    private void processDeviceEvent(Device device) {
        try {
            DefaultDeviceConnector connector = deviceConnectorManager.getConnectorByIdentify(device.model());
            if (Objects.isNull(connector)) {
                return;
            }
            
            connector.setDevice(device);
            connector.reportEvent(); // 事件报告的处理
            
        } catch (Exception e) {
            log.error("Error processing event report for device {}: {}", device.code(), e.getMessage());
        }
    }
    
    @PreDestroy
    public void shutdown() throws InterruptedException {
        // 关闭虚拟线程池，确保所有任务完成
        propertyExecutorService.shutdown();
        eventExecutorService.shutdown();
        scheduler.shutdown(); // 关闭调度器
        
        // 等待线程池中的所有任务完成
        if (!propertyExecutorService.awaitTermination(TASK_TIMEOUT, TimeUnit.SECONDS)) {
            propertyExecutorService.shutdownNow();
        }
        if (!eventExecutorService.awaitTermination(TASK_TIMEOUT, TimeUnit.SECONDS)) {
            eventExecutorService.shutdownNow();
        }
        if (!scheduler.awaitTermination(TASK_TIMEOUT, TimeUnit.SECONDS)) {
            scheduler.shutdownNow();
        }
    }
}
