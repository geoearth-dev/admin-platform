# Admin Platform
[![部署状态](https://github.com/geoearth-dev/admin-platform/actions/workflows/deploy.yml/badge.svg?branch=main)](https://github.com/geoearth-dev/admin-platform/actions/workflows/deploy.yml)
Admin Platform 是基于 Spring Boot、Vue 3 和 TypeScript 的前后端分离管理平台，提供用户与角色管理、菜单权限、系统配置、在线会话、任务调度和代码生成等功能。

## 工程结构

| 目录 | 用途 |
| --- | --- |
| `admin-server` | 后端启动模块、运行配置和日志配置 |
| `admin-module-system` | 系统管理、认证、监控与消息等业务 |
| `admin-framework` | 通用工具、安全、数据访问、Excel、任务调度与代码生成等基础模块 |
| `admin-ui` | Vue 前端、业务页面和本地组件 |
| `sql` | MySQL 数据快照和数据调整脚本 |

## 后端开发

运行环境：JDK 17 或以上版本、Maven 3.9 或以上版本、MySQL、Redis。

1. 按 [数据库初始化说明](sql/README.md) 准备数据库；演示基准为本地导出的 `sql/demo-baseline.sql`，不适用于已有业务库升级。
2. 在 [application-dev.yml](admin-server/src/main/resources/application-dev.yml) 中配置开发环境的数据库、Redis 和文件存储等参数。公共配置位于 [application.yml](admin-server/src/main/resources/application.yml)，生产环境配置位于 [application-prod.yml](admin-server/src/main/resources/application-prod.yml)。
3. 在工程根目录构建并启动后端：

```sh
mvn clean package
java -jar admin-server/target/admin-server-0.1.0-SNAPSHOT.jar --spring.profiles.active=dev
```

后端默认端口为 `8080`。数据访问使用 MyBatis-Plus，接口文档使用 SpringDoc OpenAPI，日志由 Logback 管理。

## 前端开发

前端的安装、启动、构建与目录说明见 [admin-ui/README.md](admin-ui/README.md)。

## 项目入口

- [项目主页](https://admin.geoearth.dev)
- [项目文档](https://admin-docs.geoearth.dev)
- [源码仓库](https://github.com/geoearth-dev/admin-platform)
