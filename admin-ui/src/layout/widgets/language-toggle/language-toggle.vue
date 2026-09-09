<script setup lang="ts">
import { Languages } from '@/assets/icons';

import { VbenDropdownRadioMenu, VbenIconButton } from '@/plugins/vben-ui/shadcn-ui';
import { loadLocaleMessages, type SupportedLanguagesType } from '@/plugins/locale';
import { preferences, updatePreferences } from '@/plugins/preference';
import { SUPPORT_LANGUAGES } from '@/constants/core';

defineOptions({
  name: 'LanguageToggle',
});

async function handleUpdate(value: string | undefined) {
  if (!value) return;
  const locale = value as SupportedLanguagesType;
  updatePreferences({
    app: { locale },
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
