package com.iothub.message.broker.module.logic.connector;

import cn.hutool.core.util.StrUtil;
import com.iothub.message.broker.module.domain.Device;
import com.iothub.message.broker.module.domain.DeviceStatus;
import com.iothub.message.broker.module.domain.MessageRequest;
import com.iothub.message.broker.module.enums.MessageTypeEnum;
import com.iothub.message.broker.module.logic.handler.MqttMessageSenderHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;

/**
 * 默认设备连接器抽象类，提供了设备控制命令的模板方法，
 * 业务逻辑需要由子类实现。包括命令执行、属性报告、事件报告等。
 */
@Slf4j
public abstract class DefaultDeviceConnector implements DeviceConnector {
    // 使用 ThreadLocal 来保证每个虚拟线程拥有独立的 Device 实例
    private static final ThreadLocal<Device> deviceThreadLocal = new ThreadLocal<>();
    private DeviceStatus cachedStatus; // 缓存设备状态，减少频繁获取的开销
    @Resource
    private MqttMessageSenderHandler mqttMessageSenderHandler;
    
    /**
     * 执行控制命令，模板方法
     *
     * @param request 输入参数
     */
    @Override
    public final void exec(MessageRequest request) {
        String identify = request.identify();
        Map<String, Object> params = request.inputParams();
        log.info("Executing identify: identify={}, params={}", identify, params);
        
        try {
            // 参数校验（可以通过抽象方法自定义）
            validateExecParams(identify, params);
            
            // 执行控制命令，交由子类实现
            Object result = executeControlCommand(identify, params);
            
            // 处理命令结果
            handleResult(result, MessageTypeEnum.CONTROL_COMMAND);
        } catch (Exception e) {
            log.error("Error executing identify: {}", e.getMessage(), e);
            handleExecError(identify, e);
        }
    }
    
    /**
     * 上报属性（业务逻辑由子类实现）
     */
    @Override
    public final void reportProperty() {
        log.info("Device {} Reporting property", this.getDevice().code());
        try {
            // 子类实现上报属性的具体业务逻辑
            Object result = doReportProperty();
            
            if (Objects.isNull(result)) {
                return;
            }
            
            // 处理命令结果（例如将结果上报到 MQTT 或记录日志）
            handleResult(result, MessageTypeEnum.PROPERTY);
        } catch (Exception e) {
            log.error("Error reporting device: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 上报事件（业务逻辑由子类实现）
     */
    @Override
    public final void reportEvent() {
        log.info("Device {} Reporting event", this.getDevice().code());
        try {
            // 子类实现上报属性的具体业务逻辑
            Object result = doReportEvent();
            
            if (Objects.isNull(result)) {
                return;
            }
            
            // 处理命令结果（例如将结果上报到 MQTT 或记录日志）
            handleResult(result, MessageTypeEnum.EVENT);
        } catch (Exception e) {
            log.error("Error reporting device: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 获取设备状态
     *
     * @return 设备状态
     */
    @Override
    public final DeviceStatus getDeviceStatus() {
        if (cachedStatus == null) {
            log.info("Fetching device status...");
            cachedStatus = fetchDeviceStatus();
        }
        log.info("Returning device status: {}", cachedStatus);
        return cachedStatus;
    }
    
    /**
     * 获取设备状态的具体业务逻辑（由子类实现）
     *
     * @return 设备状态
     */
    public abstract DeviceStatus fetchDeviceStatus();
    
    /**
     * 上报事件的具体实现（由子类实现）
     */
    public abstract Object doReportEvent();
    
    public Device getDevice() {
        return deviceThreadLocal.get();
    }
    
    /**
     * 上报属性的具体实现（由子类实现）
     */
    public abstract Object doReportProperty();
    
    public void setDevice(Device device) {
        deviceThreadLocal.set(device);
    }
    
    /**
     * 校验执行命令时的参数
     *
     * @param identify 命令
     * @param params   参数
     * @throws IllegalArgumentException 参数不合法时抛出异常
     */
    private void validateExecParams(String identify, Map<String, Object> params) {
        log.info("开始校验参数：{}, {}", identify, params);
        if (identify == null || identify.isEmpty()) {
            throw new IllegalArgumentException("Command cannot be null or empty");
        }
        if (params == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }
        log.info("参数校验结束！！！");
    }
    
    /**
     * 执行控制命令的具体逻辑（由子类实现）
     *
     * @param params 参数
     * @return 执行结果
     */
    public abstract Object executeControlCommand(String identify, Map<String, Object> params);
    
    private void handleResult(Object result, MessageTypeEnum messageTypeEnum) {
        log.info("handle result：{}, messageTypeEnum:{}", result, messageTypeEnum);
        mqttMessageSenderHandler.publish("/topic", StrUtil.toString(result), messageTypeEnum);
    }
    
    /**
     * 处理命令执行时的错误（由子类实现）
     *
     * @param identify 命令
     * @param e        异常
     */
    public void handleExecError(String identify, Exception e) {
        // 默认错误处理逻辑：记录日志并执行基础处理
        log.error("Error executing identify: identify={}. Exception: {}", identify, e.getMessage());
    }
    
    /**
     * 连接器标识符
     *
     * @return 唯一标识符
     */
    public abstract String getIdentify();
    
    /**
     * 处理命令执行后的结果
     *
     * @param identify 命令
     * @param result   执行结果
     */
    public void handleCommandResult(String identify, Object result) {
        log.info("处理命令执行后的结果");
    }
    
    /**
     * 提供默认的 getTag() 实现，返回当前类的简单名称
     */
    public String getTag() {
        return this.getClass().getSimpleName();
    }
    
    public void clearDevice() {
        deviceThreadLocal.remove();
    }
}
