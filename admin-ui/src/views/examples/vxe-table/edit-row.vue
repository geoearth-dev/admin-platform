<script lang="ts" setup>
import type { VxeGridProps } from '@/components/vxe-table'

import { Page } from '@/components/page'

import { ElButton, ElMessage } from 'element-plus'

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
  columns: [
    { title: '序号', type: 'seq', width: 50 },
    { editRender: { name: 'input' }, field: 'category', title: 'Category' },
    { editRender: { name: 'input' }, field: 'color', title: 'Color' },
    {
      editRender: { name: 'input' },
      field: 'productName',
      title: 'Product Name',
    },
    { field: 'price', title: 'Price' },
    { field: 'releaseDate', formatter: 'formatDateTime', title: 'Date' },
    { slots: { default: 'action' }, title: '操作' },
  ],
  editConfig: {
    mode: 'row',
    trigger: 'click',
  },
  height: 'auto',
  pagerConfig: {},
  proxyConfig: {
    ajax: {
      query: async ({ page }) => {
        return await getExampleTableApi({
          page: page.currentPage,
          pageSize: page.pageSize,
        })
      },
    },
  },
  showOverflow: true,
}

const [Grid, gridApi] = useVbenVxeGrid({ gridOptions })

function hasEditStatus(row: RowType) {
  return gridApi.grid?.isEditByRow(row)
}

function editRowEvent(row: RowType) {
  gridApi.grid?.setEditRow(row)
}

async function saveRowEvent(row: RowType) {
  await gridApi.grid?.clearEdit()

  gridApi.setLoading(true)
  setTimeout(() => {
    gridApi.setLoading(false)
    ElMessage.success({
      message: `保存成功！category=${row.category}`,
    })
  }, 600)
}

const cancelRowEvent = () => {
  gridApi.grid?.clearEdit()
}
</script>

<template>
  <Page auto-content-height>
    <Grid>
      <template #action="{ row }">
        <template v-if="hasEditStatus(row)">
          <ElButton
            link
            type="primary"
            @click="saveRowEvent(row)"
            >保存</ElButton
          >
          <ElButton
            link
            type="primary"
            @click="cancelRowEvent()"
            >取消</ElButton
          >
        </template>
        <template v-else>
          <ElButton
            link
            type="primary"
            @click="editRowEvent(row)"
            >编辑</ElButton
          >
        </template>
      </template>
    </Grid>
  </Page>
</template>
