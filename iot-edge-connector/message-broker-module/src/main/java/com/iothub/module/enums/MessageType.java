package com.iothub.module.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum MessageType {
    EVENT_REPLY("事件回复"),    // 事件回复
    ATTRIBUTE_REPORT("属性上报"), // 属性上报
    UNKNOWN("未知类型");         // 未知类型
    
    private final String desc;
}

