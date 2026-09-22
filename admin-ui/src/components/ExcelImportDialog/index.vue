 <template>
  <Modal
    :title="title || $t('ui.excelImport.title')"
    :confirm-text="$t('ui.excelImport.submit')"
    class="w-[500px] max-w-[95vw]"
  >
    <div class="space-y-4">
      <ElUpload
        ref="uploader"
        class="excel-import-upload"
        drag
        accept=".xls,.xlsx"
        :auto-upload="false"
        :limit="1"
        :disabled="importing"
        :on-change="onFileChange"
        :on-remove="onFileRemove"
        :on-exceed="onFileExceed"
      >
        <CloudUpload
          class="text-muted-foreground/50 mx-auto mb-4 size-16"
          :stroke-width="1.5"
        />
        <div class="text-muted-foreground text-sm">
          {{ $t('ui.excelImport.dropFile') }}
          <span class="text-primary">{{
            $t('ui.excelImport.clickUpload')
          }}</span>
        </div>
      </ElUpload>

      <div class="space-y-1 text-center">
        <ElCheckbox
          v-if="showUpdateSupport"
          v-model="updateSupport"
          :disabled="importing"
        >
          {{ updateSupportLabel || $t('ui.excelImport.updateSupport') }}
        </ElCheckbox>
        <div
          class="text-muted-foreground flex flex-wrap items-center justify-center gap-x-2 text-xs"
        >
          <span>{{ $t('ui.excelImport.excelOnly') }}</span>
          <VbenButton
            v-if="downloadTemplate"
            variant="link"
            class="h-auto p-0 text-xs"
            :loading="downloading"
            @click="onDownloadTemplate"
          >
            {{ $t('ui.excelImport.downloadTemplate') }}
          </VbenButton>
        </div>
      </div>

      <div
        v-if="resultMessage"
        class="max-h-64 overflow-auto whitespace-pre-wrap wrap-break-word text-sm"
        :class="failed ? 'text-destructive' : 'text-foreground'"
      >
        {{ resultMessage }}
      </div>

      <p
        v-if="failed"
        class="text-sm text-muted-foreground"
      >
        {{ $t('ui.excelImport.partialHint') }}
      </p>
    </div>
  </Modal>
</template>
<script setup lang="ts">
import type { UploadFile, UploadInstance } from 'element-plus'

import { ref } from 'vue'
import { ElCheckbox, ElMessage, ElUpload } from 'element-plus'

import { CloudUpload } from '@/assets/icons'
import { $t } from '@/plugins/locale'
import { useVbenModal } from '@/plugins/vben-ui/popup-ui'
import { VbenButton } from '@/plugins/vben-ui/shadcn-ui'

defineOptions({ name: 'ExcelImportDialog' })

const props = withDefaults(
  defineProps<{
    title?: string
    importFile: (file: File, updateSupport: boolean) => Promise<string | void>
    downloadTemplate?: () => Promise<void>
    showUpdateSupport?: boolean
    updateSupportLabel?: string
  }>(),
  {
    title: '',
    showUpdateSupport: true,
    updateSupportLabel: '',
  },
)

const emit = defineEmits<{
  success: [message: string]
  changed: []
}>()

const uploader = ref<UploadInstance>()
const file = ref<File>()
const updateSupport = ref(false)
const importing = ref(false)
const downloading = ref(false)
const resultMessage = ref('')
const failed = ref(false)

const [Modal, modalApi] = useVbenModal({
  fullscreenButton: false,

  onOpenChange(open) {
    if (!open) return

    file.value = undefined
    updateSupport.value = false
    resultMessage.value = ''
    failed.value = false

    uploader.value?.clearFiles()
  },

  async onConfirm() {
    if (importing.value) return

    if (!file.value) {
      ElMessage.warning($t('ui.excelImport.selectFileRequired'))
      return
    }

    importing.value = true
    failed.value = false
    resultMessage.value = ''
    modalApi.lock()

    try {
      const message = await props.importFile(
        file.value,
        props.showUpdateSupport && updateSupport.value,
      )

      // 后台返回的是带 <br/> 的字符串，转换成纯文本换行。
      resultMessage.value = (message || $t('ui.excelImport.success')).replace(
        /<br\s*\/?>/gi,
        '\n',
      )
      ElMessage.success($t('ui.excelImport.success'))

      file.value = undefined
      uploader.value?.clearFiles()
      emit('success', resultMessage.value)
    } catch (error) {
      failed.value = true
      resultMessage.value = (
        error instanceof Error ? error.message : $t('ui.excelImport.failed')
      ).replace(/<br\s*\/?>/gi, '\n')
    } finally {
      importing.value = false
      modalApi.unlock()

      // 后台可能已成功导入部分记录，失败也需要刷新。
      emit('changed')
    }
  },
})

function onFileChange(uploadFile: UploadFile) {
  const selected = uploadFile.raw

  file.value = undefined
  if (!selected) return

  if (!/\.(xlsx|xls)$/i.test(selected.name)) {
    uploader.value?.clearFiles()
    ElMessage.warning($t('ui.excelImport.excelOnly'))
    return
  }

  file.value = selected
  resultMessage.value = ''
  failed.value = false
}

function onFileRemove() {
  file.value = undefined
  resultMessage.value = ''
  failed.value = false
}

function onFileExceed() {
  ElMessage.warning($t('ui.excelImport.fileLimit'))
}

async function onDownloadTemplate() {
  if (downloading.value || !props.downloadTemplate) return

  downloading.value = true
  try {
    await props.downloadTemplate()
  } catch {
    // 请求层已经提示。
  } finally {
    downloading.value = false
  }
}
defineExpose({ open: () => modalApi.open(), close: () => modalApi.close() })
</script>

<style scoped>
.excel-import-upload :deep(.el-upload) {
  display: block;
}

.excel-import-upload :deep(.el-upload-dragger) {
  padding: 32px 16px;
}
</style>
