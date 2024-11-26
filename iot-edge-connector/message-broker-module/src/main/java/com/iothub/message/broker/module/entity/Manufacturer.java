package com.iothub.message.broker.module.entity;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 厂家实体
 */
@Data
@Accessors(fluent = true)
@AllArgsConstructor
@NoArgsConstructor
public class Manufacturer {
    private Long id;                       // 厂家ID
    private String code;                   // 厂商编码
    private String name;                   // 厂家名称
    private String address;                // 厂家地址
    private String contactInfo;            // 联系信息
    private List<Product> productList;     // 产品列表
}