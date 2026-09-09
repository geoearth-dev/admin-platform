<script lang="ts" setup>
import { useQuery, useQueryClient } from '@tanstack/vue-query'

import { listMenu } from '@/api/system/admin/menu'
import { useVbenForm } from '@/plugins/vben-ui/form-ui'

const queryOptions = {
  queryKey: ['demo', 'api', 'options'],
  queryFn: () => listMenu({}),
  // 五分钟内复用新鲜数据。
  staleTime: 5 * 60 * 1000,
  retry: false,
}
const count = 4
const queryClient = useQueryClient()

const { dataUpdatedAt } = useQuery({
  ...queryOptions,
  enabled: false,
})

async function fetchOptions() {
  return queryClient.query(queryOptions)
}

const schema = []

for (let i = 0; i < count; i++) {
  schema.push({
    component: 'ApiSelect',
    componentProps: {
      api: fetchOptions,
      labelField: 'menuName',
      valueField: 'id',
      filterable: true,
      clearable: true,
      class: 'w-full',
      placeholder: '请选择菜单',
    },
    fieldName: `field${i}`,
    label: `Select ${i + 1}`,
  })
}

const [Form] = useVbenForm({
  schema,
  showDefaultActions: false,
})
</script>
<template>
  <div>
    <div class="mb-2 flex gap-2">
      <div>以下{{ count }}个组件共用一个数据源。</div>
      <div>缓存更新时间：{{ new Date(dataUpdatedAt).toLocaleString() }}</div>
    </div>
    <Form />
  </div>
</template>
