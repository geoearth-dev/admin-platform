<template>
  <div @keydown.enter.prevent="handleSubmit">
    <Title>
      <slot name="title">
        {{ title || `${$t('authentication.welcomeBack')} 👋🏻` }}
      </slot>
      <template #desc>
        <span class="text-muted-foreground">
          <slot name="subTitle">
            {{ subTitle || $t('authentication.loginSubtitle') }}
          </slot>
        </span>
      </template>
    </Title>

    <Form />

    <div v-if="showRememberMe || showForgetPassword" class="mb-6 flex justify-between">
      <div class="flex-center">
        <VbenCheckbox v-if="showRememberMe" v-model="rememberMe" name="rememberMe">
          {{ $t('authentication.rememberMe') }}
        </VbenCheckbox>
      </div>
      <span
        v-if="showForgetPassword"
        class="vben-link text-sm font-normal"
        @click="handleGo(forgetPasswordPath)"
      >
        {{ $t('authentication.forgetPassword') }}
      </span>
    </div>

    <VbenButton
      :class="{ 'cursor-wait': loading }"
      :loading="loading"
      aria-label="login"
      class="w-full"
      @click="handleSubmit"
    >
      {{ submitButtonText || $t('common.login') }}
    </VbenButton>

    <div
      v-if="showCodeLogin || showQrcodeLogin"
      class="mt-4 mb-2 flex items-center justify-between"
    >
      <VbenButton
        v-if="showCodeLogin"
        class="w-1/2"
        variant="outline"
        @click="handleGo(codeLoginPath)"
      >
        {{ $t('authentication.mobileLogin') }}
      </VbenButton>
      <VbenButton
        v-if="showQrcodeLogin"
        class="ml-4 w-1/2"
        variant="outline"
        @click="handleGo(qrCodeLoginPath)"
      >
        {{ $t('authentication.qrcodeLogin') }}
      </VbenButton>
    </div>

    <slot name="third-party-login">
      <ThirdPartyLogin v-if="showThirdPartyLogin" />
    </slot>

    <slot name="to-register">
      <div v-if="showRegister" class="mt-3 text-center text-sm">
        {{ $t('authentication.accountTip') }}
        <span class="vben-link text-sm font-normal" @click="handleGo(registerPath)">
          {{ $t('authentication.createAccount') }}
        </span>
      </div>
    </slot>
  </div>
</template>
<script setup lang="ts">
import type { LoginParams } from '@/api/admin/auth';
import type { VbenFormSchema } from '@/plugins/vben-ui/form-ui';

import { computed, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';

import { $t } from '@/plugins/locale';
import { useVbenForm } from '@/plugins/vben-ui/form-ui';
import { VbenButton, VbenCheckbox } from '@/plugins/vben-ui/shadcn-ui';

import Title from './auth-title.vue';
import ThirdPartyLogin from './third-party-login.vue';
import type { AuthenticationProps } from './types';

interface Props extends AuthenticationProps {
  formSchema?: VbenFormSchema[];
}

defineOptions({ name: 'AuthenticationLogin' });

const props = withDefaults(defineProps<Props>(), {
  codeLoginPath: '/auth/code-login',
  forgetPasswordPath: '/auth/forget-password',
  formSchema: () => [],
  loading: false,
  qrCodeLoginPath: '/auth/qrcode-login',
  registerPath: '/auth/register',
  showCodeLogin: true,
  showForgetPassword: true,
  showQrcodeLogin: true,
  showRegister: true,
  showRememberMe: true,
  showThirdPartyLogin: true,
  submitButtonText: '',
  subTitle: '',
  title: '',
});

const emit = defineEmits<{
  submit: [values: LoginParams];
}>();

const router = useRouter();
const rememberKey = `REMEMBER_ME_USERNAME_${location.hostname}`;
const rememberedUsername = localStorage.getItem(rememberKey) ?? '';
const rememberMe = ref(Boolean(rememberedUsername));

const [Form, formApi] = useVbenForm(
  reactive({
    commonConfig: {
      hideLabel: true,
      hideRequiredMark: true,
    },
    schema: computed(() => props.formSchema),
    showDefaultActions: false,
  }),
);

onMounted(async () => {
  if (rememberedUsername) {
    await formApi.setFieldValue('username', rememberedUsername);
  }
});

async function handleSubmit() {
  if (props.loading) {
    return;
  }

  const { valid } = await formApi.validate();
  if (!valid) {
    return;
  }

  const values = await formApi.getValues();
  const username = String(values.username ?? '');
  localStorage.setItem(rememberKey, rememberMe.value ? username : '');
  emit('submit', {
    code: String(values.code ?? ''),
    password: String(values.password ?? ''),
    rememberMe: rememberMe.value,
    username,
    uuid: '',
  });
}

function handleGo(path: string) {
  void router.push(path);
}

defineExpose({
  getFormApi: () => formApi,
});
</script>
