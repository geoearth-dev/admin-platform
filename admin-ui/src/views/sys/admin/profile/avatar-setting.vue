<script setup lang="ts">
import type { UserProfileResult } from '@/types/base/api/system/user';
import { computed, onBeforeUnmount, ref } from 'vue';
import { useWindowSize } from '@vueuse/core';
import { ElButton, ElDialog, ElMessage } from 'element-plus';
import { uploadUserAvatar } from '@/api/system/user';
import { resolveFileUrl } from '@/api/common/file';
import { VCropper } from '@/components/cropper';
import { VbenAvatar } from '@/plugins/vben-ui/shadcn-ui';
import { preferences } from '@/plugins/preference';

const props = defineProps<{ avatar: string | null }>();
const emit = defineEmits<{ updated: [profile: UserProfileResult] }>();
const fileInput = ref<HTMLInputElement>();
const cropper = ref<InstanceType<typeof VCropper>>();
const visible = ref(false);
const saving = ref(false);
const preview = ref('');
const { width } = useWindowSize();
const cropWidth = computed(() => Math.max(180, Math.min(420, width.value - 88)));
const avatarUrl = computed(() => resolveFileUrl(props.avatar ?? '') || preferences.app.defaultAvatar);

function releasePreview() {
  if (preview.value) URL.revokeObjectURL(preview.value);
  preview.value = '';
}

async function selectFile(event: Event) {
  const input = event.target as HTMLInputElement;
  const file = input.files?.[0];
  input.value = '';
  if (!file) return;
  if (!['image/jpeg', 'image/png'].includes(file.type) || file.size === 0 || file.size > 2 * 1024 * 1024) {
    ElMessage.warning('请选择不超过 2 MB 的 JPG 或 PNG 图片');
    return;
  }
  releasePreview();
  const url = URL.createObjectURL(file);
  const image = new Image();
  image.src = url;
  try {
    await image.decode();
    if (image.naturalWidth > 4096 || image.naturalHeight > 4096) {
      throw new Error('图片宽高不能超过 4096 像素');
    }
    preview.value = url;
    visible.value = true;
  } catch (error) {
    URL.revokeObjectURL(url);
    ElMessage.error(error instanceof Error && error.message.includes('4096')
      ? error.message : '图片无法读取，请重新选择');
  }
}

async function saveAvatar() {
  if (saving.value) return;
  saving.value = true;
  try {
    const blob = await cropper.value?.getCropImage('image/png', 0.92, 'blob', 256, 256);
    if (!(blob instanceof Blob) || blob.size === 0) {
      throw new Error('图片尚未准备好，请稍后重试');
    }
    const profile = await uploadUserAvatar(new File([blob], 'avatar.png', { type: 'image/png' }));
    emit('updated', profile);
    visible.value = false;
    ElMessage.success('头像已更新');
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '头像更新失败');
  } finally {
    saving.value = false;
  }
}

onBeforeUnmount(releasePreview);
</script>

<template>
  <div class="flex flex-col items-center gap-2">
    <VbenAvatar :src="avatarUrl" class="size-20" />
    <ElButton text type="primary" :disabled="saving" @click="fileInput?.click()">修改头像</ElButton>
    <input ref="fileInput" class="hidden" type="file" accept="image/jpeg,image/png" aria-label="选择头像图片" @change="selectFile" />
  </div>
  <ElDialog
    v-model="visible" title="裁剪头像" width="500px" class="max-w-[calc(100vw-32px)]"
    append-to-body destroy-on-close :close-on-click-modal="false"
    :close-on-press-escape="!saving" :show-close="!saving" @closed="releasePreview"
  >
    <p class="mb-4 text-sm text-muted-foreground">拖动裁剪框调整头像，支持 JPG、PNG，大小不超过 2 MB。</p>
    <div class="flex justify-center">
      <VCropper v-if="preview" ref="cropper" :img="preview" aspect-ratio="1:1" :width="cropWidth" :height="320" />
    </div>
    <template #footer>
      <ElButton :disabled="saving" @click="visible = false">取消</ElButton>
      <ElButton type="primary" :loading="saving" @click="saveAvatar">保存头像</ElButton>
    </template>
  </ElDialog>
</template>
