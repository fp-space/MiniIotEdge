package com.iothub.message.application.core.config.conditions;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.lang.NonNull;

public class EdgeCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
        String enable = context.getEnvironment().getProperty("iot.enable");
        String tag = context.getEnvironment().getProperty("iot.tag");
        return "true".equalsIgnoreCase(enable) && "edge".equalsIgnoreCase(tag);
    }
}

