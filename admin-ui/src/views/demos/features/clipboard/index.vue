<script setup lang="ts">
import { ref } from 'vue'

import { Page } from '@/components/page'

import { useClipboard } from '@vueuse/core'
import {
  Button,
  Card,
  Input,
  CardHeader,
  CardTitle,
  CardContent,
} from '@/plugins/vben-ui/shadcn-ui'

const source = ref('Hello')
const { copy, text } = useClipboard({ legacy: true, source })
</script>

<template>
  <Page title="剪切板示例">
    <Card>
      <CardHeader><CardTitle>基本使用</CardTitle></CardHeader>
      <CardContent>
        <p class="mb-3">
          Current copied: <code>{{ text || 'none' }}</code>
        </p>
        <div class="flex">
          <Input
            v-model="source"
            class="mr-3 flex w-50"
          />
          <Button
            variant="default"
            @click="copy(source)"
          >
            Copy
          </Button>
        </div>
      </CardContent>
    </Card>
  </Page>
</template>
