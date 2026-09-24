# ECharts Plugin

ECharts 图表插件，预置常用组件和图表类型。

## 导出

| 导出         | 类型 | 说明         |
| ------------ | ---- | ------------ |
| `default`（`echarts.ts`） | 对象 | ECharts 模块对象 |
| `EchartsUI`  | 组件 | 图表容器组件 |
| `ECOption`   | 类型 | 图表配置类型 |
| `useEcharts` | 函数 | 组合式函数   |

## 使用

```ts
import { EchartsUI, useEcharts } from '@/components/echarts';
```

## 类型

```ts
import type { ECOption } from '@/components/echarts';
```

需要注册额外图表或地图时，从具体模块导入 ECharts 对象：

```ts
import echarts from '@/components/echarts/echarts';
```

## 预置组件

- TitleComponent
- TooltipComponent
- GridComponent
- LegendComponent
- ToolboxComponent
- DatasetComponent
- TransformComponent

## 预置图表

- BarChart
- LineChart
- PieChart
- RadarChart
