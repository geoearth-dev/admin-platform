<template>
  <div>
    <slot name="title">
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
    </slot>

    <ElForm ref="formRef" :model="formModel" :rules="rules" @submit.prevent="handleSubmit">
      <!-- 用户名 -->
      <ElFormItem prop="username">
        <ElInput
          v-model="formModel.username"
          :placeholder="$t('authentication.usernameTip')"
          autocomplete="username"
          clearable
          size="large"
        />
      </ElFormItem>

      <!-- 密码 -->
      <ElFormItem prop="password">
        <ElInput
          v-model="formModel.password"
          :placeholder="$t('authentication.passwordTip')"
          autocomplete="current-password"
          show-password
          size="large"
          type="password"
        />
      </ElFormItem>

      <!-- 验证码 -->
      <ElFormItem prop="code" v-if="captchaEnabled">
        <!-- <SliderCaptcha v-model="formModel.captcha" /> -->
        <div class="flex w-full items-center gap-3">
          <ElInput
            v-model="formModel.code"
            auto-complete="off"
            placeholder="验证码"
            class="block h-10 w-auto cursor-pointer"
            size="large"
          >
            <template #prefix>
              <ShieldCheck class="size-4" />
            </template>
          </ElInput>
          <div class="login-code">
            <img :src="codeUrl" @click="getCode" class="block h-10 w-auto cursor-pointer" />
          </div>
        </div>
      </ElFormItem>
      <!-- 记住账号、忘记密码 -->
      <div v-if="showRememberMe || showForgetPassword" class="mb-6 flex justify-between">
        <div class="flex-center">
          <VbenCheckbox v-if="showRememberMe" v-model="formModel.rememberMe" name="rememberMe">
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

      <!-- 登录按钮 -->
      <ElButton
        :class="{ 'cursor-wait': loading }"
        :loading="loading"
        class="w-full"
        native-type="submit"
        type="primary"
      >
        {{ submitButtonText || $t('common.login') }}
      </ElButton>
    </ElForm>

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
    <!-- 第三方登录 -->
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
import { computed, onMounted, reactive, ref, useTemplateRef } from 'vue';

import { useRouter } from 'vue-router';

import { $t } from '@/plugins/locale/index.ts';

import type { AuthenticationProps } from './types';

import ThirdPartyLogin from './third-party-login.vue';

import Title from './auth-title.vue';

import { VbenCheckbox, VbenButton } from '@/plugins/vben-ui/shadcn-ui/index.ts';
import { getCodeImg, type LoginParams } from '@/api/system/auth/auth.ts';
import type { ElButton, FormInstance, FormRules } from 'element-plus';
import { ShieldCheck } from '@/assets/icons';
// import { SliderCaptcha } from '@/components/captcha';

defineOptions({
  name: 'AuthenticationLogin',
});

const props = withDefaults(defineProps<AuthenticationProps>(), {
  codeLoginPath: '/auth/code-login',
  forgetPasswordPath: '/auth/forget-password',
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

const emit = defineEmits<{ submit: [values: LoginParams] }>();

const router = useRouter();
const formRef = useTemplateRef<FormInstance>('formRef');
const codeUrl = ref('');
// 验证码开关
const captchaEnabled = ref(true);

const formModel = reactive<LoginParams>({
  username: 'admin',
  password: 'admin123',
  rememberMe: true,
  code: '',
  uuid: '',
});
onMounted(() => {
  getCode();
});
/**
 * 使用 computed 是为了语言切换后，校验提示可以重新生成。
 */
const rules = computed<FormRules<LoginParams>>(() => ({
  username: [
    {
      message: $t('authentication.usernameTip'),
      required: true,
      trigger: 'blur',
    },
  ],
  password: [
    {
      message: $t('authentication.passwordTip'),
      required: true,
      trigger: 'blur',
    },
  ],
  code: [
    {
      message: $t('authentication.verifyRequiredTip'),
      required: true,
      trigger: 'blur',
    },
  ],
}));

function getCode() {
  getCodeImg().then((res) => {
    captchaEnabled.value = res.enabled === undefined ? true : res.enabled;
    if (captchaEnabled.value) {
      codeUrl.value = 'data:image/gif;base64,' + res.image;
      formModel.uuid = res.uuid;
    }
  });
}
async function handleSubmit() {
  // 防止加载过程中重复提交
  if (props.loading || !formRef.value) {
    return;
  }

  const valid = await formRef.value.validate().catch(() => false);
  if (valid) {
    emit('submit', formModel);
  }
}

function handleGo(path: string) {
  router.push(path);
}

function reset() {
  formRef.value?.resetFields();
}
function validate() {
  return formRef.value?.validate();
}
defineExpose({
  reset,
  validate,
  getCode,
});
</script>

<style scoped lang="scss">
.login-code {
  width: 104px;
  height: 34px;
  float: right;
  img {
    height: 100%;
    width: 102px;
  }
}
</style>
