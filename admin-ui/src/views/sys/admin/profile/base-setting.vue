<script setup lang="ts">
import type { ProfileUpdateParams, UserProfile } from '@/api/example/profile'
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { updateUserProfileApi } from '@/api/example/profile'
import { useVbenForm, z } from '@/plugins/vben-ui/form-ui'

const props = defineProps<{ profile: UserProfile }>()
const emit = defineEmits<{ updated: [values: ProfileUpdateParams] }>()
const saving = ref(false)
interface ProfileFormValues
  extends Record<string, unknown>, ProfileUpdateParams {}
const [Form, formApi] = useVbenForm<ProfileFormValues>({
  layout: 'vertical',
  wrapperClass: 'grid-cols-1',
  resetButtonOptions: { show: false },
  submitButtonOptions: { content: '更新基本信息' },
  handleSubmit: async ({ realName, username, roles, introduction }) => {
    if (saving.value) return
    saving.value = true
    formApi.setState({ submitButtonOptions: { loading: true } })
    try {
      const values = await updateUserProfileApi({
        realName,
        username,
        roles,
        introduction,
      })
      emit('updated', values)
      ElMessage.success('基本信息已更新')
    } catch (error) {
      ElMessage.error(error instanceof Error ? error.message : '更新失败')
    } finally {
      saving.value = false
      formApi.setState({ submitButtonOptions: { loading: false } })
    }
  },
  schema: [
    {
      fieldName: 'realName',
      component: 'Input',
      label: '姓名',
      rules: z
        .string()
        .trim()
        .min(1, '请输入姓名')
        .max(30, '姓名不能超过 30 个字符'),
    },
    {
      fieldName: 'username',
      component: 'Input',
      label: '用户名',
      rules: z
        .string()
        .trim()
        .min(1, '请输入用户名')
        .max(30, '用户名不能超过 30 个字符'),
    },
    {
      fieldName: 'roles',
      component: 'Select',
      label: '角色',
      componentProps: {
        multiple: true,
        filterable: true,
        allowCreate: true,
        options: [
          { label: '管理员', value: 'super' },
          { label: '用户', value: 'user' },
          { label: '测试', value: 'test' },
        ],
      },
      rules: z.array(z.string()).min(1, '请选择角色'),
    },
    {
      fieldName: 'introduction',
      component: 'Textarea',
      label: '个人简介',
      componentProps: { rows: 4, maxlength: 300, showWordLimit: true },
      rules: z.string().max(300, '个人简介不能超过 300 个字符'),
    },
  ],
})
watch(
  () => props.profile,
  (profile) => {
    formApi.setValues({
      realName: profile.realName,
      username: profile.username,
      roles: [...profile.roles],
      introduction: profile.introduction,
    })
  },
  { immediate: true },
)
</script>

<template>
  <section class="w-full max-w-xl">
    <h2 class="mb-6 text-lg font-semibold">基本设置</h2>
    <Form />
  </section>
</template>
