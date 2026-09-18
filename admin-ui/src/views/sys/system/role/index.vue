<script setup lang="ts">
import { ROLE_PERMISSION } from '@/constants/permissions';
import type { Dayjs } from 'dayjs';
import { computed, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { changeRoleStatus, deleteRole, exportRole, listRole } from '@/api/system/role';
import { Page } from '@/components/page';
import { VbenTableToolbar } from '@/components/table-toolbar';
import { useVbenVxeGrid } from '@/components/vxe-table';
import { useAccess } from '@/plugins/effects/access/use-access';
import { $t } from '@/plugins/locale';
import type { BaseFormComponentType, ElementPlusComponentProps } from '@/plugins/vben-ui/form-ui';
import { confirm, useVbenDrawer } from '@/plugins/vben-ui/popup-ui';
import { VbenTableAction } from '@/plugins/vben-ui/shadcn-ui';
import type { RoleQueryParams, SysRole } from '@/types/base/api/system/role';
import { downloadFileFromBlob } from '@/utils/download';
import { createDateRangeCodec } from '@/utils/date-range-codec';
import { useColumns, useGridFormSchema } from './data';
import Form from './modules/form.vue';
import DataScope from './modules/data-scope.vue';
import Users from './modules/users.vue';

interface RoleSearchFormValues extends Record<string, unknown> {
  createTime?: [Dayjs, Dayjs];
}
const roleSearchCodec = createDateRangeCodec<RoleSearchFormValues>()({
  endField: 'endTime',
  rangeField: 'createTime',
  startField: 'beginTime',
});
const { hasAccessByCodes } = useAccess();
function hasPermission(auth?: string | string[]) {
  return !auth || hasAccessByCodes(Array.isArray(auth) ? auth : [auth]);
}
type ToolbarAction = 'create' | 'edit' | 'delete' | 'export';
const toolbarPermissions: Record<ToolbarAction, string> = {
  create: ROLE_PERMISSION.add,
  edit: ROLE_PERMISSION.edit,
  delete: ROLE_PERMISSION.remove,
  export: ROLE_PERMISSION.export,
};
const toolbarActions = computed(() =>
  (['create', 'edit', 'delete', 'export'] as ToolbarAction[]).filter((action) =>
    hasPermission(toolbarPermissions[action]),
  ),
);
const selectedRows = ref<SysRole[]>([]);
const exporting = ref(false);
const deleting = ref(false);
const lastQuery = ref<RoleQueryParams>({});
const [FormDrawer, formDrawerApi] = useVbenDrawer({
  connectedComponent: Form,
  destroyOnClose: true,
});
const [ScopeDrawer, scopeDrawerApi] = useVbenDrawer({
  connectedComponent: DataScope,
  destroyOnClose: true,
});
const [UsersDrawer, usersDrawerApi] = useVbenDrawer({
  connectedComponent: Users,
  destroyOnClose: true,
});

type RoleSearchSubmitValues = ReturnType<typeof roleSearchCodec.encode>;

const [Grid, gridApi] = useVbenVxeGrid<
  SysRole,
  BaseFormComponentType,
  ElementPlusComponentProps,
  RoleSearchFormValues,
  RoleSearchSubmitValues
>({
  formOptions: {
    codec: roleSearchCodec,
    schema: useGridFormSchema(),
    submitOnChange: true,
  },
  gridOptions: {
    columns: useColumns(onStatusChange, hasPermission(ROLE_PERMISSION.edit)),
    height: 'auto',
    keepSource: true,
    checkboxConfig: {
      showHeader: true,
      checkStrictly: false,
      checkMethod: ({ row }) => row.id !== 1,
    },
    proxyConfig: {
      response: { result: 'records', total: 'total' },
      ajax: {
        query: async ({ page }, values) => {
          selectedRows.value = [];
          const query: RoleQueryParams = {
            id: typeof values.id === 'number' ? values.id : undefined,
            pageNum: page.currentPage,
            pageSize: page.pageSize,
            roleName: typeof values.roleName === 'string' ? values.roleName : undefined,
            roleKey: typeof values.roleKey === 'string' ? values.roleKey : undefined,
            status: typeof values.status === 'string' ? values.status : undefined,
            // 当前 RolePageReqDTO 的 LocalDate 使用 ISO.DATE_TIME 绑定。
            beginTime: values.beginTime ? String(values.beginTime) + 'T00:00:00' : undefined,
            endTime: values.endTime ? String(values.endTime) + 'T00:00:00' : undefined,
          };
          const result = await listRole(query);
          lastQuery.value = query;
          return result;
        },
      },
    },
    rowConfig: { keyField: 'id' },
    toolbarConfig: {
      custom: true,
      export: false,
      refresh: true,
      search: true,
      zoom: true,
    },
  },
  gridEvents: { checkboxChange: updateSelection, checkboxAll: updateSelection },
});
function updateSelection() {
  selectedRows.value = gridApi.grid.getCheckboxRecords();
}
function onCreate() {
  formDrawerApi.setData({}).open();
}
function onEdit(row: SysRole) {
  // 抽屉打开时获取详情和菜单树，不使用列表行覆盖完整权限。
  formDrawerApi.setData({ id: row.id }).open();
}
function onEditSelected() {
  const row = selectedRows.value[0];
  if (selectedRows.value.length === 1 && row) onEdit(row);
}
async function onRefresh() {
  selectedRows.value = [];
  await gridApi.grid.clearCheckboxRow();
  await gridApi.query();
}
async function onStatusChange(status: string, row: SysRole): Promise<boolean> {
  if (!hasPermission(ROLE_PERMISSION.edit)) return false;
  if (row.id === 1) {
    ElMessage.warning($t('system.role.adminProtected'));
    return false;
  }
  if (status !== '0' && status !== '1') return false;
  try {
    await confirm({
      title: $t('system.role.status'),
      content: $t('system.role.statusConfirm', {
        name: row.roleName,
        status: $t(status === '1' ? 'common.enabled' : 'common.disabled'),
      }),
      confirmText: $t('common.confirm'),
      cancelText: $t('common.cancel'),
    });
    await changeRoleStatus(row.id, status);
    return true;
  } catch {
    return false;
  }
}
async function onDelete(rows: SysRole[]) {
  if (!rows.length || deleting.value) return;
  deleting.value = true;
  try {
    try {
      await confirm({
        title: $t('common.delete'),
        content: $t('system.role.deleteConfirm', {
          names: rows.map((row) => row.roleName).join('、'),
        }),
        confirmText: $t('common.confirm'),
        cancelText: $t('common.cancel'),
        icon: 'warning',
      });
    } catch {
      return;
    }
    await deleteRole(rows.map((row) => row.id));
    ElMessage.success($t('system.role.deleteSuccess'));
    selectedRows.value = [];
    await gridApi.grid.clearCheckboxRow();
    await gridApi.reload();
  } catch {
    // 请求层已经提示。
  } finally {
    deleting.value = false;
  }
}
async function onExport() {
  if (exporting.value) return;
  exporting.value = true;
  try {
    const blob = await exportRole(lastQuery.value);
    if (blob.type.includes('json') || blob.type.includes('text/html')) {
      let message = $t('system.role.downloadFailed');
      try {
        const result: unknown = JSON.parse(await blob.text());
        if (
          result &&
          typeof result === 'object' &&
          'message' in result &&
          typeof result.message === 'string'
        ) {
          message = result.message;
        }
      } catch {
        /* 保留默认提示。 */
      }
      ElMessage.error(message);
      return;
    }
    downloadFileFromBlob({
      source: blob,
      fileName: $t('system.role.list') + '_' + Date.now() + '.xlsx',
    });
  } catch {
    // 请求层已经提示。
  } finally {
    exporting.value = false;
  }
}
</script>

<template>
  <Page auto-content-height>
    <FormDrawer @success="onRefresh" />
    <ScopeDrawer @success="onRefresh" />
    <UsersDrawer @success="onRefresh" />
    <Grid :table-title="$t('system.role.list')">
      <template #toolbar-actions>
        <VbenTableToolbar
          :actions="toolbarActions"
          :selected-count="selectedRows.length"
          :loading="{ export: exporting, delete: deleting }"
          @create="onCreate"
          @edit="onEditSelected"
          @delete="onDelete(selectedRows)"
          @export="onExport"
        />
      </template>
      <template #action="{ row }">
        <VbenTableAction
          v-if="row.id !== 1"
          align="center"
          :has-permission="hasPermission"
          :actions="[
            {
              text: $t('common.edit'),
              icon: 'lucide:square-pen',
              auth: ROLE_PERMISSION.edit,
              onClick: () => onEdit(row),
            },
            {
              text: $t('common.delete'),
              icon: 'lucide:trash-2',
              danger: true,
              disabled: deleting,
              auth: ROLE_PERMISSION.remove,
              onClick: () => onDelete([row]),
            },
          ]"
          :dropdown-actions="[
            {
              text: $t('system.role.dataScope'),
              icon: 'lucide:shield-check',
              auth: ROLE_PERMISSION.edit,
              onClick: () => scopeDrawerApi.setData({ id: row.id }).open(),
            },
            {
              text: $t('system.role.assignUsers'),
              icon: 'lucide:users',
              auth: ROLE_PERMISSION.list,
              onClick: () => usersDrawerApi.setData({ id: row.id }).open(),
            },
          ]"
        />
      </template>
    </Grid>
  </Page>
</template>
