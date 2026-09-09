<script setup lang="ts">
import { ref, unref } from 'vue';

import { createIconifyIcon } from '@/assets/icons';
import { $t } from '@/plugins/locale';

import { RadioGroup, RadioGroupItem, VbenIconButton } from '@/plugins/vben-ui/shadcn-ui';
import { useTimezoneStore } from '@/store';
import { useVbenModal } from '@/plugins/vben-ui/popup-ui';

withDefaults(defineProps<{ showButton?: boolean }>(), { showButton: true });

const TimezoneIcon = createIconifyIcon('fluent-mdl2:world-clock');

const timezoneStore = useTimezoneStore();

const timezoneRef = ref<string | undefined>();

const timezoneOptionsRef = ref<
  {
    label: string;
    value: string;
  }[]
>([]);

const [Modal, modalApi] = useVbenModal({
  fullscreenButton: false,
  onConfirm: async () => {
    try {
      modalApi.setState({ confirmLoading: true });
      const timezone = unref(timezoneRef);
      if (timezone) {
        await timezoneStore.setTimezone(timezone);
      }
      modalApi.close();
    } finally {
      modalApi.setState({ confirmLoading: false });
    }
  },
  async onOpenChange(isOpen) {
    if (isOpen) {
      timezoneRef.value = unref(timezoneStore.timezone);
      timezoneOptionsRef.value = await timezoneStore.getTimezoneOptions();
    }
  },
});

function open() {
  modalApi.open();
}

defineExpose({ open });
</script>

<template>
  <div>
    <VbenIconButton
      v-if="showButton"
      :tooltip="$t('ui.widgets.timezone.setTimezone')"
      class="hover:animate-[shrink_0.3s_ease-in-out]"
      @click="open"
    >
      <TimezoneIcon class="size-4 text-foreground" />
    </VbenIconButton>
    <Modal :title="$t('ui.widgets.timezone.setTimezone')">
      <div class="timezone-container">
        <RadioGroup v-model="timezoneRef" class="flex flex-col gap-2">
          <div
            class="flex cursor-pointer items-center gap-2"
            v-for="item in timezoneOptionsRef"
            :key="`container${item.value}`"
          >
            <RadioGroupItem :id="item.value" :value="item.value" />
            <label :for="item.value" class="cursor-pointer">{{ item.label }}</label>
          </div>
        </RadioGroup>
      </div>
    </Modal>
  </div>
</template>

<style scoped lang="scss">
.timezone-container {
  @apply pl-5;
}
</style>
