<template>
  <Modal :footer="false" :fullscreen-button="false" :title="$t('ui.widgets.lockScreen.title')">
    <div
      class="mb-10 flex w-full flex-col items-center px-10"
      @keydown.enter.prevent="handleSubmit"
    >
      <div class="w-full">
        <div class="ml-2 flex w-full flex-col items-center">
          <VbenAvatar
            :src="avatar"
            class="size-20"
            dot-class="bottom-0 right-1 border-2 size-4 bg-green-500"
          />
          <div class="my-6 flex items-center font-medium text-foreground">
            {{ text }}
          </div>
        </div>
        <Form />
        <VbenButton class="mt-1 w-full" @click="handleSubmit">
          {{ $t('ui.widgets.lockScreen.screenButton') }}
        </VbenButton>
      </div>
    </div>
  </Modal>
</template>
<script setup lang="ts">
import { computed, reactive } from 'vue';

import { $t } from '@/plugins/locale';

import type { Recordable } from '@/types';

import { useVbenModal } from '@/plugins/vben-ui/popup-ui';
import { VbenAvatar, VbenButton } from '@/plugins/vben-ui/shadcn-ui';
import { useVbenForm, z } from '@/plugins/vben-ui/form-ui';

interface Props {
  avatar?: string;
  text?: string;
}

defineOptions({
  name: 'LockScreenModal',
});

withDefaults(defineProps<Props>(), {
  avatar: '',
  text: '',
});

const emit = defineEmits<{
  submit: [Recordable<unknown>];
}>();

const [Form, { resetForm, validate, getValues, getFieldComponentRef }] = useVbenForm(
  reactive({
    commonConfig: {
      hideLabel: true,
      hideRequiredMark: true,
    },
    schema: computed(() => [
      {
        component: 'VbenInputPassword' as const,
        componentProps: {
          placeholder: $t('ui.widgets.lockScreen.placeholder'),
        },
        fieldName: 'lockScreenPassword',
        formFieldProps: { validateOn: ['change'] as const },
        label: $t('authentication.password'),
        rules: z.string().min(1, { message: $t('ui.widgets.lockScreen.placeholder') }),
      },
    ]),
    showDefaultActions: false,
  }),
);

const [Modal] = useVbenModal({
  onConfirm() {
    handleSubmit();
  },
  onOpenChange(isOpen) {
    if (isOpen) {
      resetForm();
    }
  },
  onOpened() {
    requestAnimationFrame(() => {
      getFieldComponentRef('lockScreenPassword')
        ?.$el?.querySelector('[name="lockScreenPassword"]')
        ?.focus();
    });
  },
});

async function handleSubmit() {
  const { valid } = await validate();
  const values = await getValues();
  if (valid) {
    emit('submit', values?.lockScreenPassword);
  }
}
</script>
