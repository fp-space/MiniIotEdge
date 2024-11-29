package com.iothub.message.broker.module.service;

import com.iothub.message.application.domain.DeviceStatus;
import com.iothub.message.application.core.handler.connector.DefaultDeviceConnector;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CustomConnector extends DefaultDeviceConnector {

    @Override
    public DeviceStatus fetchDeviceStatus() {
        return null;
    }

    @Override
    public Map<String, Object> doReportEvent() {
        return Map.of("key", "value");
    }

    @Override
    public Map<String, Object> doReportProperty() {
        return Map.of("key", "value");
    }

    @Override
    public Map<String, Object> doExec(String identify, Map<String, Object> params) {
        return null;
    }

    @Override
    public void handleExecError(String command, Exception e) {

    }

    @Override
    public String getIdentify() {
        return "custom";
    }
}