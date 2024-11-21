package com.iothub.device.connection.module.entity;

/**
 * 设备实体类
 */
public record Device(
        Long id,                       // 设备ID
        String code,                   // 设备编码
        String did,                    // 第三方编码
        String name,                   // 设备名称
        boolean isActive,              // 是否激活，表示设备是否启用
        boolean isOffline,             // 是否离线，表示设备是否离线
        String manufacturerCode,       // 关联的厂家ID
        String productKey,             // 关联的产品ID
        String model,                  // 关联的型号ID
        String lastHeartbeat           // 最后心跳时间（离线检测用）
) {}
