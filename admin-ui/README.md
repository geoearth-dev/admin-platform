# Admin Platform 前端

管理平台的 Vue 3 前端，使用 TypeScript、Vite、Element Plus 和 Tailwind CSS。Vben 组件在本地 `src/plugins/vben-ui` 中维护，业务组件位于 `src/components`。

## 环境与启动

Node.js 版本要求为 `^22.18.0 || >=24.12.0`，以 [package.json](package.json) 的 `engines` 为准。以下命令均在 `admin-ui` 目录执行：

```sh
npm install
npm run dev
```

开发服务通过 [vite.config.ts](vite.config.ts) 将 `/api` 请求代理到 `http://localhost:8080`，并移除路径中的 `/api` 前缀。

## 目录说明

| 路径 | 用途 |
| --- | --- |
| `src/api` | 后端接口调用 |
| `src/assets` | 样式、图标与图片 |
| `src/components` | 通用业务组件 |
| `src/constants/admin.ts` | 项目链接与品牌资源 |
| `src/layout` | 页面布局与布局部件 |
| `src/plugins/vben-ui` | Vben 表单、弹窗、标签页等组件 |
| `src/plugins/preference` | 偏好设置与主题状态 |
| `src/plugins/locale` | 国际化配置与语言资源 |
| `src/router` | 路由与访问控制 |
| `src/store` | Pinia 状态管理 |
| `src/utils` | 请求、缓存与通用工具 |
| `src/views/sys` | 系统业务页面 |
| `src/views/examples`、`src/views/demos` | 组件及功能示例 |

路径别名 `@` 指向 `src`。项目内组件和工具通过 `@/components/...`、`@/plugins/...`、`@/utils/...` 导入。

应用入口为 [src/main.ts](src/main.ts)，项目偏好配置为 [src/preference.ts](src/preference.ts)。

## 检查与构建

类型检查：

```sh
npx vue-tsc --noEmit -p tsconfig.app.json
```

打包与本地预览：

```sh
npm run build-only
npm run preview
```

构建产物输出到 `dist`。以上分别执行类型检查和打包；当前 `build` 脚本引用了未定义的 `type-check` 脚本，暂不作为构建入口。

代码检查与格式化：

```sh
npx eslint src
npm run format
```

`npm run format` 会改写 `src` 下的文件，提交前需检查差异。

## 组件文档

- [数组表单](src/views/demos/form-array/README.md)
- [缓存管理](src/utils/cache/README.md)
- [动画组件](src/components/motion/README.md)
- [表格组件](src/components/vxe-table/README.md)
- [图表组件](src/components/echarts/README.md)
