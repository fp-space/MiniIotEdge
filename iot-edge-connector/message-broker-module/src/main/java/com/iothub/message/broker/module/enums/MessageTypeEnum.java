package com.iothub.message.broker.module.enums;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum MessageTypeEnum {
    EVENT_REPLY("事件回复", "event"),    // 事件
    ATTRIBUTE_REPORT("属性上报", "property"), // 属性
    UNKNOWN("未知类型", null);         // 未知类型
    
    @Getter
    private final String desc;
    
    @Getter
    private final String type;
    
    /**
     * 根据传入的字符串匹配对应的 MessageType 枚举值。
     *
     * @param type 字符串类型
     * @return 匹配的 MessageType，若没有匹配则返回 null
     */
    public static MessageTypeEnum match(String type) {
        
        if(StrUtil.isBlank(type)){
            return null;
        }
        
        for (MessageTypeEnum messageType : MessageTypeEnum.values()) {
            if (messageType.getType().equalsIgnoreCase(type)) {
                return messageType;
            }
        }
        return null;  // 若没有匹配的类型，可以返回 null 或抛出异常
    }
}

