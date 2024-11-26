package com.iothub.message.broker.module.connector;

import com.iothub.message.broker.module.entity.DeviceStatus;
import com.iothub.message.broker.module.entity.MessageRequest;

public interface DeviceConnector {
    
    /**
     * 处理控制命令，接收下发的命令并执行相应的业务逻辑
     * @param request 参数
     */
    void exec(MessageRequest request);
    
    /**
     * 处理设备属性更新，接收属性更新的数据并执行相应的操作
     */
    void reportProperty();
    
    /**
     * 处理设备事件，接收事件数据并执行事件相关的业务逻辑
     */
    void reportEvent();
    
    /**
     * 设备当前状态
     * @return 设备状态
     */
    DeviceStatus getDeviceStatus();
}
