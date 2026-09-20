<script lang="ts" setup>
import type { VbenFormSchema } from '@/plugins/vben-ui/form-ui'

import { computed, ref } from 'vue'

import { ElMessage } from 'element-plus'

import { AuthenticationForgetPassword } from '@/components/authentication'
import { $t } from '@/plugins/locale'
import { z } from '@/plugins/vben-ui/form-ui'

defineOptions({ name: 'ForgetPassword' })

const loading = ref(false)

const formSchema = computed<VbenFormSchema[]>(() => [
  {
    component: 'VbenInput',
    componentProps: {
      autocomplete: 'email',
      placeholder: 'example@example.com',
    },
    fieldName: 'email',
    label: $t('authentication.email'),
    rules: z
      .string()
      .min(1, $t('authentication.emailTip'))
      .email($t('authentication.emailValidErrorTip')),
  },
])

function handleSubmit() {
  ElMessage.info('找回密码功能暂未接入')
}
</script>

<template>
  <AuthenticationForgetPassword
    :form-schema="formSchema"
    :loading="loading"
    @submit="handleSubmit"
  />
</template>
