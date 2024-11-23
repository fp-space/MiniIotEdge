package com.iothub.message.broker.module.connector;

import com.iothub.message.broker.module.entity.Device;
import com.iothub.message.broker.module.entity.DeviceStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 默认设备连接器抽象类，提供了设备控制命令的模板方法，
 * 业务逻辑需要由子类实现。包括命令执行、属性报告、事件报告等。
 */
@Slf4j
public abstract class DefaultDeviceConnector implements DeviceConnector {
    private DeviceStatus cachedStatus; // 缓存设备状态，减少频繁获取的开销
    
    // 使用 ThreadLocal 来保证每个虚拟线程拥有独立的 Device 实例
    private static final ThreadLocal<Device> deviceThreadLocal = new ThreadLocal<>();
    
    public DefaultDeviceConnector() {}
    
    /**
     * 执行控制命令，模板方法
     * @param identify 命令
     * @param params 参数
     */
    @Override
    public final void exec(String identify, Map<String, Object> params) {
        log.info("Executing identify: identify={}, params={}", identify, params);
        
        try {
            // 参数校验（可以通过抽象方法自定义）
            validateExecParams(identify, params);
            
            // 执行控制命令，交由子类实现
            Object result = executeControlCommand(identify, params);
            
            // 处理命令结果（例如将结果上报到 MQTT 或记录日志）
            handleCommandResult(identify, result);
            
        } catch (Exception e) {
            log.error("Error executing identify: {}", e.getMessage(), e);
            handleExecError(identify, e);
        }
    }
    
    /**
     * 上报属性（业务逻辑由子类实现）
     * @param propertyName 属性名称
     * @param value 属性值
     */
    @Override
    public final void reportProperty(String propertyName, Object value) {
        log.info("Reporting property: {}={}", propertyName, value);
        try {
            // 子类实现上报属性的具体业务逻辑
            doReportProperty(propertyName, value);
        } catch (Exception e) {
            log.error("Error reporting property: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 上报事件（业务逻辑由子类实现）
     * @param event 事件名称
     * @param payload 事件内容
     */
    @Override
    public final void reportEvent(String event, Map<String, Object> payload) {
        log.info("Reporting event: {} with payload: {}", event, payload);
        try {
            // 子类实现上报事件的具体业务逻辑
            doReportEvent(event, payload);
        } catch (Exception e) {
            log.error("Error reporting event: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 获取设备状态
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
     * 校验执行命令时的参数
     * @param identify 命令
     * @param params 参数
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
     * 获取设备状态的具体业务逻辑（由子类实现）
     * @return 设备状态
     */
    public abstract DeviceStatus fetchDeviceStatus();
    
    /**
     * 执行控制命令的具体逻辑（由子类实现）
     * @param params 参数
     * @return 执行结果
     */
    public abstract Object executeControlCommand(String identify, Map<String, Object> params);
    
    /**
     * 上报属性的具体实现（由子类实现）
     * @param propertyName 属性名称
     * @param value 属性值
     */
    public abstract void doReportProperty(String propertyName, Object value);
    
    /**
     * 上报事件的具体实现（由子类实现）
     * @param event 事件名称
     * @param payload 事件内容
     */
    public abstract void doReportEvent(String event, Map<String, Object> payload);
    
    /**
     * 处理命令执行后的结果
     * @param identify 命令
     * @param result 执行结果
     */
    public void handleCommandResult(String identify, Object result){
        log.info("处理命令执行后的结果");
    }
    
    /**
     * 处理命令执行时的错误（由子类实现）
     * @param identify 命令
     * @param e 异常
     */
    public void handleExecError(String identify, Exception e) {
        // 默认错误处理逻辑：记录日志并执行基础处理
        log.error("Error executing identify: identify={}. Exception: {}", identify, e.getMessage());
    }
    
    /**
     * 提供默认的 getTag() 实现，返回当前类的简单名称
     */
    public String getTag() {
        return this.getClass().getSimpleName();
    }
    
    public Device getDevice() {
        return deviceThreadLocal.get();
    }
    
    public void setDevice(Device device) {
        deviceThreadLocal.set(device);
    }
    
    public void clearDevice() {
        deviceThreadLocal.remove();
    }
}
