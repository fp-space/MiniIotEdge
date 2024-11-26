package com.iothub.message.broker.module.entity;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * 设备状态实体类
 */
@Data
@Accessors(fluent = true)
@AllArgsConstructor
@NoArgsConstructor
public class DeviceStatus {
    private String deviceCode;    // 设备编码
    private boolean isActive;     // 设备是否激活
    private boolean isOffline;    // 设备是否离线
    private String lastHeartbeat; // 最后心跳时间
}