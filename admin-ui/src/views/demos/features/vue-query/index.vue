<script setup lang="ts">
import { Page } from '@/components/page'

import { refAutoReset } from '@vueuse/core'
import {
  Button,
  Card,
  CardHeader,
  CardTitle,
  CardContent,
} from '@/plugins/vben-ui/shadcn-ui'
import { ElEmpty as Empty } from 'element-plus'
import ConcurrencyCaching from './concurrency-caching.vue'
import InfiniteQueries from './infinite-queries.vue'
import PaginatedQueries from './paginated-queries.vue'
import QueryRetries from './query-retries.vue'

const showCaching = refAutoReset(true, 1000)
</script>

<template>
  <Page title="Vue Query示例">
    <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
      <Card>
        <CardHeader><CardTitle>分页查询</CardTitle></CardHeader>
        <CardContent>
          <PaginatedQueries />
        </CardContent>
      </Card>
      <Card>
        <CardHeader><CardTitle>无限滚动</CardTitle></CardHeader>
        <CardContent>
          <InfiniteQueries class="h-75 overflow-auto" />
        </CardContent>
      </Card>
      <Card>
        <CardHeader><CardTitle>错误重试</CardTitle></CardHeader>
        <CardContent>
          <QueryRetries />
        </CardContent>
      </Card>
      <Card v-loading="!showCaching">
        <CardHeader class="flex flex-row items-center justify-between">
          <CardTitle>并发和缓存</CardTitle>
          <Button
            :disabled="!showCaching"
            @click="showCaching = false"
            >重新加载</Button
          >
        </CardHeader>
        <CardContent class="min-h-[330px]">
          <ConcurrencyCaching v-if="showCaching" />
          <Empty
            v-else
            description="正在加载..."
          />
        </CardContent>
      </Card>
    </div>
  </Page>
</template>
