# inject-app-loading

在应用启动前向 HTML 注入加载动画，由 [vite.config.ts](../../../../vite.config.ts) 注册。

- 通过环境变量 `VITE_INJECT_APP_LOADING=true` 启用。
- 默认使用 [default-loading.html](./default-loading.html)，可在 `admin-ui` 根目录放置 `loading.html` 覆盖。
- 根据缓存中的主题设置切换明暗外观。
