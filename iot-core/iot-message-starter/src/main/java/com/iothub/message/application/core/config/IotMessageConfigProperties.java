package com.iothub.message.application.core.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "iot")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IotMessageConfigProperties {
    private String tag;
    private Boolean enabled;
}
