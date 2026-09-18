<script setup lang="ts">
import type {
  FormInstance,
  FormRules,
  UploadProps,
  UploadRequestOptions,
} from 'element-plus'
import type { NoticeSaveParams } from '@/types/base/api/system/notice'

import { computed, nextTick, ref } from 'vue'
import {
  ElForm,
  ElFormItem,
  ElInput,
  ElMessage,
  ElOption,
  ElRadio,
  ElRadioGroup,
  ElSelect,
  ElUpload,
} from 'element-plus'
import { addNotice, getNotice, updateNotice } from '@/api/system/notice'
import { upload_file } from '@/api/example/upload'
import { VbenTiptap } from '@/components/tiptap'
import { $t } from '@/plugins/locale'
import { preferences } from '@/plugins/preference'
import { useVbenDrawer } from '@/plugins/vben-ui/popup-ui'
import { VbenButton } from '@/plugins/vben-ui/shadcn-ui'
import { useUserStore } from '@/store'
import { useDict } from '@/utils/dict'

const emit = defineEmits<{ success: [] }>()
const userStore = useUserStore()
const { sys_notice_type, sys_notice_status } = useDict(
  'sys_notice_type',
  'sys_notice_status',
)
const formRef = ref<FormInstance>()
const form = ref<NoticeSaveParams>(defaults())
const avatarMode = ref<'keep' | 'publisher' | 'custom'>('publisher')
const customAvatar = ref('')
const loading = ref(false)
const saving = ref(false)
const uploading = ref(false)
const busy = computed(() => loading.value || saving.value || uploading.value)
const error = ref('')
const title = computed(() =>
  $t(form.value.id ? 'system.notice.editTitle' : 'system.notice.createTitle'),
)
const selectedAvatar = computed(() => {
  if (avatarMode.value === 'publisher')
    return userStore.userInfo?.avatar || null
  if (avatarMode.value === 'custom') return customAvatar.value.trim() || null
  return form.value.avatar || null
})
const rules = computed<FormRules>(() => ({
  noticeTitle: [
    {
      required: true,
      whitespace: true,
      message: $t('system.notice.titleRequired'),
      trigger: 'blur',
    },
  ],
  noticeType: [
    {
      required: true,
      message: $t('system.notice.typeRequired'),
      trigger: 'change',
    },
  ],
  status: [
    {
      required: true,
      message: $t('system.notice.statusRequired'),
      trigger: 'change',
    },
  ],
}))
const [Drawer, drawerApi] = useVbenDrawer({
  onConfirm: submit,
  onBeforeClose: () => !busy.value,
})

function defaults(): NoticeSaveParams {
  return {
    noticeTitle: '',
    noticeType: '1',
    noticeContent: '',
    status: '1',
    link: '',
    avatar: null,
    remark: '',
  }
}

async function open(id?: number) {
  if (busy.value) return
  form.value = defaults()
  avatarMode.value = id ? 'keep' : 'publisher'
  customAvatar.value = ''
  error.value = ''
  drawerApi.open()
  if (id) {
    loading.value = true
    drawerApi.setState({ loading: true })
    try {
      const notice = await getNotice(id)
      form.value = {
        id: notice.id,
        noticeTitle: notice.noticeTitle,
        noticeType: notice.noticeType,
        noticeContent: notice.noticeContent ?? '',
        status: notice.status ?? '1',
        avatar: notice.avatar ?? null,
        link: notice.link ?? '',
        remark: notice.remark ?? '',
      }
    } catch {
      error.value = 'system.notice.loadFailed'
    } finally {
      loading.value = false
      drawerApi.setState({ loading: false })
    }
  }
  await nextTick()
  formRef.value?.clearValidate()
}

function validUrl(value: string) {
  if (value.startsWith('/') && !value.startsWith('//')) return true
  try {
    const url = new URL(value)
    return (
      ['http:', 'https:'].includes(url.protocol) &&
      !url.username &&
      !url.password
    )
  } catch {
    return false
  }
}

const beforeUpload: UploadProps['beforeUpload'] = (file) => {
  if (
    !['image/jpeg', 'image/png', 'image/webp'].includes(file.type) ||
    file.size > 2 * 1024 * 1024
  ) {
    ElMessage.warning($t('system.notice.avatarTip'))
    return false
  }
  return true
}

async function uploadAvatar(options: UploadRequestOptions) {
  uploading.value = true
  try {
    const result = await upload_file(options)
    customAvatar.value = result.url
    return result
  } finally {
    uploading.value = false
  }
}

async function submit() {
  if (busy.value || error.value) return
  if (!(await formRef.value?.validate().catch(() => false))) return
  if (
    avatarMode.value === 'custom' &&
    (!selectedAvatar.value || !validUrl(selectedAvatar.value))
  ) {
    ElMessage.warning($t('system.notice.avatarRequired'))
    return
  }
  const link = form.value.link?.trim() ?? ''
  if (link && !validUrl(link)) {
    ElMessage.warning($t('system.notice.invalidLink'))
    return
  }
  saving.value = true
  drawerApi.setState({ confirmLoading: true })
  try {
    // 编辑默认沿用原头像，只有重新选择时才替换快照。
    const payload: NoticeSaveParams = {
      ...form.value,
      noticeTitle: form.value.noticeTitle.trim(),
      link,
      avatar: selectedAvatar.value,
    }
    if (payload.id) await updateNotice(payload)
    else await addNotice(payload)
    ElMessage.success($t('system.notice.saveSuccess'))
    saving.value = false
    await drawerApi.close()
    emit('success')
  } catch {
    ElMessage.error($t('system.notice.saveFailed'))
  } finally {
    saving.value = false
    drawerApi.setState({ confirmLoading: false })
  }
}

defineExpose({ open })
</script>

<template>
  <Drawer
    :title="title"
    class="w-[820px] max-w-full sm:max-w-[820px]"
  >
    <div
      v-if="error"
      class="p-4 text-sm text-destructive"
    >
      {{ $t(error) }}
    </div>
    <ElForm
      v-else
      ref="formRef"
      :model="form"
      :rules="rules"
      label-position="top"
      :disabled="busy"
      class="p-2"
    >
      <ElFormItem
        :label="$t('system.notice.noticeTitle')"
        prop="noticeTitle"
      >
        <ElInput
          v-model="form.noticeTitle"
          maxlength="50"
          show-word-limit
          :placeholder="$t('system.notice.titlePlaceholder')"
        />
      </ElFormItem>
      <div class="grid gap-x-5 sm:grid-cols-2">
        <ElFormItem
          :label="$t('system.notice.type')"
          prop="noticeType"
        >
          <ElSelect
            v-model="form.noticeType"
            class="w-full"
            ><ElOption
              v-for="item in sys_notice_type"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          /></ElSelect>
        </ElFormItem>
        <ElFormItem
          :label="$t('system.notice.status')"
          prop="status"
        >
          <ElRadioGroup v-model="form.status"
            ><ElRadio
              v-for="item in sys_notice_status"
              :key="item.value"
              :value="item.value"
              >{{ item.label }}</ElRadio
            ></ElRadioGroup
          >
        </ElFormItem>
      </div>
      <ElFormItem :label="$t('system.notice.avatar')">
        <div
          class="w-full space-y-3 rounded-lg border border-border bg-muted/30 p-3"
        >
          <ElRadioGroup v-model="avatarMode">
            <ElRadio
              v-if="form.id"
              value="keep"
              >{{ $t('system.notice.keepAvatar') }}</ElRadio
            >
            <ElRadio value="publisher">{{
              $t(
                form.id
                  ? 'system.notice.myAvatar'
                  : 'system.notice.publisherAvatar',
              )
            }}</ElRadio>
            <ElRadio value="custom">{{
              $t('system.notice.customAvatar')
            }}</ElRadio>
          </ElRadioGroup>
          <div class="flex items-center gap-3">
            <img
              :src="selectedAvatar || preferences.app.defaultAvatar"
              alt=""
              class="size-12 shrink-0 rounded-full object-cover"
            />
            <template v-if="avatarMode === 'custom'">
              <ElInput
                v-model="customAvatar"
                :placeholder="$t('system.notice.avatarPlaceholder')"
              />
              <ElUpload
                :show-file-list="false"
                :http-request="uploadAvatar"
                :before-upload="beforeUpload"
                :disabled="busy"
                accept=".jpg,.jpeg,.png,.webp"
              >
                <VbenButton
                  type="button"
                  size="sm"
                  variant="outline"
                  :disabled="busy"
                  >{{ $t('system.notice.uploadAvatar') }}</VbenButton
                >
              </ElUpload>
            </template>
          </div>
        </div>
      </ElFormItem>
      <ElFormItem :label="$t('system.notice.link')">
        <div class="w-full space-y-1">
          <ElInput
            v-model="form.link"
            maxlength="2048"
            clearable
            :placeholder="$t('system.notice.linkPlaceholder')"
          />
          <p class="text-xs text-muted-foreground">
            {{ $t('system.notice.linkTip') }}
          </p>
        </div>
      </ElFormItem>
      <ElFormItem :label="$t('system.notice.content')">
        <VbenTiptap
          v-model="form.noticeContent"
          class="w-full"
          :editable="!busy"
          :min-height="240"
          :max-height="420"
        />
      </ElFormItem>
      <ElFormItem :label="$t('system.notice.remark')">
        <ElInput
          v-model="form.remark"
          type="textarea"
          :rows="2"
          maxlength="500"
        />
      </ElFormItem>
    </ElForm>
    <template #footer>
      <VbenButton
        variant="outline"
        :disabled="busy"
        @click="drawerApi.close()"
        >{{ $t('common.cancel') }}</VbenButton
      >
      <VbenButton
        :disabled="busy || !!error"
        :loading="saving"
        @click="submit"
        >{{ $t('common.confirm') }}</VbenButton
      >
    </template>
  </Drawer>
</template>
