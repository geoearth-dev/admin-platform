<script lang="ts" setup>
import type { SelectRootProps } from 'reka-ui';

import { CircleX } from '@/assets/icons';

import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../../ui';
import type { ClassType } from '@/types';

interface Props extends Pick<SelectRootProps<string>, 'disabled' | 'name' | 'required'> {
  allowClear?: boolean;
  class?: ClassType;
  options?: Array<{ label: string; value: string }>;
  placeholder?: string;
}

defineOptions({ inheritAttrs: false });

const props = withDefaults(defineProps<Props>(), {
  allowClear: false,
});

const modelValue = defineModel<string>();

function handleClear() {
  if (props.disabled) {
    return;
  }
  modelValue.value = undefined;
}
</script>
<template>
  <Select v-model="modelValue" :disabled="disabled" :name="name" :required="required">
    <SelectTrigger v-bind="$attrs" :class="props.class" class="flex w-full items-center">
      <SelectValue class="flex-auto text-left" :placeholder="placeholder" />
      <CircleX
        @pointerdown.stop
        @click.stop.prevent="handleClear"
        v-if="allowClear && modelValue && !disabled"
        data-clear-button
        class="mr-1 size-4 cursor-pointer opacity-50 hover:opacity-100"
      />
    </SelectTrigger>
    <SelectContent>
      <template v-for="item in options" :key="item.value">
        <SelectItem :value="item.value"> {{ item.label }} </SelectItem>
      </template>
    </SelectContent>
  </Select>
</template>

<style lang="scss" scoped>
button[role='combobox'][data-placeholder] {
  color: hsl(var(--muted-foreground));
}

button {
  --ring: var(--primary);
}
</style>
