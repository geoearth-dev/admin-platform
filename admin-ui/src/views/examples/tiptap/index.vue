<script lang="ts" setup>
import type { ImageUploadOptions } from '@/components/tiptap'

import { computed, ref } from 'vue'

import { Page } from '@/components/page'
import { VbenTiptap, VbenTiptapPreview } from '@/components/tiptap'

import { ElCard, ElSwitch } from 'element-plus'
import { uploadExampleFileApi } from '@/api/example/mock-upload'
const content = ref(`
  <h1>Vben Tiptap</h1>
  <p>这个编辑器已经被封装在 <code>src/components/tiptap</code> 中。</p>
  <p>你可以直接在各个 app 里通过 <code>@/components/tiptap</code> 引入。</p>
  <blockquote>默认内置 StarterKit、Underline、TextAlign、Placeholder。</blockquote>
`)
const previewContent = computed(() => content.value)

const enableUpload = ref(true)

// 本地示例上传，回显实际选中的图片。
const imageUpload: ImageUploadOptions = {
  accept: 'image/*',
  maxSize: 5 * 1024 * 1024, // 5MB
  upload: async (file, onProgress) => {
    const result = await uploadExampleFileApi(file, undefined, onProgress)
    return result.url
  },
  onUploadError: (error: unknown) => {
    console.error('Image upload failed:', error)
  },
}
</script>

<template>
  <Page title="Tiptap 富文本">
    <template #description>
      <div class="mt-2 text-foreground/80">
        统一封装后的富文本编辑器，适合在各个 app 中直接复用。
      </div>
    </template>

    <ElCard
      class="mb-5"
      header="编辑器"
    >
      <div class="mb-3 flex items-center gap-3">
        <span class="text-sm">启用图片上传：</span>
        <ElSwitch v-model="enableUpload" />
      </div>
      <VbenTiptap
        v-model="content"
        :image-upload="enableUpload ? imageUpload : undefined"
      />
    </ElCard>

    <ElCard
      class="mb-5"
      header="富文本预览"
    >
      <VbenTiptapPreview :content="previewContent" />
    </ElCard>

    <ElCard header="HTML 输出">
      <pre class="overflow-auto rounded-xl border border-border bg-muted p-4">
        {{ previewContent }}
      </pre>
    </ElCard>
  </Page>
</template>
