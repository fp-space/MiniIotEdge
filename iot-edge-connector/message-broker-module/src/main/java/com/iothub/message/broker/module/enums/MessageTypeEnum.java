package com.iothub.message.broker.module.enums;

public enum MessageTypeEnum {
    
    EVENT_NOTIFICATION("事件通知", "event_notification"),    // 事件通知
    PROPERTY_UPDATE("属性更新", "property_update"), // 属性更新
    HEARTBEAT_MONITORING("心跳监控", "heartbeat_monitoring"), // 心跳监控
    CONTROL_COMMAND("控制命令", "control_command"), // 控制命令
    RESPONSE_TO_EVENT("事件响应", "response_to_event"), // 事件响应
    UNSUPPORTED("不支持的类型", "unsupported");         // 不支持的类型
    
    private final String description;
    private final String type;
    
    // 构造函数
    MessageTypeEnum(String description, String type) {
        this.description = description;
        this.type = type;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getType() {
        return type;
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
