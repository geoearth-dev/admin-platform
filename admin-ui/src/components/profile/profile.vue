<script setup lang="ts">
import type { Props } from './types'

import {
  Card,
  Separator,
  Tabs,
  TabsList,
  TabsTrigger,
  VbenAvatar,
} from '@/plugins/vben-ui/shadcn-ui'

import { Page } from '../page'
import { preferences } from '@/plugins/preference'

defineOptions({
  name: 'ProfileUI',
})

withDefaults(defineProps<Props>(), {
  title: '个人中心',
  tabs: () => [],
})

const tabsValue = defineModel<string>('modelValue')
</script>
<template>
  <Page
    :title="title"
    auto-content-height
  >
    <div class="flex w-full flex-col gap-4 md:flex-row">
      <Card class="w-full flex-none md:w-56">
        <div class="flex-col-center gap-3 px-4 pt-6">
          <slot name="avatar">
            <VbenAvatar
              :src="userInfo?.avatar || preferences.app.defaultAvatar"
              class="size-20"
            />
          </slot>
          <span class="text-lg font-semibold">
            {{ userInfo?.nickName || userInfo?.userName || '' }}
          </span>
          <span class="text-sm text-foreground/80">
            {{ userInfo?.userName ?? '' }}
          </span>
        </div>
        <Separator class="my-4" />
        <Tabs
          v-model="tabsValue"
          orientation="vertical"
          class="m-4"
        >
          <TabsList
            class="grid h-auto w-full grid-cols-2 bg-card md:grid-cols-1"
          >
            <TabsTrigger
              v-for="tab in tabs"
              :key="tab.value"
              :value="tab.value"
              class="h-12 justify-start data-[state=active]:bg-primary data-[state=active]:text-primary-foreground"
            >
              {{ tab.label }}
            </TabsTrigger>
          </TabsList>
        </Tabs>
      </Card>
      <Card class="min-w-0 flex-1 p-4 sm:p-8">
        <slot name="content"></slot>
      </Card>
    </div>
  </Page>
</template>
