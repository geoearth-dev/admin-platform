<script setup lang="ts">
import { computed, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { allocatedUserList, authUserCancel, authUserCancelAll } from '@/api/system/role';
import { IconifyIcon } from '@/assets/icons';
import { VbenTableToolbar } from '@/components/table-toolbar';
import { useVbenVxeGrid } from '@/components/vxe-table';
import { ROLE_PERMISSION } from '@/constants/permissions';
import { useAccess } from '@/plugins/effects/access/use-access';
import { $t } from '@/plugins/locale';
import { confirm, useVbenDrawer, useVbenModal } from '@/plugins/vben-ui/popup-ui';
import { VbenButton, VbenTableAction } from '@/plugins/vben-ui/shadcn-ui';
import type { SysUser } from '@/types/base/api/system/user';
import SelectUsers from './select-users.vue';
import { useUserColumns, useUserSearchSchema } from './user-data';

const emit = defineEmits<{ success: [] }>();
const roleId = ref<number>();
const selectedIds = ref<number[]>([]);
const busy = ref(false);
const querying = ref(false);
const selectingUsers = ref(false);
const { hasAccessByCodes } = useAccess();
const canEdit = computed(() => hasAccessByCodes([ROLE_PERMISSION.edit]));

const [SelectUsersModal, selectUsersModalApi] = useVbenModal({
  connectedComponent: SelectUsers,
  destroyOnClose: true,
  onOpenChange(open) {
    if (open) selectingUsers.value = true;
  },
  onClosed() {
    selectingUsers.value = false;
  },
});
const [Grid, gridApi] = useVbenVxeGrid<SysUser>({
  formOptions: {
    schema: useUserSearchSchema(),
    showCollapseButton: false,
    compact: true,
    actionLayout: 'rowEnd',
    wrapperClass: 'grid-cols-1 md:grid-cols-[minmax(0,1fr)_minmax(0,1fr)_auto] gap-x-4',
  },
  gridOptions: {
    height: 'auto',
    rowConfig: { keyField: 'id' },
    checkboxConfig: {
      showHeader: true,
      checkStrictly: false,
      checkMethod: () => canEdit.value && !busy.value && !querying.value,
    },
    columns: [
      ...useUserColumns(),
      {
        field: 'operation',
        title: $t('system.user.operation'),
        width: 140,
        fixed: 'right',
        slots: { default: 'operation' },
      },
    ],
    toolbarConfig: { search: true, refresh: true },
    proxyConfig: {
      autoLoad: false,
      response: { result: 'records', total: 'total' },
      ajax: {
        query: async ({ page }, values) => {
          selectedIds.value = [];
          await gridApi.grid.clearCheckboxRow();
          if (roleId.value == null) return { records: [], total: 0 };
          querying.value = true;
          try {
            return await allocatedUserList({
              roleId: roleId.value,
              pageNum: page.currentPage,
              pageSize: page.pageSize,
              userName: typeof values.userName === 'string' ? values.userName : undefined,
              phoneNumber: typeof values.phoneNumber === 'string' ? values.phoneNumber : undefined,
            });
          } finally {
            querying.value = false;
          }
        },
      },
    },
  },
  gridEvents: {
    checkboxChange: updateSelection,
    checkboxAll: updateSelection,
  },
});

function updateSelection() {
  selectedIds.value = gridApi.grid.getCheckboxRecords().map((row) => row.id);
}

const [Drawer, drawerApi] = useVbenDrawer({
  // 等选择用户弹窗完全关闭后，才允许关闭父抽屉。
  onBeforeClose: () => !selectingUsers.value,
  onOpenChange(open) {
    if (!open) return;
    roleId.value = drawerApi.getData<{ id: number }>().id;
    selectedIds.value = [];
    querying.value = true;
  },
  async onOpened() {
    // 表格在抽屉打开时才挂载，等打开完成后再重置表单并查询。
    try {
      await gridApi.formApi.reset();
      await gridApi.reload();
    } finally {
      querying.value = false;
    }
  },
});

function addUsers() {
  if (selectingUsers.value || busy.value || querying.value || roleId.value == null || !canEdit.value) return;
  selectingUsers.value = true;
  selectUsersModalApi.setData({ roleId: roleId.value }).open();
}

async function onUsersAdded() {
  emit('success');
  await gridApi.reload();
}

async function cancelAuthorization(row?: SysUser) {
  if (busy.value || querying.value || roleId.value == null || !canEdit.value) return;
  const ids = row ? [row.id] : [...selectedIds.value];
  if (!ids.length) return;
  const currentRoleId = roleId.value;
  busy.value = true;
  try {
    try {
      await confirm({
        title: $t('system.role.cancelAuthorization'),
        content: row
          ? $t('system.role.cancelUserAuthorizationConfirm', { name: row.userName })
          : $t('system.role.cancelAuthorizationConfirm', { count: ids.length }),
        confirmText: $t('common.confirm'),
        cancelText: $t('common.cancel'),
      });
    } catch {
      return;
    }
    drawerApi.lock();
    if (row) await authUserCancel({ roleId: currentRoleId, userId: row.id });
    else await authUserCancelAll(currentRoleId, ids);
    ElMessage.success($t('system.role.saveSuccess'));
    emit('success');
    selectedIds.value = [];
    await gridApi.grid.clearCheckboxRow();
    await gridApi.reload();
  } catch {
    // 请求层已提示，保留列表供重试。
  } finally {
    busy.value = false;
    drawerApi.unlock();
  }
}
</script>

<template>
  <Drawer
    :title="$t('system.role.assignUsers')"
    :footer="false"
    :z-index="1000"
    class="w-[1000px] max-w-full"
    content-class="min-h-0 overflow-hidden"
  >
    <Grid>
      <template #toolbar-actions>
        <VbenTableToolbar :actions="[]">
          <VbenButton
            v-if="canEdit"
            variant="outline"
            class="border-primary/30 bg-primary/10 text-primary hover:bg-primary/20 hover:text-primary"
            :disabled="busy || querying"
            @click="addUsers"
          >
            <IconifyIcon icon="lucide:plus" />
            {{ $t('system.role.addUsers') }}
          </VbenButton>
          <VbenButton
            v-if="canEdit"
            variant="outline"
            class="border-destructive/30 bg-destructive/10 text-destructive hover:bg-destructive/20 hover:text-destructive"
            :loading="busy"
            :disabled="busy || querying || selectedIds.length === 0"
            @click="cancelAuthorization()"
          >
            <IconifyIcon v-if="!busy" icon="lucide:circle-x" />
            {{ $t('system.role.cancelSelectedAuthorization') }}
          </VbenButton>
          <VbenButton variant="outline" :disabled="busy || selectingUsers" @click="drawerApi.close()">
            <IconifyIcon icon="lucide:x" />
            {{ $t('common.cancel') }}
          </VbenButton>
        </VbenTableToolbar>
      </template>
      <template #operation="{ row }">
        <VbenTableAction
          v-if="canEdit"
          :actions="[
            {
              text: $t('system.role.cancelAuthorization'),
              icon: 'lucide:circle-x',
              danger: true,
              disabled: busy || querying,
              onClick: () => cancelAuthorization(row),
            },
          ]"
          align="center"
        />
      </template>
    </Grid>
  </Drawer>
  <SelectUsersModal @success="onUsersAdded" />
</template>
