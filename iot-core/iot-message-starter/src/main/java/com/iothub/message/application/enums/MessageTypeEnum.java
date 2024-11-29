package com.iothub.message.application.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageTypeEnum {
    
    // 请求与响应类型
    REQUEST("请求", "request"),
    RESPONSE("响应", "response"),
    
    // 命令请求
    COMMAND_INVOCATION("命令请求", "invocation"),
    
    // 命令确认响应
    COMMAND_ACKNOWLEDGMENT("命令确认", "acknowledgment"),
    
    // 事件类消息
    EVENT("事件", "event"),          // 通用事件
    
    NOTIFICATION("通知", "notification"),
    
    // 属性类消息
    PROPERTY("属性", "property"),
    
    // 心跳类消息
    HEARTBEAT("心跳", "heartbeat_request"),
    
    // 自定义类型
    CUSTOM("自定义", "custom"),
    
    // 不支持的类型
    UNSUPPORTED("不支持的类型", "unsupported");
    
    private final String description;
    private final String type;
    
    /**
     * 根据传入的字符串类型匹配对应的 MessageType
     *
     * @param type 字符串类型
     * @return 匹配的 MessageType，若没有匹配则返回 null
     */
    public static MessageTypeEnum match(String type) {
        if (type == null || type.trim().isEmpty()) {
            return null;
        }
        
        for (MessageTypeEnum messageType : MessageTypeEnum.values()) {
            if (messageType.getType().equalsIgnoreCase(type)) {
                return messageType;
            }
        }
        return UNSUPPORTED;  // 若没有匹配的类型，可以返回 UNSUPPORTED 或其他适当地默认值
    }
}
