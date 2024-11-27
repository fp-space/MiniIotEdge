package com.iothub.message.broker.module.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@AllArgsConstructor
@NoArgsConstructor
public class Device {
    private Long id;                       // 设备ID
    private String code;                   // 设备编码
    private String did;                    // 第三方编码
    private String name;                   // 设备名称
    private boolean isActive;              // 是否激活，表示设备是否启用
    private boolean isOffline;             // 是否离线，表示设备是否离线
    private String manufacturerCode;       // 关联的厂家ID
    private String productKey;             // 关联的产品ID
    private String model;                  // 关联的型号ID
    private String lastHeartbeat;          // 最后心跳时间（离线检测用）
}

