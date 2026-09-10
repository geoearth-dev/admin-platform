<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { updateUserPasswordApi } from '@/api/example/profile'
import { useVbenForm, z } from '@/plugins/vben-ui/form-ui'

interface PasswordFormValues extends Record<string, unknown> {
  oldPassword: string
  newPassword: string
  confirmPassword: string
}
const saving = ref(false)
const [Form, formApi] = useVbenForm<PasswordFormValues>({
  layout: 'vertical',
  wrapperClass: 'grid-cols-1',
  resetButtonOptions: { show: false },
  submitButtonOptions: { content: '修改密码' },
  handleSubmit: async ({ oldPassword, newPassword }) => {
    if (saving.value) return
    saving.value = true
    formApi.setState({ submitButtonOptions: { loading: true } })
    try {
      await updateUserPasswordApi({ oldPassword, newPassword })
      await formApi.reset()
      ElMessage.success('演示密码已修改，再次修改时请使用新密码')
    } catch (error) {
      ElMessage.error(error instanceof Error ? error.message : '修改失败')
    } finally {
      saving.value = false
      formApi.setState({ submitButtonOptions: { loading: false } })
    }
  },
  schema: [
    {
      fieldName: 'oldPassword',
      component: 'InputPassword',
      label: '旧密码',
      defaultValue: '',
      rules: z.string().min(1, '请输入旧密码'),
      componentProps: { autocomplete: 'current-password' },
    },
    {
      fieldName: 'newPassword',
      component: 'InputPassword',
      label: '新密码',
      defaultValue: '',
      componentProps: { autocomplete: 'new-password', maxlength: 20 },
      dependencies: {
        triggerFields: ['oldPassword'],
        rules: (values) =>
          z
            .string()
            .min(5, '新密码至少 5 位')
            .max(20, '新密码最多 20 位')
            .refine(
              (value) => value !== values.oldPassword,
              '新密码不能与旧密码相同',
            ),
      },
    },
    {
      fieldName: 'confirmPassword',
      component: 'InputPassword',
      label: '确认密码',
      defaultValue: '',
      componentProps: { autocomplete: 'new-password', maxlength: 20 },
      dependencies: {
        triggerFields: ['newPassword'],
        rules: (values) =>
          z
            .string()
            .min(1, '请再次输入新密码')
            .refine(
              (value) => value === values.newPassword,
              '两次输入的密码不一致',
            ),
      },
    },
  ],
})
</script>

<template>
  <section class="w-full max-w-xl">
    <h2 class="mb-2 text-lg font-semibold">修改密码</h2>
    <p class="mb-6 text-sm text-muted-foreground">
      示例初始密码：Admin123。新密码需为 5–20 位，刷新页面后恢复初始密码。
    </p>
    <Form />
  </section>
</template>
