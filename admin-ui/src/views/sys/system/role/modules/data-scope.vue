<template>
  <Drawer
    :title="$t('system.role.assignDataScope')"
    class="w-[640px] max-w-full"
    content-class="flex min-h-0 flex-col overflow-hidden"
  >
    <div class="shrink-0">
      <Form />
    </div>
    <div v-if="showDeptTree" class="mt-4 flex min-h-0 flex-1 flex-col gap-3">
      <div class="flex shrink-0 items-center justify-between text-sm">
        <span>{{ $t('system.role.dataScope') }}</span>
        <label class="flex items-center gap-2">
          <input v-model="deptCheckLinked" type="checkbox" :disabled="busy" />
          {{ $t('system.role.linked') }}
        </label>
      </div>
      <Tree
        v-model="deptIds"
        class="min-h-0 flex-1 overflow-y-auto overscroll-contain"
        :tree-data="departments"
        multiple
        bordered
        :check-strictly="!deptCheckLinked"
        :auto-check-parent="false"
        :disabled="busy"
        :default-expanded-level="Number.MAX_SAFE_INTEGER"
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
import { nextTick, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { dataScope, deptTreeSelect, getRole } from '@/api/system/role';
import { Tree } from '@/components/tree';
import { $t } from '@/plugins/locale';
import { useVbenForm } from '@/plugins/vben-ui/form-ui';
import { useVbenDrawer } from '@/plugins/vben-ui/popup-ui';
import type { TreeSelect } from '@/types/base/api/common';
import type { RoleDataScopeParams } from '@/types/base/api/system/role';
import { restoreChecked, withParents } from './tree-selection';

const emit = defineEmits<{ success: [] }>();
const id = ref<number>();
const departments = ref<TreeSelect[]>([]);
const deptIds = ref<number[]>([]);
const deptCheckLinked = ref(true);
const showDeptTree = ref(false);
const busy = ref(false);
const [Form, formApi] = useVbenForm({
  showDefaultActions: false,
  handleValuesChange(values) {
    showDeptTree.value = values.dataScope === '2';
  },
  schema: [
    {
      component: 'Input',
      fieldName: 'roleName',
      label: $t('system.role.roleName'),
      componentProps: { disabled: true },
    },
    {
      component: 'Input',
      fieldName: 'roleKey',
      label: $t('system.role.roleKey'),
      componentProps: { disabled: true },
    },
    {
      component: 'Select',
      fieldName: 'dataScope',
      label: $t('system.role.permissionScope'),
      rules: 'required',
      componentProps: {
        options: ['1', '2', '3', '4', '5'].map((value) => ({
          value,
          label: $t('system.role.scope' + value),
        })),
      },
    },
  ],
});

const [Drawer, drawerApi] = useVbenDrawer({
  async onOpenChange(open) {
    if (!open) return;
    id.value = drawerApi.getData<{ id: number }>().id;
    busy.value = true;
    drawerApi.lock();
    departments.value = [];
    deptIds.value = [];
    showDeptTree.value = false;
    try {
      const [role, tree] = await Promise.all([getRole(id.value), deptTreeSelect(id.value)]);
      departments.value = tree.departments;
      deptCheckLinked.value = role.deptCheckLinked ?? true;
      deptIds.value = deptCheckLinked.value
        ? restoreChecked(tree.departments, tree.checkedKeys)
        : tree.checkedKeys;
      await nextTick();
      await formApi.reset();
      await formApi.setValues({
        roleName: role.roleName,
        roleKey: role.roleKey ?? '',
        dataScope: role.dataScope ?? '5',
      });
      showDeptTree.value = role.dataScope === '2';
    } catch {
      await drawerApi.close();
    } finally {
      busy.value = false;
      drawerApi.unlock();
    }
  },
  async onConfirm() {
    if (busy.value || id.value == null) return;
    busy.value = true;
    try {
      const { valid } = await formApi.validate();
      if (!valid) return;
      drawerApi.lock();
      const values = await formApi.getValues<RoleDataScopeParams & Record<string, unknown>>();
      await dataScope({
        id: id.value,
        dataScope: values.dataScope,
        deptCheckLinked: deptCheckLinked.value,
        deptIds:
          values.dataScope === '2'
            ? deptCheckLinked.value
              ? withParents(departments.value, deptIds.value)
              : deptIds.value
            : [],
      });
      ElMessage.success($t('system.role.saveSuccess'));
      emit('success');
      await drawerApi.close();
    } catch {
      // 请求层已经提示。
    } finally {
      busy.value = false;
      drawerApi.unlock();
    }
  },
});
</script>
