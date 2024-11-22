package com.iothub.message.broker.module.connector;

import com.iothub.message.broker.module.entity.DeviceStatus;

import java.util.Map;

public interface DeviceConnector {
    
    /**
     * 处理控制命令，接收下发的命令并执行相应的业务逻辑
     * @param identify 控制命令
     * @param params 参数
     */
    void exec(String identify, Map<String, Object> params);
    
    /**
     * 处理设备属性更新，接收属性更新的数据并执行相应的操作
     * @param propertyName 属性名称
     * @param value 属性值
     */
    void reportProperty(String propertyName, Object value);
    
    /**
     * 处理设备事件，接收事件数据并执行事件相关的业务逻辑
     * @param event 事件名称
     * @param payload 事件数据
     */
    void reportEvent(String event, Map<String, Object> payload);
    
    /**
     * 设备当前状态
     * @return 设备状态
     */
    DeviceStatus getDeviceStatus();
}
