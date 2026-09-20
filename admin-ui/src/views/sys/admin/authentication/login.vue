<script lang="ts" setup>
import type { LoginParams } from '@/api/admin/auth'
import type { VbenFormSchema } from '@/plugins/vben-ui/form-ui'

import { computed, h, onMounted, ref } from 'vue'

import { getCodeImg } from '@/api/admin/auth'
import { AuthenticationLogin } from '@/components/authentication'
import { $t } from '@/plugins/locale'
import { z } from '@/plugins/vben-ui/form-ui'
import { useAuthStore } from '@/store'

defineOptions({ name: 'Login' })

const authStore = useAuthStore()
const captchaEnabled = ref(true)
const captchaImage = ref('')
const captchaUuid = ref('')

const formSchema = computed<VbenFormSchema[]>(() => {
  const schema: VbenFormSchema[] = [
    {
      component: 'VbenInput',
      componentProps: {
        autocomplete: 'username',
        placeholder: $t('authentication.usernameTip'),
      },
      defaultValue: 'admin',
      fieldName: 'username',
      label: $t('authentication.username'),
      rules: z.string().min(1, $t('authentication.usernameTip')),
    },
    {
      component: 'VbenInputPassword',
      componentProps: {
        autocomplete: 'current-password',
        placeholder: $t('authentication.passwordTip'),
      },
      defaultValue: 'admin123',
      fieldName: 'password',
      label: $t('authentication.password'),
      rules: z.string().min(1, $t('authentication.passwordTip')),
    },
  ]

  if (captchaEnabled.value) {
    schema.push({
      component: 'Input',
      componentProps: {
        autocomplete: 'off',
        placeholder: $t('authentication.code'),
      },
      fieldName: 'code',
      label: $t('authentication.code'),
      renderComponentContent: () => ({
        append: () =>
          h('img', {
            alt: $t('authentication.code'),
            class: 'h-8 w-[100px] cursor-pointer object-fill',
            onClick: getCode,
            src: captchaImage.value,
            title: $t('authentication.code'),
          }),
      }),
      rules: z.string().min(1, $t('authentication.verifyRequiredTip')),
    })
  }

  return schema
})

onMounted(getCode)

async function getCode() {
  const result = await getCodeImg()
  captchaEnabled.value = result.enabled ?? true
  captchaUuid.value = captchaEnabled.value ? result.uuid : ''
  captchaImage.value = captchaEnabled.value
    ? `data:image/gif;base64,${result.image}`
    : ''
}

async function handleSubmit(values: LoginParams) {
  try {
    await authStore.authLogin({
      ...values,
      code: captchaEnabled.value ? (values.code ?? '') : '',
      uuid: captchaUuid.value,
    })
  } catch {
    // 登录失败后更新验证码，避免重复提交已经失效的验证码。
    if (captchaEnabled.value) {
      await getCode()
    }
  }
}
</script>

<template>
  <AuthenticationLogin
    :form-schema="formSchema"
    :loading="authStore.loginLoading"
    @submit="handleSubmit"
  />
</template>
