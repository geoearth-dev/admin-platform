<script setup lang="ts">
import { computed } from 'vue'
import { useNow } from '@vueuse/core'
import { useI18n } from '@/plugins/locale'
import { preferences } from '@/plugins/preference'
import { useUserStore } from '@/store'
import { VbenAvatar } from '@/plugins/vben-ui/shadcn-ui'
import { resolveFileUrl } from '@/api/common/file'
const props = defineProps<{ analytics?: boolean }>()
const { t } = useI18n()
const userStore = useUserStore()
const now = useNow({ interval: 60000 })
const name = computed(
  () =>
    userStore.userInfo?.nickName ||
    userStore.userInfo?.username ||
    t('dashboardDemo.admin'),
)
const greeting = computed(() =>
  t(
    `dashboardDemo.${now.value.getHours() < 12 ? 'morning' : now.value.getHours() < 18 ? 'afternoon' : 'evening'}`,
  ),
)
</script>
<template>
  <header
    class="demo-hero"
    :class="{ 'demo-hero-analytics': props.analytics }"
  >
    <div class="demo-hero-intro">
      <div class="demo-hero-title">
        <VbenAvatar
          :src="
            resolveFileUrl(userStore.userInfo?.avatar ?? '') ||
            preferences.app.defaultAvatar
          "
          :alt="name"
          :size="38"
          class="demo-user-avatar"
        />
        <h1 v-if="!analytics">
          {{ t('dashboardDemo.greeting', { greeting, name }) }}
          <span class="demo-wave">👋</span>
        </h1>
        <h1 v-else>{{ t('dashboardDemo.hello', { name }) }} 👋</h1>
      </div>
      <p>
        {{
          t(
            analytics
              ? 'dashboardDemo.analyticsIntro'
              : 'dashboardDemo.workspaceIntro',
          )
        }}
      </p>
      <div
        v-if="!analytics"
        class="demo-hero-meta"
      >
        <span>{{ t('dashboardDemo.weather') }}</span
        ><span class="demo-data-label">{{ t('dashboardDemo.sample') }}</span>
      </div>
      <span
        v-else
        class="demo-data-label"
        >{{ t('dashboardDemo.sample') }}</span
      >
    </div>
    <div
      v-if="!analytics"
      class="demo-hero-quote"
    >
      <span>{{ t('dashboardDemo.quoteOne') }}</span
      ><span>{{ t('dashboardDemo.quoteTwo') }}</span>
    </div>
    <div class="demo-hero-motto">
      {{
        t(
          analytics
            ? 'dashboardDemo.analyticsMotto'
            : 'dashboardDemo.workspaceMotto',
        )
      }}
    </div>
    <div
      v-if="$slots.controls"
      class="demo-hero-controls"
    >
      <slot name="controls" />
    </div>
  </header>
</template>
