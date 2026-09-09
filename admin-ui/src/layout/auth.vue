<template>
  <AuthPageLayout
    :app-name="appName"
    :logo="logo"
    :logo-dark="logoDark"
    :page-description="$t('authentication.pageDesc')"
    :page-title="$t('authentication.pageTitle')"
  >
    <!-- 自定义工具栏 -->
    <!-- <template #toolbar></template> -->
  </AuthPageLayout>
</template>

<script lang="ts" setup>
import { computed } from 'vue';

import { AuthPageLayout } from './authentication';

import { $t } from '@/plugins/locale';
import { preferences } from '@/plugins/preference';

const appName = computed(() => preferences.app.name);
const logo = computed(() => preferences.logo.source);
const logoDark = computed(() => preferences.logo.sourceDark);
</script>
