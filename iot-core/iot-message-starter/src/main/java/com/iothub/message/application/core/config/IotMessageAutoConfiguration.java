package com.iothub.message.application.core.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = "com.iothub.message.application")  // 指定扫描其他包
public class IotMessageAutoConfiguration {
}
