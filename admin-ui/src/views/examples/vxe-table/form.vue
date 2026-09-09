<script lang="ts" setup>
import type {
  VbenFormProps,
  BaseFormComponentType,
  ElementPlusComponentProps,
} from '@/plugins/vben-ui/form-ui'
import type { VxeTableGridOptions } from '@/components/vxe-table'

import { Page } from '@/components/page'

import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'

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

interface SearchFormValues extends Record<string, unknown> {
  category?: string
  color?: string
  date?: [Date, Date] | null
  price?: string
  productName?: string
}

interface SearchSubmitValues extends Record<string, unknown> {
  category?: string
  color?: string
  end?: string
  price?: string
  productName?: string
  start?: string
}

// Element Plus 日期组件使用 Date，提交时拆成后端的 start/end 日期字符串。
const searchCodec = {
  encode(values: Readonly<SearchFormValues>): SearchSubmitValues {
    const { date, ...rest } = values
    return {
      ...rest,
      start: date?.[0] ? dayjs(date[0]).format('YYYY-MM-DD') : undefined,
      end: date?.[1] ? dayjs(date[1]).format('YYYY-MM-DD') : undefined,
    }
  },
  decode(values: Readonly<SearchSubmitValues>): SearchFormValues {
    const { start, end, ...rest } = values
    return {
      ...rest,
      date:
        start && end ? [dayjs(start).toDate(), dayjs(end).toDate()] : undefined,
    }
  },
}

const formOptions: VbenFormProps<
  BaseFormComponentType,
  ElementPlusComponentProps,
  SearchFormValues,
  SearchSubmitValues
> = {
  codec: searchCodec,
  // 默认展开
  collapsed: false,
  schema: [
    {
      component: 'Input',
      defaultValue: '1',
      fieldName: 'category',
      label: 'Category',
    },
    {
      component: 'Input',
      fieldName: 'productName',
      label: 'ProductName',
    },
    {
      component: 'Input',
      fieldName: 'price',
      label: 'Price',
    },
    {
      component: 'Select',
      componentProps: {
        clearable: true,
        options: [
          {
            label: 'Color1',
            value: '1',
          },
          {
            label: 'Color2',
            value: '2',
          },
        ],
        placeholder: '请选择',
      },
      fieldName: 'color',
      label: 'Color',
    },
    {
      component: 'RangePicker',
      defaultValue: [dayjs().subtract(7, 'days').toDate(), dayjs().toDate()],
      fieldName: 'date',
      label: 'Date',
    },
  ],
  // 控制表单是否显示折叠按钮
  showCollapseButton: true,
  // 是否在字段值改变时提交表单
  submitOnChange: true,
  // 按下回车时是否提交表单
  submitOnEnter: false,
}

const gridOptions: VxeTableGridOptions<RowType> = {
  checkboxConfig: {
    highlight: true,
    labelField: 'name',
  },
  columns: [
    { title: '序号', type: 'seq', width: 50 },
    { align: 'left', title: 'Name', type: 'checkbox', width: 100 },
    { field: 'category', title: 'Category' },
    { field: 'color', title: 'Color' },
    { field: 'productName', title: 'Product Name' },
    { field: 'price', title: 'Price' },
    { field: 'releaseDate', formatter: 'formatDateTime', title: 'Date' },
  ],
  exportConfig: {},
  height: 'auto',
  keepSource: true,
  pagerConfig: {},
  proxyConfig: {
    ajax: {
      query: async ({ page }, formValues: SearchSubmitValues) => {
        ElMessage.success(`Query params: ${JSON.stringify(formValues)}`)
        return await getExampleTableApi({
          page: page.currentPage,
          pageSize: page.pageSize,
          ...formValues,
        })
      },
    },
  },
  toolbarConfig: {
    custom: true,
    export: true,
    refresh: true,
    resizable: true,
    search: true,
    zoom: true,
  },
}

const [Grid] = useVbenVxeGrid({
  formOptions,
  gridOptions,
})
</script>

<template>
  <Page auto-content-height>
    <Grid />
  </Page>
</template>
