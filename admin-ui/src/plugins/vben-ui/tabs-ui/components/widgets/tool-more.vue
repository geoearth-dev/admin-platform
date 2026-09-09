<script lang="ts" setup>
import { LayoutGrid } from '@/assets/icons';
import type { DropdownMenuProps } from '@/plugins/vben-ui/shadcn-ui';
import { VbenDropdownMenu } from '@/plugins/vben-ui/shadcn-ui';
defineProps<DropdownMenuProps>();
</script>

<template>
  <VbenDropdownMenu :menus="menus" :modal="false">
    <div
      class="flex-center h-full cursor-pointer border-l border-border px-2 text-lg font-semibold text-muted-foreground hover:bg-muted hover:text-foreground"
    >
      <LayoutGrid class="size-4" />
    </div>
  </VbenDropdownMenu>
</template>
