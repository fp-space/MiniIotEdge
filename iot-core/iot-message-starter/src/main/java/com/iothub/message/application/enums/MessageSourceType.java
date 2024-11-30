package com.iothub.message.application.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageSourceType {
    
    CLOUD("cloud", "云端"),       // 云端来源
    EDGE("edge", "边端"),         // 边端来源
    UNKNOWN("unknown", "其他");       // 其他来源
    
    private final String code;         // 来源编码
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
    
    /**
     * 根据传入的字符串类型匹配对应的 MessageSourceType
     *
     * @param code 字符串类型
     * @return 匹配的 MessageSourceType，若没有匹配则返回 UNKNOWN
     */
    public static MessageSourceType match(String code) {
        if (code == null || code.trim().isEmpty()) {
            return UNKNOWN;
        }
        
        for (MessageSourceType sourceType : MessageSourceType.values()) {
            if (sourceType.getCode().equalsIgnoreCase(code)) {
                return sourceType;
            }
        }
        return UNKNOWN;
    }
    
    @Override
    public String toString() {
        return String.format("MessageSourceType{code='%s', description='%s'}", code, description);
    }
}
