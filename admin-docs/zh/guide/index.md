# 项目介绍 {#introduction}

Admin Platform 是基于 Spring Boot、Vue 3 和 TypeScript 的开源管理平台，采用前后端分离架构，为业务系统开发提供可复用的工程基础。

## 核心能力 {#core-capabilities}

| 模块       | 内容                           |
| ---------- | ------------------------------ |
| 系统管理   | 用户、角色、菜单权限和系统配置 |
| 认证与会话 | 访问控制、在线会话管理         |
| 开发工具   | 定时任务、模板驱动的代码生成   |
| 前端基础   | 通用组件、主题偏好和国际化     |

## 工程结构 {#project-structure}

| 目录                  | 用途                   |
| --------------------- | ---------------------- |
| `admin-server`        | 后端启动模块与运行配置 |
| `admin-module-system` | 系统管理等业务功能     |
| `admin-framework`     | 后端公共能力           |
| `admin-ui`            | Vue 前端               |
| `admin-docs`          | 官网与开发文档         |

## 开始了解 {#explore-the-project}

- [快速开始](./quick-start)：在本地启动后端和前端。
- [在线体验](https://admin-demo.geoearth.dev)：浏览后台界面，数据分析和工作台使用静态演示数据。
- [GitHub](https://github.com/geoearth-dev/admin-platform)：查看源码、反馈问题或参与贡献。

## 许可证与致谢 {#license-and-acknowledgments}

项目采用 [MIT 协议](https://github.com/geoearth-dev/admin-platform/blob/main/LICENSE)。感谢 [RuoYi](https://github.com/yangzongzhuan/RuoYi-Vue) 与 [Vue Vben Admin](https://github.com/vbenjs/vue-vben-admin) 提供的实现参考与组件基础。
