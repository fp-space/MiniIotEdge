package com.iothub.device.connection.module.entity;

import java.util.List;

/**
 * 厂家实体
 */
public record Manufacturer(
        Long id,                       // 厂家ID
        String code,                   // 厂商编码
        String name,                   // 厂家名称
        String address,                // 厂家地址
        String contactInfo,            // 联系信息
        List<Product> productList      // 产品列表
) {
    /**
     * 产品实体
     */
    public record Product(
            Long id,                        // 产品ID
            String productKey,              // 产品Key
            String name,                    // 产品名称
            String description,             // 产品描述
            String manufacturerCode,        // 关联的厂家编码
            String modelCode,               // 关联的型号编码
            List<Model> modelList           // 型号列表
    ) {}
    
    /**
     * 型号实体
     */
    public record Model(
            Long id,                        // 型号ID
            String code,                    // 型号编码
            String name,                    // 型号名称
            String specification            // 型号规格描述
    ) {}
}
