package com.iothub.message.broker.module.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum MessageSourceType {
    
    CLOUD("CLOUD", "云端"),       // 云端来源
    EDGE("EDGE", "边端"),         // 边端来源
    UNKNOWN("UNKNOWN", "其他");       // 其他来源
    
    @Getter
    private final String code;         // 来源编码
    @Getter
    private final String description;  // 来源描述
    
    public static MessageSourceType fromDescription(String description) {
        for (MessageSourceType type : MessageSourceType.values()) {
            if (type.getDescription().equalsIgnoreCase(description)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown description: " + description);
    }
    
    public static MessageSourceType fromCode(String code) {
        for (MessageSourceType type : MessageSourceType.values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
    
    @Override
    public String toString() {
        return String.format("MessageSourceType{code='%s', description='%s'}", code, description);
    }
}
