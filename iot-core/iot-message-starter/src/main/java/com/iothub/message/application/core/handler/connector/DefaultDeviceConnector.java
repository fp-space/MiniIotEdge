package com.iothub.message.application.core.handler.connector;

import cn.hutool.core.util.StrUtil;
import com.iothub.message.application.core.handler.mqtt.MqttMessageSenderHandler;
import com.iothub.message.application.domain.DataPayload;
import com.iothub.message.application.domain.Device;
import com.iothub.message.application.domain.DeviceStatus;
import com.iothub.message.application.domain.MessageRequest;
import com.iothub.message.application.enums.MessageTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Lazy;

import java.util.Map;

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
    @Lazy
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
            Map<String, Object> result = doExec(identify, params);
            
            if(result.isEmpty()){
                return;
            }
            
            DataPayload<Object> payload = DataPayload.builder()
                    .withData(result)
                    .withMessageType(MessageTypeEnum.COMMAND_ACKNOWLEDGMENT)
                    .build();
            
            // 处理命令结果
            handleResult(payload);
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
            Map<String, Object> result = doReportProperty();
            
            if (result.isEmpty()) {
                return;
            }
            
            DataPayload<Object> payload = DataPayload.builder()
                    .withData(result)
                    .withMessageType(MessageTypeEnum.PROPERTY) // 设置枚举类型为 EVENT
                    .build();
            
            // 处理命令结果（例如将结果上报到 MQTT 或记录日志）
            handleResult(payload);
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
            Map<String, Object> result = doReportEvent();
            
            if(result.isEmpty()){
                return;
            }
            
            DataPayload<Object> payload = DataPayload.builder()
                    .withData(result)
                    .withMessageType(MessageTypeEnum.EVENT)
                    .build();
            
            // 处理命令结果（例如将结果上报到 MQTT 或记录日志）
            handleResult(payload);
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
    
    public void setDevice(Device device) {
        deviceThreadLocal.set(device);
    }
    
    public Device getDevice() {
        return deviceThreadLocal.get();
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
    public abstract Map<String, Object> doReportEvent();
    
    /**
     * 上报属性的具体实现（由子类实现）
     */
    public abstract Map<String, Object> doReportProperty();
    
    /**
     * 执行控制命令的具体逻辑（由子类实现）
     *
     * @param params 参数
     * @return 执行结果
     */
    public abstract Map<String, Object> doExec(String identify, Map<String, Object> params);
    
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
    
  
    private void handleResult(DataPayload<Object> payload) {
        log.info("handle result：{}", payload);
        // todo: 根据不同类型，选择不同topic进行发送，目前默认是 /topic 测试
        mqttMessageSenderHandler.publish("/topic", StrUtil.toString(payload.getData()), payload.getMessageTypeEnum());
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
     * 提供默认的 getTag() 实现，返回当前类的简单名称
     */
    public String getTag() {
        return this.getClass().getSimpleName();
    }
    
    public void clearDevice() {
        deviceThreadLocal.remove();
    }
}
