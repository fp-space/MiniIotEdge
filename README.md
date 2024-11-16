# IotEdge
边端平台


## 设备连接器（Device Connector）模块

1. 核心模块
   该模块包含平台的核心功能和接口，供其他模块调用。
core-service：核心服务模块，负责平台的基础服务和工具类，提供公共的接口和基础功能（例如：服务注册、配置管理、日志管理、常量定义等）。

2. 设备接入与协议适配模块
   该模块负责将不同设备的接入协议适配成统一的数据格式，处理不同协议的设备。

device-connection-service：设备连接管理服务模块，负责设备的接入、连接状态管理和心跳机制。
protocol-adapter-service：协议适配服务模块，处理各类协议（MQTT、HTTP、WebSocket等）的适配和数据转换。
3. 数据处理与消息转发模块
   该模块负责从设备接收数据、对数据进行格式化、转发并且存储。

data-ingestion-service：数据接入服务模块，接收设备发送的数据，并进行预处理，后续将数据送入消息队列。
message-broker-service：消息代理服务模块，负责将设备数据转发到目标系统或服务，比如数据存储或下游应用。
data-forwarding-service：数据转发服务模块，负责将接收到的数据根据不同业务逻辑进行转发。

4. 设备控制与命令下发模块
   该模块处理平台向设备下发控制命令和操作，确保命令正确执行。

device-control-service：设备控制服务模块，负责向设备下发控制命令，如设备重启、状态切换等。
5. 监控与告警模块
   该模块负责设备连接状态的监控和异常告警的处理。

device-status-monitor-service：设备状态监控服务模块，负责实时监控设备的连接状态和数据上报。
alert-service：告警服务模块，处理设备状态异常时的告警逻辑，通知运维人员或者系统进行修复。
6. 负载均衡与分布式管理模块
   在多节点环境中，这些模块处理节点之间的负载均衡和任务调度。

load-balancer-service：负载均衡服务模块，确保请求在不同节点间合理分配，避免某个节点过载。

## 目录

```text
├── core-service                  # 核心服务模块
│   ├── src/main/java/com/iothhub/core/
│   └── src/main/resources/
├── device-connection-service     # 设备连接管理服务
│   ├── src/main/java/com/iothhub/device/connection/
│   └── src/main/resources/
├── protocol-adapter-service      # 协议适配服务
│   ├── src/main/java/com/iothhub/device/protocol/
│   └── src/main/resources/
├── data-ingestion-service        # 数据接入服务
│   ├── src/main/java/com/iothhub/data/ingestion/
│   └── src/main/resources/
├── message-broker-service       # 消息代理服务
│   ├── src/main/java/com/iothhub/message/broker/
│   └── src/main/resources/
├── data-forwarding-service      # 数据转发服务
│   ├── src/main/java/com/iothhub/data/forwarding/
│   └── src/main/resources/
├── device-control-service       # 设备控制服务
│   ├── src/main/java/com/iothhub/device/control/
│   └── src/main/resources/
├── alert-service                # 告警服务
│   ├── src/main/java/com/iothhub/alert/
│   └── src/main/resources/
├── load-balancer-service        # 负载均衡服务
│   ├── src/main/java/com/iothhub/loadbalancer/
│   └── src/main/resources/

```

