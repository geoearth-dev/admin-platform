<template>
  <Modal
    :title="$t('system.user.importTitle')"
    :confirm-text="$t('system.user.importSubmit')"
    class="w-[600px] max-w-[95vw]"
  >
    <div class="space-y-4">
      <input ref="inputRef" type="file" accept=".xls,.xlsx" class="hidden" @change="onFileChange" />

      <div class="flex items-center gap-3">
        <VbenButton variant="outline" @click="inputRef?.click()">
          {{ $t('system.user.selectFile') }}
        </VbenButton>

        <span class="truncate text-sm">{{ file?.name }}</span>
      </div>

      <label class="flex items-center gap-2 text-sm">
        <input v-model="updateSupport" type="checkbox" />
        {{ $t('system.user.updateSupport') }}
      </label>

      <VbenButton
        variant="link"
        class="h-auto p-0"
        :loading="downloading"
        @click="onDownloadTemplate"
      >
        {{ $t('system.user.downloadTemplate') }}
      </VbenButton>

      <div
        v-if="resultMessage"
        class="max-h-64 overflow-auto whitespace-pre-wrap wrap-break-word text-sm"
        :class="failed ? 'text-destructive' : 'text-foreground'"
      >
        {{ resultMessage }}
      </div>

      <p v-if="failed" class="text-sm text-muted-foreground">
        {{ $t('system.user.importPartialHint') }}
      </p>
    </div>
  </Modal>
</template>
<script setup lang="ts">
import { ref } from 'vue';
import { ElMessage } from 'element-plus';

import { downloadUserTemplate, importUser } from '@/api/system/user';
import { $t } from '@/plugins/locale';
import { useVbenModal } from '@/plugins/vben-ui/popup-ui';
import { VbenButton } from '@/plugins/vben-ui/shadcn-ui';

import { saveExcel } from './download';

const emit = defineEmits<{ changed: [] }>();

const inputRef = ref<HTMLInputElement>();
const file = ref<File>();
const updateSupport = ref(false);
const importing = ref(false);
const downloading = ref(false);
const resultMessage = ref('');
const failed = ref(false);

const [Modal, modalApi] = useVbenModal({
  fullscreenButton: false,

  onOpenChange(open) {
    if (!open) return;

    file.value = undefined;
    updateSupport.value = false;
    resultMessage.value = '';
    failed.value = false;

    if (inputRef.value) {
      inputRef.value.value = '';
    }
  },

  async onConfirm() {
    if (importing.value) return;

    if (!file.value) {
      ElMessage.warning($t('system.user.selectFileRequired'));
      return;
    }

    importing.value = true;
    failed.value = false;
    resultMessage.value = '';
    modalApi.lock();

    try {
      const message = await importUser(file.value, updateSupport.value);

      // 后台返回的是带 <br/> 的字符串，转换成纯文本换行。
      resultMessage.value = message.replace(/<br\s*\/?>/gi, '\n');
      ElMessage.success($t('system.user.importSuccess'));

      file.value = undefined;
      if (inputRef.value) {
        inputRef.value.value = '';
      }
    } catch (error) {
      failed.value = true;
      resultMessage.value = (
        error instanceof Error ? error.message : $t('system.user.importFailed')
      ).replace(/<br\s*\/?>/gi, '\n');
    } finally {
      importing.value = false;
      modalApi.unlock();

      // 后台可能已成功导入部分记录，失败也需要刷新。
      emit('changed');
    }
  },
});

function onFileChange(event: Event) {
  const input = event.target as HTMLInputElement;
  const selected = input.files?.[0];

  file.value = undefined;
  if (!selected) return;

  if (!/\.(xlsx|xls)$/i.test(selected.name)) {
    input.value = '';
    ElMessage.warning($t('system.user.excelOnly'));
    return;
  }

  file.value = selected;
  resultMessage.value = '';
  failed.value = false;
}

async function onDownloadTemplate() {
  if (downloading.value) return;

  downloading.value = true;
  try {
    const blob = await downloadUserTemplate();
    await saveExcel(blob, `${$t('system.user.downloadTemplate')}.xlsx`);
  } catch {
    // 请求层已经提示。
  } finally {
    downloading.value = false;
  }
}
</script>
