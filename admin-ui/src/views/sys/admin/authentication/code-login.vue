<script lang="ts" setup>
import type { VbenFormSchema } from '@/plugins/vben-ui/form-ui'

import { computed, ref } from 'vue'

import { ElMessage } from 'element-plus'

import { AuthenticationCodeLogin } from '@/components/authentication'
import { $t } from '@/plugins/locale'
import { z } from '@/plugins/vben-ui/form-ui'

defineOptions({ name: 'CodeLogin' })

const loading = ref(false)
const codeLength = 6

const formSchema = computed<VbenFormSchema[]>(() => [
  {
    component: 'VbenInput',
    componentProps: {
      placeholder: $t('authentication.mobile'),
    },
    fieldName: 'phoneNumber',
    label: $t('authentication.mobile'),
    rules: z
      .string()
      .min(1, $t('authentication.mobileTip'))
      .refine((value) => /^1\d{10}$/.test(value), {
        message: $t('authentication.mobileErrortip'),
      }),
  },
  {
    component: 'VbenPinInput',
    componentProps: {
      codeLength,
      createText: (countdown: number) =>
        countdown > 0
          ? $t('authentication.sendText', [countdown])
          : $t('authentication.sendCode'),
      handleSendCode: async () => {
        ElMessage.info('短信验证码功能暂未接入')
      },
      placeholder: $t('authentication.code'),
    },
    fieldName: 'code',
    label: $t('authentication.code'),
    rules: z
      .string()
      .length(codeLength, $t('authentication.codeTip', [codeLength])),
  },
])

function handleLogin() {
  ElMessage.info('短信登录功能暂未接入')
}
</script>

<template>
  <AuthenticationCodeLogin
    :form-schema="formSchema"
    :loading="loading"
    @submit="handleLogin"
  />
</template>
