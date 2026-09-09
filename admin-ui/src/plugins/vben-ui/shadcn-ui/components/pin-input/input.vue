<script setup lang="ts">
import type { PinInputProps } from './types';

import { computed, onBeforeUnmount, ref, useAttrs, useId, watch } from 'vue';

import { $t } from '@/plugins/locale';

import { PinInput, PinInputGroup, PinInputSlot } from '../../ui';
import { VbenButton } from '../button';

defineOptions({
  inheritAttrs: false,
});

const {
  class: className,
  codeLength = 6,
  createText,
  disabled = false,
  handleSendCode = async () => {},
  id: providedId,
  loading = false,
  maxTime = 60,
} = defineProps<PinInputProps>();

const emit = defineEmits<{
  blur: [event: FocusEvent];
  complete: [];
  sendError: [error: unknown];
}>();

const timer = ref<ReturnType<typeof setTimeout>>();

const modelValue = defineModel<string>();

const inputValue = ref<string[]>([]);
const countdown = ref(0);

const btnText = computed(() => {
  const countdownValue = countdown.value;
  if (createText) {
    return createText(countdownValue);
  }
  return countdownValue > 0
    ? $t('authentication.sendText', [countdownValue])
    : $t('authentication.sendCode');
});

const btnLoading = computed(() => {
  return loading || countdown.value > 0;
});

watch(
  () => modelValue.value,
  () => {
    inputValue.value = modelValue.value?.split('') ?? [];
  },
  { immediate: true },
);

watch(inputValue, (val) => {
  modelValue.value = val.join('');
});

function handleComplete(e: string[]) {
  modelValue.value = e.join('');
  emit('complete');
}

function handleFocusout(event: FocusEvent) {
  const group = event.currentTarget;
  // 单元格之间移动焦点不算整个验证码控件失焦。
  if (
    group instanceof HTMLElement &&
    event.relatedTarget instanceof Node &&
    group.contains(event.relatedTarget)
  ) {
    return;
  }
  emit('blur', event);
}

async function handleSend(e: Event) {
  try {
    e?.preventDefault();
    await handleSendCode();
    countdown.value = maxTime;
    startCountdown();
  } catch (error) {
    console.error('Failed to send code:', error);
    // Consider emitting an error event or showing a notification
    emit('sendError', error);
  }
}

function startCountdown() {
  if (countdown.value > 0) {
    timer.value = setTimeout(() => {
      countdown.value--;
      startCountdown();
    }, 1000);
  }
}

onBeforeUnmount(() => {
  countdown.value = 0;
  clearTimeout(timer.value);
});

const generatedId = useId();
const id = computed(() => providedId ?? generatedId);
const attrs = useAttrs();

function getInputAria() {
  return {
    'aria-describedby':
      typeof attrs['aria-describedby'] === 'string' ? attrs['aria-describedby'] : undefined,
    'aria-invalid': attrs['aria-invalid'] === true || attrs['aria-invalid'] === 'true',
  };
}

const pinType = 'text' as const;
</script>

<template>
  <PinInput
    v-bind="$attrs"
    :id="id"
    v-model="inputValue"
    :disabled="disabled"
    :name="name"
    :required="required"
    :class="className"
    class="flex w-full justify-between"
    otp
    placeholder="○"
    :type="pinType"
    @complete="handleComplete"
    @focusout="handleFocusout"
  >
    <div class="relative flex w-full">
      <PinInputGroup class="mr-2">
        <PinInputSlot
          v-for="(item, index) in codeLength"
          :key="item"
          :index="index"
          v-bind="getInputAria()"
        />
      </PinInputGroup>
      <VbenButton
        :disabled="disabled"
        :loading="btnLoading"
        class="grow"
        size="lg"
        variant="outline"
        @click="handleSend"
      >
        {{ btnText }}
      </VbenButton>
    </div>
  </PinInput>
</template>
