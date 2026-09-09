<template>
  <div class="w-full sm:mx-auto md:max-w-md">
    <div class="mt-4 flex items-center justify-between">
      <span class="w-[35%] border-b border-input dark:border-gray-600"></span>
      <span class="text-center text-xs text-muted-foreground uppercase">
        {{ $t('authentication.thirdPartyLogin') }}
      </span>
      <span class="w-[35%] border-b border-input dark:border-gray-600"></span>
    </div>

    <div class="mt-4 flex flex-wrap justify-center">
      <VbenIconButton :tooltip="$t('authentication.wechatLogin')" tooltip-side="top" class="mb-3">
        <SvgWeChatIcon />
      </VbenIconButton>
      <VbenIconButton :tooltip="$t('authentication.qqLogin')" tooltip-side="top" class="mb-3">
        <SvgQQChatIcon />
      </VbenIconButton>
      <VbenIconButton :tooltip="$t('authentication.githubLogin')" tooltip-side="top" class="mb-3">
        <SvgGithubIcon />
      </VbenIconButton>
      <VbenIconButton :tooltip="$t('authentication.googleLogin')" tooltip-side="top" class="mb-3">
        <SvgGoogleIcon />
      </VbenIconButton>
      <DingdingLogin
        v-if="dingdingAuthConfig"
        :corp-id="dingdingAuthConfig.corpId"
        :client-id="dingdingAuthConfig.clientId"
        class="mb-3"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
const dingdingAuthConfig = {
  corpId: import.meta.env.VITE_GLOB_AUTH_DINGDING_CORP_ID ?? '',
  clientId: import.meta.env.VITE_GLOB_AUTH_DINGDING_CLIENT_ID ?? '',
};
import { SvgGithubIcon, SvgGoogleIcon, SvgQQChatIcon, SvgWeChatIcon } from '@/assets/icons';

import { $t } from '@/plugins/locale/index.ts';

import { VbenIconButton } from '@/plugins/vben-ui/shadcn-ui/index.ts';

import DingdingLogin from './dingding-login.vue';

defineOptions({
  name: 'ThirdPartyLogin',
});
</script>
