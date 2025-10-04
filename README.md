企智汇   
Hive 寓意蜂巢式汇聚（多模块协作），Zhi 加智慧。

<p align="center">
 <img src="https://img.shields.io/badge/Pig-3.9-success.svg" alt="Build Status">
 <img src="https://img.shields.io/badge/Spring%20Cloud-2025-blue.svg" alt="Coverage Status">
 <img src="https://img.shields.io/badge/Spring%20Boot-3.5-blue.svg" alt="Downloads">
 <img src="https://img.shields.io/badge/Vue-3.5-blue.svg" alt="Downloads">
 <img src="https://img.shields.io/github/license/pig-mesh/pig"/>
 <img src="https://gitcode.com/pig-mesh/pig/star/badge.svg"/>
</p>

## 系统说明

- 基于 Spring Cloud 、Spring Boot、 OAuth2 的 RBAC **企业快速开发平台**， 同时支持微服务架构和单体架构
- 提供对 Spring Authorization Server 生产级实践，支持多种安全授权模式
- 提供对常见容器化方案支持 Kubernetes、Rancher2 、Kubesphere、EDAS、SAE 支持

#### 使用文档

PIG 提供了详尽的部署文档 👉 [wiki.pig4cloud.com](https://wiki.pig4cloud.com)，涵盖开发环境配置、服务端启动、前端运行等关键步骤。

#### 其他产品

- 👉🏻 [PIGX 在线体验](http://home.pig4cloud.com:38081)

- 👉🏻 [自研BPMN工作流引擎](http://home.pig4cloud.com:38082)

- 👉🏻 [大模型 RAG 知识库](http://home.pig4cloud.com:38083)


### 核心依赖

| 依赖                         | 版本     |
|-----------------------------|--------|
| Spring Boot                 | 3.5.6  |
| Spring Cloud                | 2025   |
| Spring Cloud Alibaba        | 2023   |
| Spring Authorization Server | 1.5.2  |
| Mybatis Plus                | 3.5.14 |
| Vue                         | 3.5    |
| Element Plus                | 2.7    |

### 模块说明

```lua
pig-ui  -- https://gitee.com/log4j/pig-ui

pig
├── pig-boot -- 单体模式启动器[9999]
├── pig-auth -- 授权服务提供[3000]
└── pig-common -- 系统公共模块
     ├── pig-common-bom -- 全局依赖管理控制
     ├── pig-common-core -- 公共工具类核心包
     ├── pig-common-datasource -- 动态数据源包
     ├── pig-common-log -- 日志服务
     ├── pig-common-oss -- 文件上传工具类
     ├── pig-common-mybatis -- mybatis 扩展封装
     ├── pig-common-seata -- 分布式事务
     ├── pig-common-websocket -- websocket 封装
     ├── pig-common-security -- 安全工具类
     ├── pig-common-swagger -- 接口文档
     ├── pig-common-feign -- feign 扩展封装
     └── pig-common-xss -- xss 安全封装
├── pig-register -- Nacos Server[8848]
├── pig-gateway -- Spring Cloud Gateway网关[9999]
└── pig-upms -- 通用用户权限管理模块
     └── pig-upms-api -- 通用用户权限管理系统公共api模块
     └── pig-upms-biz -- 通用用户权限管理系统业务处理模块[4000]
└── pig-visual
     └── pig-monitor -- 服务监控 [5001]
     ├── pig-codegen -- 图形化代码生成 [5002]
     └── pig-quartz -- 定时任务管理台 [5007]
```

基于PIG开发一个 协同管理平台
demo：http://www.rockoa.com/view_demo.html
      http://demo.rockoa.com/?m=login