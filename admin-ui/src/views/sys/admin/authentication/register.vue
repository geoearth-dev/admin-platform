<script lang="ts" setup>
import type { VbenFormSchema } from '@/plugins/vben-ui/form-ui'

import { computed, h, ref } from 'vue'

import { ElMessage } from 'element-plus'

import { AuthenticationRegister } from '@/components/authentication'
import { $t } from '@/plugins/locale'
import { z } from '@/plugins/vben-ui/form-ui'

defineOptions({ name: 'Register' })

const loading = ref(false)

const formSchema = computed<VbenFormSchema[]>(() => [
  {
    component: 'VbenInput',
    componentProps: {
      autocomplete: 'username',
      placeholder: $t('authentication.usernameTip'),
    },
    fieldName: 'username',
    label: $t('authentication.username'),
    rules: z.string().min(1, $t('authentication.usernameTip')),
  },
  {
    component: 'VbenInputPassword',
    componentProps: {
      autocomplete: 'new-password',
      passwordStrength: true,
      placeholder: $t('authentication.password'),
    },
    fieldName: 'password',
    label: $t('authentication.password'),
    renderComponentContent: () => ({
      strengthText: () => $t('authentication.passwordStrength'),
    }),
    rules: z.string().min(1, $t('authentication.passwordTip')),
  },
  {
    component: 'VbenInputPassword',
    componentProps: {
      autocomplete: 'new-password',
      placeholder: $t('authentication.confirmPassword'),
    },
    dependencies: {
      resolve: ({ values }) => ({
        rules: z
          .string()
          .min(1, $t('authentication.passwordTip'))
          .refine((value) => value === values.password, {
            message: $t('authentication.confirmPasswordTip'),
          }),
      }),
      triggerFields: ['password'],
    },
    fieldName: 'confirmPassword',
    label: $t('authentication.confirmPassword'),
  },
  {
    component: 'VbenCheckbox',
    fieldName: 'agreePolicy',
    renderComponentContent: () => ({
      default: () =>
        h('span', [
          $t('authentication.agree'),
          h(
            'a',
            {
              class: 'vben-link ml-1',
              href: '',
              onClick: (event: Event) => event.preventDefault(),
            },
            `${$t('authentication.privacyPolicy')} & ${$t('authentication.terms')}`,
          ),
        ]),
    }),
    rules: z.boolean().refine(Boolean, $t('authentication.agreeTip')),
  },
])

function handleSubmit() {
  ElMessage.info('用户注册功能暂未接入')
}
</script>

<template>
  <AuthenticationRegister
    :form-schema="formSchema"
    :loading="loading"
    @submit="handleSubmit"
  />
</template>
