<template>
  <div>
    <VbenIconButton
      @click="handleLogin"
      :tooltip="$t('authentication.dingdingLogin')"
      tooltip-side="top"
    >
      <SvgDingDingIcon />
    </VbenIconButton>

    <ElDialog
      v-model="qrCodeDialogVisible"
      :title="$t('authentication.dingdingLogin')"
      align-center
      class="dingding-qrcode-login-dialog"
      destroy-on-close
      width="340px"
      @opened="handleQrCodeLogin"
    >
      <div id="dingding_qrcode_login_element" class="flex justify-center" />
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router';

import { SvgDingDingIcon } from '@/assets/icons';
import { $t } from '@/plugins/locale';

import { VbenIconButton } from '@/plugins/vben-ui/shadcn-ui';
import { loadScript } from '@/utils/resources';
import { ref } from 'vue';
import { ElMessage } from 'element-plus';

interface Props {
  clientId: string;
  corpId: string;
  // 登录回调地址
  redirectUri?: string;
  // 是否内嵌二维码登录
  isQrCode?: boolean;
}

const props = defineProps<Props>();

const route = useRoute();

const qrCodeDialogVisible = ref(false);

const DINGTALK_SDK_URL = 'https://g.alicdn.com/dingding/h5-dingtalk-login/0.21.0/ddlogin.js';

function getRedirectUri() {
  return props.redirectUri ?? new URL(route.fullPath, window.location.origin).href;
}

/**
 * 内嵌二维码登录
 */
const handleQrCodeLogin = async () => {
  const { clientId, corpId } = props;
  if (!window.DTFrameLogin) {
    // 二维码登录 加载资源
    await loadScript(DINGTALK_SDK_URL);
  }
  window.DTFrameLogin?.(
    {
      id: 'dingding_qrcode_login_element',
      width: 300,
      height: 300,
    },
    {
      // 注意：redirect_uri 需为完整URL，扫码后钉钉会带code跳转到这里
      redirect_uri: encodeURIComponent(getRedirectUri()),
      client_id: clientId,
      scope: 'openid corpid',
      response_type: 'code',
      state: crypto.randomUUID(),
      prompt: 'consent',
      corpId,
    },
    (loginResult) => {
      const { redirectUrl } = loginResult;
      // 重定向
      window.location.href = redirectUrl;
    },
    (errorMsg: string) => {
      ElMessage.error(`Login Error: ${errorMsg}`);
    },
  );
};

const handleLogin = () => {
  const { clientId, corpId, isQrCode } = props;
  if (isQrCode) {
    qrCodeDialogVisible.value = true;
    return;
  }
  const url = new URL('https://login.dingtalk.com/oauth2/auth');
  url.searchParams.set('redirect_uri', getRedirectUri());
  url.searchParams.set('response_type', 'code');
  url.searchParams.set('client_id', clientId);
  url.searchParams.set('scope', 'openid');
  url.searchParams.set('corpid', corpId);
  url.searchParams.set('prompt', 'consent');
  url.searchParams.set('state', crypto.randomUUID());
  window.location.assign(url.href);
};
</script>

<style>
.dingding-qrcode-login-modal {
  .relative {
    padding: 0 !important;
  }
}
</style>
