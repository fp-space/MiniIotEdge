package com.iothub.message.broker.module.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {
    private String identify; // 命令标识
    private Map<String, Object> inputParams; // 输入参数
}
