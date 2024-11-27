package com.iothub.message.broker.module.enums;

import lombok.Getter;

@Getter
public enum MessageTypeEnum {
    
    // 请求与响应类型
    REQUEST("请求", "request", MessageSourceType.UNKNOWN),       // 通用请求
    RESPONSE("响应", "response", MessageSourceType.UNKNOWN),    // 通用响应
    
    // 控制类消息（仅云端下发）
    CONTROL_COMMAND("控制命令", "control_command", MessageSourceType.CLOUD), // 云端下发控制命令
    CONTROL_ACKNOWLEDGEMENT("控制确认", "control_acknowledgement", MessageSourceType.EDGE), // 边端确认控制
    
    // 事件类消息
    EVENT("事件", "event", MessageSourceType.EDGE),          // 通用事件
    NOTIFICATION("通知", "notification", MessageSourceType.CLOUD), // 云端发起的通知
    
    // 属性类消息
    PROPERTY("属性", "property", MessageSourceType.EDGE),  // 属性更新
    
    // 心跳类消息
    HEARTBEAT("心跳", "heartbeat", MessageSourceType.UNKNOWN), // 心跳消息
    HEARTBEAT_REQUEST("心跳请求", "heartbeat_request", MessageSourceType.EDGE),  // 边端请求
    HEARTBEAT_RESPONSE("心跳响应", "heartbeat_response", MessageSourceType.CLOUD), // 云端响应
    
    // 自定义类型
    CUSTOM("自定义", "custom", MessageSourceType.UNKNOWN),
    
    // 不支持的类型
    UNSUPPORTED("不支持的类型", "unsupported", MessageSourceType.UNKNOWN);
    
    private final String description;
    private final String type;
    private final MessageSourceType messageSourceType;
    
    // 构造函数
    MessageTypeEnum(String description, String type, MessageSourceType messageSourceType) {
        this.description = description;
        this.type = type;
        this.messageSourceType = messageSourceType;
    }
    
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
