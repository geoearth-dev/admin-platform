<script lang="ts" setup>
import type { VxeGridProps } from '@/components/vxe-table'

import { Page } from '@/components/page'

import { ElButton } from 'element-plus'

import { useVbenVxeGrid } from '@/components/vxe-table'
import { getExampleTableApi } from '@/api/example/table'

interface RowType {
  category: string
  color: string
  id: string
  price: string
  productName: string
  releaseDate: string
}

const gridOptions: VxeGridProps<RowType> = {
  checkboxConfig: {
    highlight: true,
    labelField: 'name',
  },
  columns: [
    { title: '序号', type: 'seq', width: 50 },
    { align: 'left', title: 'Name', type: 'checkbox', width: 100 },
    { field: 'category', sortable: true, title: 'Category' },
    { field: 'color', sortable: true, title: 'Color' },
    { field: 'productName', sortable: true, title: 'Product Name' },
    { field: 'price', sortable: true, title: 'Price' },
    { field: 'releaseDate', formatter: 'formatDateTime', title: 'DateTime' },
  ],
  exportConfig: {},
  height: 'auto',
  keepSource: true,
  proxyConfig: {
    ajax: {
      query: async ({ page, sort }) => {
        return await getExampleTableApi({
          page: page.currentPage,
          pageSize: page.pageSize,
          sortBy: sort.field,
          sortOrder: sort.order,
        })
      },
    },
    sort: true,
  },
  sortConfig: {
    defaultSort: { field: 'category', order: 'desc' },
    remote: true,
  },
  toolbarConfig: {
    custom: true,
    export: true,
    // import: true,
    refresh: true,
    zoom: true,
  },
}

const [Grid, gridApi] = useVbenVxeGrid({
  gridOptions,
})
</script>

<template>
  <Page auto-content-height>
    <Grid
      table-title="数据列表"
      table-title-help="提示"
    >
      <template #toolbar-tools>
        <ElButton
          class="mr-2"
          type="primary"
          @click="() => gridApi.query()"
        >
          刷新当前页面
        </ElButton>
        <ElButton
          type="primary"
          @click="() => gridApi.reload()"
        >
          刷新并返回第一页
        </ElButton>
      </template>
    </Grid>
  </Page>
</template>
