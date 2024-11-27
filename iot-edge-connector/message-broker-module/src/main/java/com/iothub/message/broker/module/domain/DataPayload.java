package com.iothub.message.broker.module.domain;

import com.iothub.message.broker.module.enums.MessageTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataPayload<T> {
    // 数据部分
    private T data;
    
    // 数据类型
    private MessageTypeEnum messageTypeEnum;
    
    // MQTT 配置部分
    private Boolean enabled;
    private int qos;       // Quality of Service (0, 1, or 2)
    private boolean retained; // 是否为保留消息
    
    // 使用 Builder 模式
    public static class Builder<T> {
        private T data;
        private MessageTypeEnum messageTypeEnum;
        private Boolean enabled = false;
        private int qos = 0;
        private boolean retained = false;
        
        // 只传数据构造
        public Builder<T> withData(T data) {
            this.data = data;
            return this;
        }
        
        // 可选的 MQTT 配置
        public Builder<T> withMqttConfig(int qos, boolean retained) {
            this.enabled = true;
            this.qos = qos;
            this.retained = retained;
            return this;
        }
        
        // 设置消息类型
        public Builder<T> withMessageType(MessageTypeEnum messageTypeEnum) {
            this.messageTypeEnum = messageTypeEnum;
            return this;
        }
        
        // 构建 DataPayload 对象
        public DataPayload<T> build() {
            return new DataPayload<>(data, messageTypeEnum, enabled, qos, retained);
        }
    }
    
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }
}
