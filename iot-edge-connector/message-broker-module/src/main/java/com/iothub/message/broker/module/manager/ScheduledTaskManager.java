package com.iothub.message.broker.module.manager;


import com.iothub.message.broker.module.connector.DefaultDeviceConnector;
import com.iothub.message.broker.module.entity.Device;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

@Slf4j
public abstract class ScheduledTaskManager {
    
    private static final int TASK_TIMEOUT = 20; // 每个任务的超时时间（秒）
    
    @Resource
    private DeviceConnectorManager deviceConnectorManager;
    
    @Resource
    private DeviceRegistry deviceRegistry;
    
    // 开始扫描所有设备进行处理
    public void scan(){
        Map<String, Device> devicesMap = deviceRegistry.getAllDeviceMap();
        
        for(Map.Entry<String, Device> item: devicesMap.entrySet()){
            Device device = item.getValue();
            
            if(!device.isActive() || !device.isOffline()){
                continue;
            }
            
            DefaultDeviceConnector connector = deviceConnectorManager.getConnectorByIdentify(device.model());
            connector.setDevice(device);
            if(Objects.nonNull(connector)){
//                connector.reportProperty();
//                connector.reportEvent();
            }
            
        }
    }
}

