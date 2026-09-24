# Admin Platform 前端

[English](./README.md) | **简体中文** | [项目 README](../README.zh-CN.md)

管理平台的 Vue 3 前端，使用 TypeScript、Vite、Element Plus 和 Tailwind CSS。Vben 组件在本地 `src/plugins/vben-ui` 中维护，通用业务组件位于 `src/components`。

## 开发

Node.js 版本要求为 `^22.18.0 || >=24.12.0`。以下命令均在 `admin-ui` 目录执行：

```sh
npm install
npm run dev
```

[vite.config.ts](./vite.config.ts) 将 `/api` 请求代理到 `http://localhost:8080`，并移除 `/api` 前缀。后端启动见 [项目 README](../README.zh-CN.md#快速开始)。

## 目录说明

| 路径 | 用途 |
| --- | --- |
| `src/api` | 后端接口调用 |
| `src/assets` | 样式、图标与图片 |
| `src/components` | 通用业务组件 |
| `src/constants/admin.ts` | 项目链接与品牌资源 |
| `src/layout` | 页面布局 |
| `src/plugins/vben-ui` | 本地 Vben 组件、表单与布局 |
| `src/plugins/preference` | 偏好设置与主题状态 |
| `src/plugins/locale` | 国际化配置与语言资源 |
| `src/router` | 路由与访问控制 |
| `src/store` | Pinia 状态管理 |
| `src/utils` | 请求、缓存与通用工具 |
| `src/views/sys` | 系统业务页面 |
| `src/views/examples`、`src/views/demos` | 组件及功能示例 |

路径别名 `@` 指向 `src`。应用入口为 [src/main.ts](./src/main.ts)，默认偏好配置为 [src/preference.ts](./src/preference.ts)。

## 检查与构建

| 命令 | 用途 |
| --- | --- |
| `npm run type-check` | 执行 TypeScript / Vue 类型检查 |
| `npm run build` | 执行类型检查并构建到 `dist` |
| `npm run build-only` | 仅构建，不单独执行类型检查 |
| `npm run preview` | 本地预览构建产物 |
| `npx eslint src` | 检查源码 |
| `npm run format` | 格式化 `src` 下的文件，会改写文件 |

具体脚本见 [package.json](./package.json)。格式化后请检查文件差异。

## 组件文档

- [页面布局](src/layout/basic/README.md)
- [数组表单](src/views/demos/form-array/README.md)
- [缓存管理](src/utils/cache/README.md)
- [动画组件](src/components/motion/README.md)
- [表格组件](src/components/vxe-table/README.md)
- [图表组件](src/components/echarts/README.md)
