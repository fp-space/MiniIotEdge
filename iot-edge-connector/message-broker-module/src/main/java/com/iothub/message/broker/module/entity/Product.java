package com.iothub.message.broker.module.entity;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(fluent = true)
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private Long id;                        // 产品ID
    private String productKey;              // 产品Key
    private String name;                    // 产品名称
    private String description;             // 产品描述
    private String manufacturerCode;        // 关联的厂家编码
    private String modelCode;               // 关联的型号编码
    private List<Model> modelList;          // 型号列表
}
