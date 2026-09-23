<template>
  <AuthenticationLogin
    :form-schema="formSchema"
    :loading="authStore.loginLoading"
    @submit="handleSubmit"
  />
</template>
<script lang="ts" setup>
import type { LoginParams } from '@/api/admin/auth';
import type { VbenFormSchema } from '@/plugins/vben-ui/form-ui';

import { computed, markRaw, onMounted, ref } from 'vue';

import { getCodeImg } from '@/api/admin/auth';
import { AuthenticationLogin } from '@/components/authentication';
import { ImageCaptcha } from '@/components/captcha';
import { $t } from '@/plugins/locale';
import { z } from '@/plugins/vben-ui/form-ui';
import { useAuthStore } from '@/store';

defineOptions({ name: 'Login' });

const authStore = useAuthStore();
const captchaEnabled = ref(true);
const captchaImage = ref('');
const captchaLoading = ref(false);
const captchaUuid = ref('');

const formSchema = computed<VbenFormSchema[]>(() => {
  const schema: VbenFormSchema[] = [
    {
      component: 'VbenInput',
      componentProps: {
        autocomplete: 'username',
        placeholder: $t('authentication.usernameTip'),
      },
      defaultValue: 'user',
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
      defaultValue: '123456',
      fieldName: 'password',
      label: $t('authentication.password'),
      rules: z.string().min(1, $t('authentication.passwordTip')),
    },
  ];

  if (captchaEnabled.value) {
    schema.push({
      component: markRaw(ImageCaptcha),
      componentProps: {
        loading: captchaLoading.value,
        onRefresh: getCode,
        src: captchaImage.value,
      },
      fieldName: 'code',
      label: $t('authentication.code'),
      rules: z.string().min(1, $t('authentication.verifyRequiredTip')),
    });
  }

  return schema;
});

onMounted(getCode);

async function getCode() {
  if (captchaLoading.value) return;

  captchaLoading.value = true;
  try {
    const result = await getCodeImg();
    captchaEnabled.value = result.enabled ?? true;
    captchaUuid.value = captchaEnabled.value ? result.uuid : '';
    captchaImage.value =
      captchaEnabled.value && result.image ? `data:image/gif;base64,${result.image}` : '';
  } catch {
    captchaImage.value = '';
    captchaUuid.value = '';
  } finally {
    captchaLoading.value = false;
  }
}

async function handleSubmit(values: LoginParams) {
  try {
    await authStore.authLogin({
      ...values,
      code: captchaEnabled.value ? (values.code ?? '') : '',
      uuid: captchaUuid.value,
    });
  } catch {
    // 登录失败后更新验证码，避免重复提交已经失效的验证码。
    if (captchaEnabled.value) {
      await getCode();
    }
  }
}
</script>
