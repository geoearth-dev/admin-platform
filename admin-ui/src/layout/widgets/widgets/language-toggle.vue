<script setup lang="ts">
import type { SupportedLanguagesType } from '@/plugins/locale';

import { SUPPORT_LANGUAGES } from '@vben/constants';
import { Languages } from '@/assets/icons';
import { loadLocaleMessages } from '@/plugins/locale';
import { preferences, updatePreferences } from '@vben/preferences';

import { VbenDropdownRadioMenu, VbenIconButton } from '@/plugins/vben-ui/shadcn-ui';

defineOptions({
  name: 'LanguageToggle',
});

async function handleUpdate(value: string | undefined) {
  if (!value) return;
  const locale = value as SupportedLanguagesType;
  updatePreferences({
    app: {
      locale,
    },
  });
  await loadLocaleMessages(locale);
}
</script>

<template>
  <div>
    <VbenDropdownRadioMenu
      :menus="SUPPORT_LANGUAGES"
      :model-value="preferences.app.locale"
      @update:model-value="handleUpdate"
    >
      <VbenIconButton class="hover:animate-[shrink_0.3s_ease-in-out]">
        <Languages class="size-4 text-foreground" />
      </VbenIconButton>
    </VbenDropdownRadioMenu>
  </div>
</template>
