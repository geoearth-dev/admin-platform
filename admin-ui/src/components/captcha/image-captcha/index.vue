<template>
  <div class="flex w-full items-center gap-3">
    <div class="relative min-w-0 flex-1">
      <Input
        v-bind="$attrs"
        v-model="modelValue"
        :disabled="disabled"
        :placeholder="placeholder || $t('authentication.code')"
        autocomplete="off"
        class="pl-9"
      />
      <ShieldCheck
        class="text-muted-foreground/50 pointer-events-none absolute left-3 top-1/2 size-4 -translate-y-1/2"
      />
    </div>
    <button
      type="button"
      class="border-input bg-muted/40 text-muted-foreground hover:border-primary/50 hover:text-primary focus-visible:ring-ring relative flex h-10 w-28 shrink-0 cursor-pointer items-center justify-center gap-1.5 overflow-hidden rounded-md border text-xs transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-60"
      :disabled="disabled || loading"
      :aria-label="imageTip"
      :aria-busy="imageLoading"
      :title="imageTip"
      @click="refresh"
      @keydown.enter.stop
    >
      <img
        v-if="src"
        v-show="imageLoaded && !loading && !imageError"
        :key="`${src}:${attempt}`"
        :src="src"
        :alt="$t('authentication.code')"
        class="absolute inset-0 h-full w-full object-contain"
        @load="imageLoaded = true"
        @error="imageError = true"
      />
      <LoaderCircle v-if="imageLoading" class="size-4 animate-spin" aria-hidden="true" />
      <ImageOff v-else-if="!src || imageError" class="size-5" aria-hidden="true" />
    </button>
  </div>
</template>
<script setup lang="ts">
import { computed, ref, watch } from 'vue';

import { ImageOff, LoaderCircle, ShieldCheck } from '@/assets/icons';
import { $t } from '@/plugins/locale';
import { Input } from '@/plugins/vben-ui/shadcn-ui';

defineOptions({ name: 'ImageCaptcha', inheritAttrs: false });

const props = withDefaults(
  defineProps<{
    disabled?: boolean;
    loading?: boolean;
    placeholder?: string;
    src?: string;
  }>(),
  { disabled: false, loading: false, placeholder: '', src: '' },
);

const modelValue = defineModel<string>({ default: '' });
const emit = defineEmits<{ refresh: [] }>();
const imageLoaded = ref(false);
const imageError = ref(false);
const attempt = ref(0);
const imageLoading = computed(
  () => props.loading || Boolean(props.src && !imageLoaded.value && !imageError.value),
);
const imageTip = computed(() =>
  $t(!imageLoading.value && (!props.src || imageError.value)
    ? 'ui.captcha.imageLoadFailed'
    : 'ui.captcha.refreshAriaLabel'),
);

watch(() => props.src, resetImage);

function resetImage() {
  imageLoaded.value = false;
  imageError.value = false;
}

function refresh() {
  if (props.disabled || props.loading) return;
  // 即使地址不变，重试时也重新加载图片。
  resetImage();
  attempt.value += 1;
  emit('refresh');
}
</script>
