<script setup lang="ts">
import type { SelectOption } from '@/types';

import { computed, nextTick } from 'vue';

import { GripVertical } from '@/assets/icons';
import { $t } from '@/plugins/locale';

import Draggable from 'vuedraggable';

import SelectItem from './select-item.vue';

interface Item {
  key: string;
  label: string;
  position: 'auto' | 'fixed' | 'header' | 'none' | 'user-dropdown';
  /** 每项可选的 position 选项；不传则用 props.positionItems */
  positionItems?: SelectOption[];
}

const props = defineProps<{
  items: Item[];
  positionItems: SelectOption[];
}>();

const emit = defineEmits<{
  updateOrder: [keys: string[]];
  updatePosition: [key: string, position: 'auto' | 'fixed' | 'header' | 'none' | 'user-dropdown'];
}>();

// 划分规则：
// - hidden：position === 'none'（不显示）
// - sortable：其它（含 auto/fixed，偏好按钮的智能模式视觉归入顶栏组参与排序，
//   避免 preferences 从 widget.order 丢失）
const sortableList = computed(() => props.items.filter((item) => item.position !== 'none'));

const hiddenList = computed(() => props.items.filter((item) => item.position === 'none'));

function updateSortableList(items: Item[]) {
  emit('updateOrder', [
    ...items.map((item) => item.key),
    ...hiddenList.value.map((item) => item.key),
  ]);
}
function setPosition(key: string, value: string | undefined) {
  if (!value) return;
  emit('updatePosition', key, value as 'auto' | 'fixed' | 'header' | 'none' | 'user-dropdown');
  nextTick(() => {
    emit(
      'updateOrder',
      [...sortableList.value, ...hiddenList.value].map((i) => i.key),
    );
  });
}
</script>

<template>
  <div class="space-y-1">
    <Draggable
      :model-value="sortableList"
      item-key="key"
      handle=".drag-handle"
      :animation="200"
      class="space-y-1"
      @update:model-value="updateSortableList"
    >
      <template #item="{ element: item }">
        <div class="bg-accent flex items-center rounded-md pl-2">
          <GripVertical
            class="drag-handle size-4 shrink-0 cursor-grab text-muted-foreground active:cursor-grabbing"
          />
          <SelectItem
            :items="item.positionItems ?? positionItems"
            :model-value="item.position"
            class="min-w-0 flex-1"
            @update:model-value="(value: string | undefined) => setPosition(item.key, value)"
          >
            <span class="truncate">{{ item.label }}</span>
          </SelectItem>
        </div>
      </template>
    </Draggable>
    <div v-if="hiddenList.length > 0" class="pt-2">
      <div class="text-muted-foreground mb-1 text-xs font-medium">
        {{ $t('preferences.widget.hidden') }}
      </div>
      <div v-for="item in hiddenList" :key="item.key" class="flex items-center rounded-md">
        <SelectItem
          :items="item.positionItems ?? positionItems"
          :model-value="item.position"
          class="min-w-0 flex-1"
          @update:model-value="(v: string | undefined) => setPosition(item.key, v)"
        >
          <span class="text-muted-foreground truncate line-through decoration-dotted">
            {{ item.label }}
          </span>
        </SelectItem>
      </div>
    </div>
  </div>
</template>
