# Simple i18n

供基础组件使用的轻量国际化组合式函数，支持 `zh-CN` 和 `en-US`，默认中文。

`useSimpleLocale()` 返回 `$t`、`currentLocale` 和 `setSimpleLocale`。词条定义在 [messages.ts](./messages.ts)，找不到词条时返回原始键名。

业务页面的翻译使用项目的 [locale 模块](../../locale/index.ts)。
