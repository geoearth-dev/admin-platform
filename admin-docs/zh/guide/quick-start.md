# 快速开始 {#quick-start}

## 环境要求 {#prerequisites}

- JDK 17 或以上版本、Maven 3.9 或以上版本。
- 后台前端的 Node.js 版本满足 `^22.18.0 || >=24.12.0`。
- 已启动 MySQL 和 Redis。

## 获取源码与配置 {#clone-and-configure}

```bash
git clone https://github.com/geoearth-dev/admin-platform.git
cd admin-platform
```

在独立的开发或演示数据库中导入 `sql/demo-baseline.sql`，调整 `admin-server/src/main/resources/application-dev.yml` 中的数据库、Redis 和文件存储等配置。

同目录下的 `application.yml` 为公共配置，`application-prod.yml` 为生产环境配置。

## 启动后端 {#start-the-backend}

在仓库根目录执行：

```bash
mvn clean package
java -jar admin-server/target/admin-server-0.1.0-SNAPSHOT.jar --spring.profiles.active=dev
```

后端默认端口为 `8080`。项目版本变化后，请使用 `admin-server/target` 下实际生成的 JAR 文件名。也可以在 IntelliJ IDEA 中运行启动类，并激活 `dev` Profile。

## 启动前端 {#start-the-frontend}

另开终端，在仓库根目录执行：

```bash
cd admin-ui
npm install
npm run dev
```

## 检查与构建 {#check-and-build}

在 `admin-ui` 目录执行：

```bash
npm run type-check
npm run build
```

前端构建产物位于 `admin-ui/dist`，更多说明见 [前端 README](https://github.com/geoearth-dev/admin-platform/blob/main/admin-ui/README.zh-CN.md)。
