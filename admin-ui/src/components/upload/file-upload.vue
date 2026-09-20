<script setup lang="ts">
import type {
  UploadFile,
  UploadInstance,
  UploadRawFile,
  UploadRequestOptions,
  UploadUserFile,
} from 'element-plus'
import type { FileUploadProps } from './types'
import { computed, onBeforeUnmount, ref, watch } from 'vue'
import { ElMessage, genFileId, useFormItem } from 'element-plus'
import { resolveFileUrl, uploadFile } from '@/api/common/file'

const props = withDefaults(defineProps<FileUploadProps>(), {
  mode: 'file',
  limit: 5,
  maxSize: 10,
  disabled: false,
  drag: false,
})
/** Comma-separated server paths, matching existing String business columns. */
const model = defineModel<string | null>()
const emit = defineEmits<{ uploading: [value: boolean] }>()
const { form, formItem } = useFormItem()
const uploader = ref<UploadInstance>()
const files = ref<UploadUserFile[]>([])
const pending = new Map<number, AbortController>()
const paths = new Map<number, string>()
const failed = ref<UploadRawFile[]>([])
const previewUrl = ref('')
const previewOpen = ref(false)
const disabled = computed(() => props.disabled || form?.disabled === true)
const accept = computed(
  () =>
    props.accept ??
    (props.mode === 'image'
      ? '.jpg,.jpeg,.png,.gif,.bmp'
      : '.doc,.docx,.xls,.xlsx,.ppt,.pptx,.txt,.pdf,.zip,.rar,.gz,.bz2,.jpg,.jpeg,.png,.gif,.bmp,.mp4,.avi,.rmvb'),
)
let lastEmitted: string | undefined
let disposed = false

function cancelAll(): void {
  for (const controller of pending.values()) controller.abort()
  pending.clear()
  uploader.value?.abort()
  emit('uploading', false)
}
watch(
  model,
  (value) => {
    if ((value ?? '') === lastEmitted) return
    cancelAll()
    paths.clear()
    failed.value = []
    files.value = (value ?? '')
      .split(',')
      .map((value) => value.trim())
      .filter(Boolean)
      .map((path) => {
        const uid = genFileId()
        paths.set(uid, path)
        return {
          uid,
          name: path.split('/').pop() || path,
          url: resolveFileUrl(path),
          status: 'success',
        }
      })
  },
  { immediate: true },
)
function publish(): void {
  lastEmitted = files.value
    .flatMap((file) => {
      const path = file.uid === undefined ? undefined : paths.get(file.uid)
      return path ? [path] : []
    })
    .join(',')
  model.value = lastEmitted
  void formItem?.validate('change').catch(() => undefined)
}
function beforeUpload(file: UploadRawFile): boolean {
  const extension = `.${file.name.split('.').pop()?.toLowerCase()}`
  const allowed = accept.value
    .split(',')
    .map((value) => value.trim().toLowerCase())
    .filter(Boolean)
  const matches = allowed.some((value) =>
    value.startsWith('.')
      ? extension === value
      : value.endsWith('/*')
        ? file.type.startsWith(value.slice(0, -1))
        : file.type === value,
  )
  if (allowed.length && !matches) {
    ElMessage.warning('文件格式不符合要求')
    return false
  }
  if (!file.size || file.size > props.maxSize * 1024 * 1024) {
    ElMessage.warning(`文件不能为空且不能超过 ${props.maxSize} MB`)
    return false
  }
  return true
}
async function request(options: UploadRequestOptions): Promise<unknown> {
  const controller = new AbortController()
  pending.set(options.file.uid, controller)
  emit('uploading', true)
  try {
    const result = await (props.upload ?? uploadFile)(options.file, {
      signal: controller.signal,
      onProgress: (percent) =>
        options.onProgress(
          Object.assign(new ProgressEvent('progress'), { percent }),
        ),
    })
    if (disposed || controller.signal.aborted) throw new Error('上传已取消')
    const file = files.value.find((file) => file.uid === options.file.uid)
    if (file) {
      if (!result.fileName || result.fileName.includes(','))
        throw new Error('服务器返回了无效文件路径')
      paths.set(options.file.uid, result.fileName)
      if (file.url?.startsWith('blob:')) URL.revokeObjectURL(file.url)
      file.url = resolveFileUrl(result.fileName)
      file.name = result.originalFilename
      publish()
    }
    return result
  } catch (error) {
    if (!disposed && !controller.signal.aborted) failed.value.push(options.file)
    throw error
  } finally {
    pending.delete(options.file.uid)
    if (!disposed) emit('uploading', pending.size > 0)
  }
}
function remove(file: UploadFile, remaining: UploadFile[]): void {
  files.value = remaining
  pending.get(file.uid)?.abort()
  pending.delete(file.uid)
  paths.delete(file.uid)
  emit('uploading', pending.size > 0)
  publish()
}
function preview(file: UploadFile): void {
  if (!file.url) return
  if (props.mode === 'image') {
    previewUrl.value = file.url
    previewOpen.value = true
  } else window.open(file.url, '_blank', 'noopener,noreferrer')
}
function retry(file: UploadRawFile): void {
  if (files.value.length >= props.limit) {
    ElMessage.warning('文件数量已达上限')
    return
  }
  failed.value = failed.value.filter((item) => item.uid !== file.uid)
  file.uid = genFileId()
  uploader.value?.handleStart(file)
  uploader.value?.submit()
}
onBeforeUnmount(() => {
  disposed = true
  cancelAll()
})
defineExpose({ cancelAll })
</script>

<template>
  <div class="w-full">
    <el-upload
      ref="uploader"
      v-model:file-list="files"
      :http-request="request"
      :before-upload="beforeUpload"
      :on-remove="remove"
      :on-preview="preview"
      :on-exceed="() => ElMessage.warning(`最多上传 ${limit} 个文件`)"
      :list-type="mode === 'image' ? 'picture-card' : 'text'"
      :accept="accept"
      :limit="limit"
      :multiple="limit > 1"
      :disabled="disabled"
      :drag="drag"
    >
      <span v-if="mode === 'image'">＋ 上传图片</span>
      <el-button
        v-else
        :disabled="disabled"
        type="primary"
        plain
        >选择文件</el-button
      >
      <template #tip
        ><div class="el-upload__tip">
          最多 {{ limit }} 个，每个不超过 {{ maxSize }} MB；支持 {{ accept }}
        </div></template
      >
    </el-upload>
    <div
      v-for="file in failed"
      :key="file.uid"
      class="mt-2 flex items-center gap-2 text-sm"
    >
      <span class="text-danger">{{ file.name }} 上传失败</span>
      <el-button
        link
        type="primary"
        :disabled="disabled"
        @click="retry(file)"
        >重试</el-button
      >
      <el-button
        link
        :disabled="disabled"
        @click="failed = failed.filter((item) => item.uid !== file.uid)"
        >移除</el-button
      >
    </div>
    <el-dialog
      v-model="previewOpen"
      title="图片预览"
      append-to-body
      width="min(800px, 90vw)"
    >
      <img
        :src="previewUrl"
        alt="图片预览"
        class="mx-auto max-h-[70vh] max-w-full"
      />
    </el-dialog>
  </div>
</template>
