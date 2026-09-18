<template>
  <ElConfigProvider :locale="elementLocale">
    <RouterView />
  </ElConfigProvider>
</template>
<script setup lang="ts">
import { ElConfigProvider } from 'element-plus';
import { elementLocale } from '@/plugins/locale';
import { useElementPlusDesignTokens } from './plugins/effects/hooks/use-design-tokens';
import { useAccessStore, useAuthStore } from './store';
import { computed, onBeforeUnmount, watch } from 'vue';
import { startOnlineStream } from './utils/online-stream';
useElementPlusDesignTokens();

const authStore = useAuthStore();
const accessStore = useAccessStore();

const onlineSessionId = computed(
  () =>
    // authStore.isAuthenticated && authStore.sessionInitialized ? accessStore.sessionId : null,
    null,
);

let stopStream: (() => void) | undefined;

function stopOnlineStream() {
  stopStream?.();
  stopStream = undefined;
}

function syncOnlineStream() {
  stopOnlineStream();

  const sessionId = onlineSessionId.value;
  if (!sessionId) return;

  stopStream = startOnlineStream({
    sessionId,
    getToken: () => accessStore.accessToken,
    isCurrent: () => onlineSessionId.value === sessionId,
    refreshToken: () => authStore.refreshAccessToken(),
    onInvalidated: () => authStore.handleSessionExpired(),
  });
}

watch(onlineSessionId, syncOnlineStream, { immediate: true });

window.addEventListener('pagehide', stopOnlineStream);
window.addEventListener('pageshow', syncOnlineStream);

onBeforeUnmount(() => {
  stopOnlineStream();
  window.removeEventListener('pagehide', stopOnlineStream);
  window.removeEventListener('pageshow', syncOnlineStream);
});
</script>
