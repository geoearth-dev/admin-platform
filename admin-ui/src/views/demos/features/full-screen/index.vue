<script lang="ts" setup>
import { ref } from 'vue'

import { Page } from '@/components/page'

import { useFullscreen } from '@vueuse/core'
import {
  Button,
  Card,
  CardHeader,
  CardTitle,
  CardContent,
} from '@/plugins/vben-ui/shadcn-ui'

const domRef = ref<HTMLElement>()

const { enter, exit, isFullscreen, toggle } = useFullscreen()

const { isFullscreen: isDomFullscreen, toggle: toggleDom } =
  useFullscreen(domRef)
</script>

<template>
  <Page title="全屏示例">
    <Card>
      <CardHeader><CardTitle>Window Full Screen</CardTitle></CardHeader>
      <CardContent>
        <div class="flex flex-wrap items-center gap-4">
          <Button
            :disabled="isFullscreen"
            variant="default"
            @click="enter"
          >
            Enter Window Full Screen
          </Button>
          <Button @click="toggle"> Toggle Window Full Screen </Button>

          <Button
            :disabled="!isFullscreen"
            variant="destructive"
            @click="exit"
          >
            Exit Window Full Screen
          </Button>

          <span class="text-nowrap"> Current State: {{ isFullscreen }} </span>
        </div>
      </CardContent>
    </Card>

    <Card class="mt-5">
      <CardHeader><CardTitle>Dom Full Screen</CardTitle></CardHeader>
      <CardContent>
        <Button
          variant="default"
          @click="toggleDom"
        >
          Enter Dom Full Screen
        </Button>
      </CardContent>
    </Card>

    <div
      ref="domRef"
      class="mx-auto mt-10 flex-center h-64 w-1/2 rounded-md bg-yellow-400"
    >
      <Button
        class="mr-2"
        variant="default"
        @click="toggleDom"
      >
        {{ isDomFullscreen ? 'Exit Dom Full Screen' : 'Enter Dom Full Screen' }}
      </Button>
    </div>
  </Page>
</template>
