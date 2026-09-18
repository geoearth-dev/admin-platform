<template>
  <Modal :title="$t('system.user.assignRoles')" class="w-[600px] max-w-[95vw]">
    <Form />
  </Modal>
</template>
<script setup lang="ts">
import { nextTick, ref } from 'vue';
import { ElMessage } from 'element-plus';

import { getAuthRole, updateAuthRole } from '@/api/system/user';
import { $t } from '@/plugins/locale';
import { useVbenForm } from '@/plugins/vben-ui/form-ui';
import { useVbenModal } from '@/plugins/vben-ui/popup-ui';

const emit = defineEmits<{ success: [] }>();
const userId = ref<number>();
const submitting = ref(false);

const [Form, formApi] = useVbenForm({
  showDefaultActions: false,
  schema: [
    {
      component: 'CheckboxGroup',
      fieldName: 'roleIds',
      label: $t('system.user.roles'),
      defaultValue: [],
      componentProps: { options: [] },
    },
  ],
});

const [Modal, modalApi] = useVbenModal({
  fullscreenButton: false,

  async onOpenChange(open) {
    if (!open) return;

    userId.value = modalApi.getData<{ id: number }>().id;
    modalApi.lock();

    try {
      const roles = await getAuthRole(userId.value);

      formApi.updateSchema([
        {
          fieldName: 'roleIds',
          componentProps: {
            options: roles.map((role) => ({
              label: role.roleName,
              value: role.id,
              disabled: role.status !== '1',
            })),
          },
        },
      ]);

      await nextTick();
      await formApi.reset();
      await formApi.setValues({
        roleIds: roles.filter((role) => role.status).map((role) => role.id),
      });
    } catch {
      modalApi.close();
    } finally {
      modalApi.unlock();
    }
  },

  async onConfirm() {
    if (userId.value == null || submitting.value) return;

    const values = await formApi.getValues<{
      roleIds?: number[];
    }>();

    submitting.value = true;
    modalApi.lock();

    try {
      await updateAuthRole(
        userId.value,
        // 空数组表示清空已分配角色。
        values.roleIds ?? [],
      );

      ElMessage.success($t('ui.actionMessage.operationSuccess'));
      emit('success');
      modalApi.close();
    } catch {
      // 请求层已经提示。
    } finally {
      submitting.value = false;
      modalApi.unlock();
    }
  },
});
</script>
