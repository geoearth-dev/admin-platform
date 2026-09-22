<script lang="ts" setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

import { Page } from '@/components/page'

import {
  Button,
  Card,
  Input,
  CardHeader,
  CardTitle,
  CardContent,
} from '@/plugins/vben-ui/shadcn-ui'
import { useTabs } from '@/plugins/effects/hooks'

const router = useRouter()
const newTabTitle = ref('')

const {
  closeAllTabs,
  closeCurrentTab,
  closeLeftTabs,
  closeOtherTabs,
  closeRightTabs,
  closeTabByKey,
  refreshTab,
  resetTabTitle,
  setTabTitle,
} = useTabs()

function openTab() {
  // 这里就是路由跳转，也可以用path
  router.push({ name: 'About' })
}

function openTabWithParams(id: number) {
  // 这里就是路由跳转，也可以用path
  router.push({ name: 'FeatureTabDetailDemo', params: { id } })
}

function reset() {
  newTabTitle.value = ''
  resetTabTitle()
}
</script>

<template>
  <Page
    description="用于需要操作标签页的场景"
    title="标签页"
  >
    <Card class="mb-5">
      <CardHeader><CardTitle>打开/关闭标签页</CardTitle></CardHeader>
      <CardContent>
        <div class="mb-3 text-foreground/80">
          如果标签页存在，直接跳转切换。如果标签页不存在，则打开新的标签页。
        </div>
        <div class="flex flex-wrap gap-3">
          <Button
            variant="default"
            @click="openTab"
          >
            打开 "关于" 标签页
          </Button>
          <Button
            variant="default"
            @click="closeTabByKey('/admin/about')"
          >
            关闭 "关于" 标签页
          </Button>
        </div>
      </CardContent>
    </Card>

    <Card class="mb-5">
      <CardHeader><CardTitle>标签页操作</CardTitle></CardHeader>
      <CardContent>
        <div class="mb-3 text-foreground/80">用于动态控制标签页的各种操作</div>
        <div class="flex flex-wrap gap-3">
          <Button
            variant="default"
            @click="closeCurrentTab()"
          >
            关闭当前标签页
          </Button>
          <Button
            variant="default"
            @click="closeLeftTabs()"
          >
            关闭左侧标签页
          </Button>
          <Button
            variant="default"
            @click="closeRightTabs()"
          >
            关闭右侧标签页
          </Button>
          <Button
            variant="default"
            @click="closeAllTabs()"
          >
            关闭所有标签页
          </Button>
          <Button
            variant="default"
            @click="closeOtherTabs()"
          >
            关闭其他标签页
          </Button>
          <Button
            variant="default"
            @click="refreshTab()"
          >
            刷新当前标签页
          </Button>
        </div>
      </CardContent>
    </Card>

    <Card class="mb-5">
      <CardHeader><CardTitle>动态标题</CardTitle></CardHeader>
      <CardContent>
        <div class="mb-3 text-foreground/80">
          该操作不会影响页面标题，仅修改Tab标题
        </div>
        <div class="flex flex-wrap items-center gap-3">
          <Input
            v-model="newTabTitle"
            class="w-40"
            placeholder="请输入新标题"
          />
          <Button
            variant="default"
            @click="() => setTabTitle(newTabTitle)"
          >
            修改
          </Button>
          <Button @click="reset"> 重置 </Button>
        </div>
      </CardContent>
    </Card>

    <Card class="mb-5">
      <CardHeader><CardTitle>最大打开数量</CardTitle></CardHeader>
      <CardContent>
        <div class="mb-3 text-foreground/80">
          限制带参数的tab打开的最大数量，由 `route.meta.maxNumOfOpenTab` 控制
        </div>
        <div class="flex flex-wrap items-center gap-3">
          <template
            v-for="item in 5"
            :key="item"
          >
            <Button
              variant="default"
              @click="openTabWithParams(item)"
            >
              打开{{ item }}详情页
            </Button>
          </template>
        </div>
      </CardContent>
    </Card>
  </Page>
</template>
