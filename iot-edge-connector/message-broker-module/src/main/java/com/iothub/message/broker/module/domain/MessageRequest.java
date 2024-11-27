package com.iothub.message.broker.module.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(fluent = true)
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {
    private String identify; // 命令标识
    private Map<String, Object> inputParams; // 输入参数
}
