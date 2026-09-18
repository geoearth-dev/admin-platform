<template>
  <Drawer
    :title="title"
    class="w-[640px] max-w-full"
    content-class="flex min-h-0 flex-col overflow-hidden"
  >
    <div class="shrink-0">
      <Form />
    </div>
    <div class="mt-4 flex min-h-0 flex-1 flex-col gap-3">
      <div class="flex shrink-0 items-center justify-between text-sm">
        <span>{{ $t('system.role.setPermissions') }}</span>
        <label class="flex items-center gap-2">
          <input v-model="menuCheckLinked" type="checkbox" :disabled="busy" />
          {{ $t('system.role.linked') }}
        </label>
      </div>
      <Tree
        v-model="menuIds"
        class="min-h-0 flex-1 overflow-y-auto overscroll-contain"
        :tree-data="permissions"
        multiple
        bordered
        :check-strictly="!menuCheckLinked"
        :auto-check-parent="false"
        :disabled="busy"
        :default-expanded-level="0"
        :expand-all-label="$t('system.role.expandAll')"
        :collapse-all-label="$t('system.role.collapseAll')"
        :select-all-label="$t('system.role.selectAll')"
        value-field="id"
        label-field="label"
      />
    </div>
  </Drawer>
</template>
<script setup lang="ts">
import { computed, nextTick, ref } from 'vue';
import { ElMessage } from 'element-plus';
import {
  addRole,
  getRole,
  getRoleMenuTree,
  roleMenuTreeSelect,
  updateRole,
} from '@/api/system/role';
import { Tree } from '@/components/tree';
import { $t } from '@/plugins/locale';
import { useVbenForm } from '@/plugins/vben-ui/form-ui';
import { useVbenDrawer } from '@/plugins/vben-ui/popup-ui';
import type { TreeSelect } from '@/types/base/api/common';
import type { RoleSaveParams, SysRole } from '@/types/base/api/system/role';
import { useFormSchema } from '../data';
import { restoreChecked, withParents } from './tree-selection';

const emit = defineEmits<{ success: [] }>();
const formData = ref<SysRole>();
const permissions = ref<TreeSelect[]>([]);
const menuIds = ref<number[]>([]);
const menuCheckLinked = ref(true);
const busy = ref(false);
const [Form, formApi] = useVbenForm({
  schema: useFormSchema(),
  showDefaultActions: false,
});
const title = computed(() =>
  $t(formData.value ? 'system.role.editTitle' : 'system.role.createTitle'),
);

const [Drawer, drawerApi] = useVbenDrawer({
  async onOpenChange(open) {
    if (!open) return;
    const { id } = drawerApi.getData<{ id?: number }>();
    formData.value = undefined;
    permissions.value = [];
    menuIds.value = [];
    menuCheckLinked.value = true;
    busy.value = true;
    drawerApi.lock();
    try {
      if (id != null) {
        const [role, tree] = await Promise.all([getRole(id), getRoleMenuTree(id)]);
        formData.value = role;
        permissions.value = tree.menus;
        menuCheckLinked.value = role.menuCheckLinked ?? true;
        menuIds.value = menuCheckLinked.value
          ? restoreChecked(tree.menus, tree.checkedKeys)
          : tree.checkedKeys;
      } else {
        permissions.value = await roleMenuTreeSelect();
      }
      await nextTick();
      await formApi.reset();
      await formApi.setValues({
        roleName: formData.value?.roleName ?? '',
        roleKey: formData.value?.roleKey ?? '',
        roleSort: formData.value?.roleSort ?? 0,
        status: formData.value?.status ?? '1',
        remark: formData.value?.remark ?? '',
      });
    } catch {
      await drawerApi.close();
    } finally {
      busy.value = false;
      drawerApi.unlock();
    }
  },
  async onConfirm() {
    if (busy.value) return;
    busy.value = true;
    try {
      const { valid } = await formApi.validate();
      if (!valid) return;
      drawerApi.lock();
      const values = await formApi.getValues<RoleSaveParams & Record<string, unknown>>();
      const payload: RoleSaveParams = {
        id: formData.value?.id,
        roleName: values.roleName,
        roleKey: values.roleKey,
        roleSort: values.roleSort,
        status: values.status,
        remark: values.remark,
        menuCheckLinked: menuCheckLinked.value,
        menuIds: menuCheckLinked.value
          ? withParents(permissions.value, menuIds.value)
          : menuIds.value,
        // 普通修改保留现有数据范围，数据权限由独立接口保存。
        dataScope: formData.value?.dataScope,
        deptCheckLinked: formData.value?.deptCheckLinked ?? true,
      };
      if (formData.value) await updateRole(payload);
      else await addRole(payload);
      ElMessage.success($t('system.role.saveSuccess'));
      emit('success');
      await drawerApi.close();
    } catch {
      // 请求层已提示；保留输入供修改或重试。
    } finally {
      busy.value = false;
      drawerApi.unlock();
    }
  },
});
</script>
