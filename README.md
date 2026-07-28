# Boot Framework

A clean and extensible Spring Boot backend scaffold for modern web applications.

一个简洁、可扩展的 Spring Boot 后端开发脚手架，用于快速构建规范化的 Web API 项目。

## Features

- 统一 API 响应结构
- 全局异常处理
- 分页查询封装
- MyBatis-Plus 数据访问
- PostgreSQL 数据库支持
- Druid 数据库连接池
- OpenAPI 接口文档
- Scalar API 文档页面
- Logback 日志配置

## Tech Stack

- Java
- Spring Boot
- MyBatis-Plus
- PostgreSQL
- Druid
- SpringDoc OpenAPI
- Scalar

## Getting Started

### Requirements

- JDK 17+
- Maven 3.9+
- PostgreSQL

### Configuration

配置以下环境变量：

```text
DB_URL=jdbc:postgresql://localhost:5432/your_database
DB_USERNAME=your_username
DB_PASSWORD=your_password
