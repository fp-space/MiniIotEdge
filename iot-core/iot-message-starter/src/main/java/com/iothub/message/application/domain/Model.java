package com.iothub.message.application.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@AllArgsConstructor
@NoArgsConstructor
public class Model {
    private Long id;                        // 型号ID
    private String code;                    // 型号编码
    private String name;                    // 型号名称
    private String specification;           // 型号规格描述
}
