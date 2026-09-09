<script setup lang="ts">
import type { CheckboxRootEmits, CheckboxRootProps } from 'reka-ui';

import { computed, useId } from 'vue';

import { useForwardPropsEmits } from 'reka-ui';

import { Checkbox } from '../../ui/checkbox';

defineOptions({ inheritAttrs: false });

const props = defineProps<CheckboxRootProps & { id?: string; indeterminate?: boolean }>();

const emits = defineEmits<CheckboxRootEmits>();

const forwarded = useForwardPropsEmits(props, emits);

const generatedId = useId();
const id = computed(() => props.id ?? generatedId);
</script>

<template>
  <div class="flex items-center">
    <!-- blur 和表单 aria 属性必须落在实际可聚焦的控件上。 -->
    <Checkbox v-bind="{ ...forwarded, ...$attrs }" :id="id" />
    <label :for="id" class="ml-2 cursor-pointer text-sm"> <slot></slot> </label>
  </div>
</template>
