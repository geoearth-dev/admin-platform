# Motion Plugin

基于 `@vueuse/motion` 的动画封装，入口为 `admin-ui/src/components/motion/index.ts`。

## 导出

| 导出              | 类型 | 说明       |
| ----------------- | ---- | ---------- |
| `Motion`          | 组件 | 动画组件   |
| `MotionGroup`     | 组件 | 动画组组件 |
| `MotionDirective` | 指令 | 动画指令   |
| `MotionPlugin`    | 插件 | Vue 插件   |

## 使用

`MotionPlugin` 已在 [main.ts](../../main.ts) 中全局注册，页面无需重复安装。组件通过本地入口导入：

```ts
import { Motion, MotionGroup } from '@/components/motion';
```

## 类型

```ts
import type { MotionPreset } from '@/components/motion';
import { MotionPresets } from '@/components/motion';
```

`MotionPreset` 为内置动画名称类型，`MotionPresets` 为对应名称列表，定义见 [types.ts](./types.ts)。

底层动画参数类型由依赖包直接提供：

```ts
import type { MotionVariants, UseMotionOptions } from '@vueuse/motion';
```
