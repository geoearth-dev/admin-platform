<template>
  <AuthenticationLogin ref="loginRef" :loading="authStore.loginLoading" @submit="handleSubmit" />
</template>

<script lang="ts" setup>
import { AuthenticationLogin } from '@/components/authentication';

import { useAuthStore } from '@/store';
import type { LoginParams } from '@/api/system/auth/auth';
import { ref } from 'vue';
const loginRef = ref<InstanceType<typeof AuthenticationLogin> | null>(null);
defineOptions({ name: 'Login' });

const authStore = useAuthStore();

/**
 * 页面只负责调用登录业务。
 * 表单校验、记住账号等逻辑由 AuthenticationLogin 负责。
 */
async function handleSubmit(values: LoginParams) {
  try {
    await authStore.authLogin(values);
  } catch {
    await loginRef.value?.getCode();
  }
}
</script>
