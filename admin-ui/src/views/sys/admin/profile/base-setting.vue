<script setup lang="ts">
import type { UserProfileResult, UserProfileUpdateParams } from '@/types/base/api/system/user';
import { ref, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { updateUserProfile } from '@/api/system/user';
import { useVbenForm, z } from '@/plugins/vben-ui/form-ui';

const props = defineProps<{ profile: UserProfileResult }>();
const emit = defineEmits<{ updated: [profile: UserProfileResult] }>();
const saving = ref(false);
interface ProfileFormValues extends Record<string, unknown>, UserProfileUpdateParams {
  userName: string;
  deptName: string;
  roleGroup: string;
  postGroup: string;
}

const [Form, formApi] = useVbenForm<ProfileFormValues>({
  layout: 'vertical',
  wrapperClass: 'grid-cols-1 md:grid-cols-2',
  resetButtonOptions: { show: false },
  submitButtonOptions: { content: '保存基本信息' },
  handleSubmit: async ({ nickName, email, phoneNumber, sex, remark }) => {
    if (saving.value) return;
    saving.value = true;
    formApi.setState({ submitButtonOptions: { loading: true } });
    try {
      const profile = await updateUserProfile({ nickName, email, phoneNumber, sex, remark });
      emit('updated', profile);
      ElMessage.success('个人资料已更新');
    } catch (error) {
      ElMessage.error(error instanceof Error ? error.message : '个人资料更新失败');
    } finally {
      saving.value = false;
      formApi.setState({ submitButtonOptions: { loading: false } });
    }
  },
  schema: [
    { fieldName: 'userName', component: 'Input', label: '用户账号', componentProps: { disabled: true } },
    {
      fieldName: 'nickName', component: 'Input', label: '用户昵称',
      componentProps: { maxlength: 30 },
      rules: z.string().trim().min(1, '请输入昵称').max(30, '昵称不能超过 30 个字符'),
    },
    {
      fieldName: 'email', component: 'Input', label: '邮箱',
      componentProps: { maxlength: 50, placeholder: '请输入邮箱（选填）' },
      rules: z.string().trim().max(50, '邮箱不能超过 50 个字符').refine(
        (value) => value === '' || z.email().safeParse(value).success, '邮箱格式不正确',
      ),
    },
    {
      fieldName: 'phoneNumber', component: 'Input', label: '手机号码',
      componentProps: { maxlength: 11, placeholder: '请输入手机号码（选填）' },
      rules: z.string().trim().regex(/^$|^1[3-9]\d{9}$/, '手机号码格式不正确'),
    },
    {
      fieldName: 'sex', component: 'Select', label: '性别',
      componentProps: { options: [
        { label: '男', value: '0' }, { label: '女', value: '1' }, { label: '未知', value: '2' },
      ] },
      rules: z.enum(['0', '1', '2']),
    },
    { fieldName: 'deptName', component: 'Input', label: '所属部门', componentProps: { disabled: true } },
    { fieldName: 'roleGroup', component: 'Input', label: '所属角色', componentProps: { disabled: true } },
    { fieldName: 'postGroup', component: 'Input', label: '所属岗位', componentProps: { disabled: true } },
    {
      fieldName: 'remark', component: 'Textarea', label: '备注', formItemClass: 'md:col-span-2',
      componentProps: { rows: 4, maxlength: 500, showWordLimit: true },
      rules: z.string().trim().max(500, '备注不能超过 500 个字符'),
    },
  ],
});

watch(() => props.profile, (profile) => {
  void formApi.setValues({
    userName: profile.userName,
    nickName: profile.nickName ?? '',
    email: profile.email ?? '',
    phoneNumber: profile.phoneNumber ?? '',
    sex: profile.sex === '0' || profile.sex === '1' ? profile.sex : '2',
    remark: profile.remark ?? '',
    deptName: profile.deptName || '未分配',
    roleGroup: profile.roleGroup || '未分配',
    postGroup: profile.postGroup || '未分配',
  });
}, { immediate: true });
</script>

<template>
  <section class="w-full max-w-3xl">
    <h2 class="mb-2 text-lg font-semibold">基本设置</h2>
    <p class="mb-6 text-sm text-muted-foreground">账号、部门、角色和岗位由管理员维护。</p>
    <Form />
  </section>
</template>
