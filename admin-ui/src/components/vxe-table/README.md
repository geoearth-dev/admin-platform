# VXE Table

基于 `vxe-table`、`vxe-pc-ui` 的本地表格封装，包含查询表单、分页、工具栏和已读行标记。

## 模块关系

- `init.ts`：首次使用时注册 VXE 组件、加载样式，同步项目语言和明暗主题。
- `adapter.ts`：表格默认配置和 Element Plus 单元格渲染器。
- 查询表单：直接复用 `@/plugins/vben-ui/form-ui` 的 `useVbenForm` 和组件注册。

不需要在 `main.ts` 重复注册，也不需要把表格 adapter 放入 form-ui。

## 使用

```vue
<script setup lang="ts">
import { useVbenVxeGrid } from '@/components/vxe-table'

interface UserRow {
  id: number
  name: string
}

const [Grid, gridApi] = useVbenVxeGrid<UserRow>({
  tableTitle: '用户',
  gridOptions: {
    rowConfig: { keyField: 'id' },
    columns: [
      { field: 'id', title: '编号' },
      { field: 'name', title: '姓名' },
    ],
  },
  tableData: [{ id: 1, name: '张三' }],
})

// gridApi.setState({ tableData: [] }) 可以清空数据。
</script>

<template>
  <Grid />
</template>
```

`VxeGridProps` 是 VXE 原生配置类型；需要本封装的 `formOptions`、`viewedRowOptions` 等字段时使用 `VbenVxeGridProps`。列配置可使用 `VxeTableGridColumns<Row>`，带搜索按钮的表格配置使用 `VxeTableGridOptions<Row>`。

## 查询和单元格

`formOptions` 的 schema 与本地 VbenForm 一致。查询函数通过 `proxyConfig.ajax.query(params, formValues)` 接收分页信息和最近一次提交的表单值，支持表单 codec。`gridApi.query()` 保留当前页，`gridApi.reload()` 从第一页重新查询。

默认分页响应为 `{ items: Row[], total: number }`。如果接口返回 `{ records, total }`，在 `gridOptions.proxyConfig.response` 中设置 `result: 'records'`、`total: 'total'`、`list: 'records'`，或在查询函数中转换返回值。

已注册以下 `cellRender.name`：

- `CellImage`：Element Plus 图片及预览，读取当前列字段。
- `CellLink`：Element Plus 链接按钮，`props.text` 为文字，`attrs.onClick` 接收单元格参数。
- `CellOperation`：操作按钮；`options` 支持 `'view'`、`'edit'`、`'delete'` 或 `{ code, text }`，`attrs.onClick` 接收 `OnActionClickParams<Row>`。按钮只触发事件，具体业务逻辑由页面处理。

统一配置可直接修改 `adapter.ts`。也可在首次渲染表格前调用 `setupVbenVxeTable({ configVxeTable(ui) { /* 自定义配置 */ } })`。

## 已读行

通过 `viewedRowOptions` 开启，行标识默认读取 `rowConfig.keyField`，未配置时使用 `id`。支持内存、localStorage、sessionStorage、IndexedDB 和自定义存储。`persist` 未配置时仅保存在当前表格实例中。

`gridApi` 提供 `markRowAsViewed`、`markKeysAsViewed`、`isRowViewed`、`getViewedKeys`、`removeViewedKeys` 和 `clearViewedRows`。`actionCodes` 可使 `CellOperation` 的指定操作自动标记已读。`persist` 和 `keyField` 在实例创建后固定，样式及操作码可以通过 `setState` 调整。

## 示例数据

远程表格示例统一调用 `@/api/example/table` 的 `getExampleTableApi`，当前在方法内返回 120 条本地商品数据，支持分页、排序、关键词和日期筛选，不需要后端。

接入真实业务时，替换该方法为实际请求并保留 `{ items, total }` 返回结构即可。`basic.vue`、`tree.vue`、`virtual.vue` 继续使用各自的本地数据。
