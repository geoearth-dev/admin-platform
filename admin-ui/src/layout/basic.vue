<template>
  <Layout
    :avatar
    :text="userStore.userInfo?.nickName"
    @clear-preferences-and-logout="handleLogout"
    @logout="handleLogout"
  >
    <template #user-dropdown>
      <UserDropdown
        :avatar
        :menus
        :text="userStore.userInfo?.nickName"
        description="ann.vben@gmail.com"
        tag-text="Pro"
        @clear-preferences-and-logout="handleLogout"
        @logout="handleLogout"
      />
    </template>
    <template #notification>
      <Notification />
    </template>
    <template #extra>
      <AuthenticationLoginExpiredModal
        v-model:open="accessStore.loginExpired"
        :avatar
      >
        <LoginForm />
      </AuthenticationLoginExpiredModal>
    </template>
    <template #lock-screen>
      <LockScreen
        :avatar
        @to-login="handleLogout"
      />
    </template>
  </Layout>
</template>

<script lang="ts" setup>
import { computed, watch } from 'vue'
import { useRouter } from 'vue-router'

import { AuthenticationLoginExpiredModal } from '@/components/authentication'
import { BookOpenText, CircleHelp, SvgGithubIcon } from '@/assets/icons'
import { Layout, LockScreen, Notification, UserDropdown } from '@/layout'
import { preferences, usePreferences } from '@/plugins/preference'
import { useAccessStore, useUserStore, useAuthStore } from '@/store'

import LoginForm from '@/views/sys/admin/authentication/login.vue'
import { VBEN_DOC_URL, VBEN_GITHUB_URL } from '@/constants/vben'
import { useWatermark } from '@/plugins/effects/hooks/use-watermark'
import { $t } from '@/plugins/locale'
import { openWindow } from '@/utils/window'

const router = useRouter()
const userStore = useUserStore()
const authStore = useAuthStore()
const accessStore = useAccessStore()
const { destroyWatermark, updateWatermark } = useWatermark()
const { isDark } = usePreferences()

const menus = computed(() => [
  {
    handler: () => {
      router.push({ name: 'Profile' })
    },
    icon: 'lucide:user',
    text: $t('page.auth.profile'),
  },
  {
    handler: () => {
      openWindow(VBEN_DOC_URL, {
        target: '_blank',
      })
    },
    icon: BookOpenText,
    text: $t('ui.widgets.document'),
  },
  {
    handler: () => {
      openWindow(VBEN_GITHUB_URL, {
        target: '_blank',
      })
    },
    icon: SvgGithubIcon,
    text: 'GitHub',
  },
  {
    handler: () => {
      openWindow(`${VBEN_GITHUB_URL}/issues`, {
        target: '_blank',
      })
    },
    icon: CircleHelp,
    text: $t('ui.widgets.qa'),
  },
])

const avatar = computed(
  () => userStore.userInfo?.avatar || preferences.app.defaultAvatar,
)

async function handleLogout() {
  await authStore.logout(false)
}

watch(
  () => ({
    enable: preferences.app.watermark,
    content: preferences.app.watermarkContent,
    isDark: isDark.value,
  }),
  async ({ enable, content, isDark: isDarkValue }) => {
    if (enable) {
      const watermarkColor = isDarkValue
        ? 'rgba(255, 255, 255, 0.12)'
        : 'rgba(0, 0, 0, 0.12)'

      await updateWatermark({
        advancedStyle: {
          colorStops: [
            {
              color: watermarkColor,
              offset: 0,
            },
            {
              color: watermarkColor,
              offset: 1,
            },
          ],
          type: 'linear',
        },
        content:
          content ||
          `${userStore.userInfo?.username} - ${userStore.userInfo?.nickName}`,
      })
    } else {
      destroyWatermark()
    }
  },
  {
    immediate: true,
  },
)
</script>
