<script lang="ts" setup>
import type { TreeProps } from '@/plugins/vben-ui/shadcn-ui';

import { computed, nextTick, ref } from 'vue';

import { Tree } from '@/components/tree';

import { VbenSpinner } from '@/plugins/vben-ui/shadcn-ui';

import { addUser, updateUser } from '@/api/system/user';
import { $t } from '@/plugins/locale';

import { useFormSchema } from '../data';
import type { Recordable } from '@/types';
import { useVbenDrawer } from '@/plugins/vben-ui/popup-ui';
import { useVbenForm } from '@/plugins/vben-ui/form-ui';
import type { SysUser } from '@/types/base/api/system/user';
import { listMenu } from '@/api/admin/menu';

const emits = defineEmits(['success']);

const formData = ref<SysUser>();

const [Form, formApi] = useVbenForm({
  schema: useFormSchema(),
  showDefaultActions: false,
});

const permissions = ref<TreeProps['treeData']>([]);
const loadingPermissions = ref(false);
type UserFormValues = SysUser & Recordable<unknown>;
const id = ref<SysUser['id']>();
const [Drawer, drawerApi] = useVbenDrawer({
  async onConfirm() {
    const { valid } = await formApi.validate();
    if (!valid) return;
    const values = await formApi.getValues<UserFormValues>();
    drawerApi.lock();
    (id.value ? updateUser({ ...values, id: id.value }) : addUser(values))
      .then(() => {
        emits('success');
        drawerApi.close();
      })
      .catch(() => {
        drawerApi.unlock();
      });
  },

  async onOpenChange(isOpen: boolean) {
    if (isOpen) {
      const data = drawerApi.getData<SysUser>();
      formApi.reset();

      if (data) {
        formData.value = data;
        id.value = data.id;
      } else {
        id.value = undefined;
      }

      if (permissions.value.length === 0) {
        await loadPermissions();
      }
      // Wait for Vue to flush DOM updates (form fields mounted)
      await nextTick();
      if (data) {
        await formApi.setValues({ ...formData.value });
      }
    }
  },
});

async function loadPermissions() {
  loadingPermissions.value = true;
  try {
    const res = await listMenu();
    permissions.value = res as unknown as TreeProps['treeData'];
  } finally {
    loadingPermissions.value = false;
  }
}

const getDrawerTitle = computed(() => {
  return formData.value?.id
    ? $t('common.edit', $t('system.user.name'))
    : $t('common.create', $t('system.user.name'));
});

const getNodeClass: NonNullable<TreeProps['getNodeClass']> = (node) => {
  const classes: string[] = [];
  if (node.value.type === 'button') {
    classes.push('inline-flex');
  }

  return classes.join(' ');
};
</script>
<template>
  <Drawer :title="getDrawerTitle">
    <Form>
      <template #permissions="slotProps">
        <VbenSpinner :spinning="loadingPermissions" :classes="{ root: 'w-full' }">
          <Tree
            :tree-data="permissions"
            multiple
            bordered
            :default-expanded-level="2"
            :get-node-class="getNodeClass"
            v-bind="slotProps"
            value-field="id"
            label-field="name"
          />
        </VbenSpinner>
      </template>
    </Form>
  </Drawer>
</template>
