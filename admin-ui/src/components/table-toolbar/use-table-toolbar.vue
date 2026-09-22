<template>
  <VbenButtonGroup class="management-toolbar" size="small" :gap="8">
    <VbenButton
      v-for="button in buttons"
      :key="button.code"
      type="button"
      variant="outline"
      :class="button.class"
      :disabled="button.disabled"
      :loading="button.loading"
      @click="emit(button.code)"
    >
      <IconifyIcon
        v-if="!button.loading"
        :icon="button.icon"
        class="size-3.5 shrink-0"
        aria-hidden="true"
      />
      <span>{{ button.text }}</span>
    </VbenButton>

    <!-- 页面需要特殊操作时，可以追加按钮 -->
    <slot />
  </VbenButtonGroup>
</template>
<script setup lang="ts">
import { computed } from 'vue';

import { IconifyIcon } from '@/assets/icons';
import { $t } from '@/plugins/locale';
import { VbenButton, VbenButtonGroup } from '@/plugins/vben-ui/shadcn-ui';

type ToolbarAction = 'create' | 'edit' | 'delete' | 'import' | 'export';

const props = withDefaults(
  defineProps<{
    /** 显示哪些按钮，以及按钮顺序 */
    actions?: ToolbarAction[];
    /** 当前选中行数 */
    selectedCount?: number;
    /** 各按钮的加载状态 */
    loading?: Partial<Record<ToolbarAction, boolean>>;
  }>(),
  {
    actions: () => ['create', 'edit', 'delete', 'import', 'export'],
    selectedCount: 0,
    loading: () => ({}),
  },
);

const emit = defineEmits<{
  (event: 'create' | 'edit' | 'delete' | 'import' | 'export'): void;
}>();

const buttons = computed(() => {
  const presets = {
    create: {
      text: $t('common.create'),
      icon: 'lucide:plus',
      class:
        'border-primary/30 bg-primary/10 text-primary hover:border-primary/50 hover:bg-primary/20 hover:text-primary',
    },
    edit: {
      text: $t('common.edit'),
      icon: 'lucide:square-pen',
      class:
        'border-emerald-500/30 bg-emerald-500/10 text-emerald-600 hover:border-emerald-500/50 hover:bg-emerald-500/20 hover:text-emerald-600 dark:text-emerald-400 dark:hover:text-emerald-400',
    },
    delete: {
      text: $t('common.delete'),
      icon: 'lucide:trash-2',
      class:
        'border-red-500/30 bg-red-500/10 text-red-600 hover:border-red-500/50 hover:bg-red-500/20 hover:text-red-600 dark:text-red-400 dark:hover:text-red-400',
    },
    import: {
      text: $t('common.import'),
      icon: 'lucide:upload',
      class: 'border-border bg-muted/50 text-muted-foreground hover:bg-muted hover:text-foreground',
    },
    export: {
      text: $t('common.export'),
      icon: 'lucide:download',
      class:
        'border-amber-500/30 bg-amber-500/10 text-amber-600 hover:border-amber-500/50 hover:bg-amber-500/20 hover:text-amber-600 dark:text-amber-400 dark:hover:text-amber-400',
    },
  };

  return props.actions.map((code) => ({
    code,
    ...presets[code],
    loading: props.loading[code] ?? false,
    disabled:
      code === 'edit'
        ? props.selectedCount !== 1
        : code === 'delete'
          ? props.selectedCount === 0
          : false,
  }));
});
</script>
