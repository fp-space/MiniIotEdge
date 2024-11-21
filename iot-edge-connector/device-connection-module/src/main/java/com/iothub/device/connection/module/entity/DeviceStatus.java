package com.iothub.device.connection.module.entity;

/**
 * 设备状态实体类
 */
public record DeviceStatus(
        String deviceCode,            // 设备编码
        boolean isActive,             // 设备是否激活
        boolean isOffline,            // 设备是否离线
        String lastHeartbeat         // 最后心跳时间
) {}