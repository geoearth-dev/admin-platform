<script setup lang="ts">
import type { SecuritySettings } from '@/api/example/profile'
import type { FormSchemaItem, SettingChange } from '@/components/profile'
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElSkeleton } from 'element-plus'
import {
  getSecuritySettingsApi,
  updateSecuritySettingsApi,
} from '@/api/example/profile'
import { ProfileSecuritySetting } from '@/components/profile'

const settings = ref<SecuritySettings>()
const saving = ref(false)
const formSchema = computed<FormSchemaItem[]>(() =>
  settings.value
    ? [
        {
          fieldName: 'accountPassword',
          label: '账户密码',
          description: '演示账户密码保护开关',
          value: settings.value.accountPassword,
        },
        {
          fieldName: 'securityPhone',
          label: '密保手机',
          description: settings.value.securityPhone
            ? '演示手机：138****8293'
            : '未启用密保手机',
          value: settings.value.securityPhone,
        },
        {
          fieldName: 'securityQuestion',
          label: '密保问题',
          description: settings.value.securityQuestion
            ? '已启用演示密保问题'
            : '未设置密保问题',
          value: settings.value.securityQuestion,
        },
        {
          fieldName: 'securityEmail',
          label: '备用邮箱',
          description: settings.value.securityEmail
            ? '演示邮箱：adm***@example.com'
            : '未启用备用邮箱',
          value: settings.value.securityEmail,
        },
        {
          fieldName: 'securityMfa',
          label: 'MFA 设备',
          description: settings.value.securityMfa
            ? '已启用演示 MFA 设备'
            : '未绑定 MFA 设备',
          value: settings.value.securityMfa,
        },
      ]
    : [],
)

onMounted(async () => {
  settings.value = await getSecuritySettingsApi()
})
async function handleChange({ fieldName, value }: SettingChange) {
  if (saving.value) return
  saving.value = true
  try {
    settings.value = await updateSecuritySettingsApi({ [fieldName]: value })
    ElMessage.success('演示安全设置已更新')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '更新失败')
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <section>
    <h2 class="mb-6 text-lg font-semibold">安全设置</h2>
    <ElSkeleton
      v-if="!settings"
      :rows="5"
      animated
    />
    <ProfileSecuritySetting
      v-else
      :form-schema="formSchema"
      :disabled="saving"
      @change="handleChange"
    />
  </section>
</template>
