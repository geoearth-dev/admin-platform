<template>
  <Page auto-content-height>
    <FormDrawer @success="onRefresh" />
    <DetailDrawer @success="onRefresh" />
    <AssignRolesModal @success="onRefresh" />
    <ImportModal @changed="onRefresh" />
    <div class="flex size-full">
      <ElCard class="w-1/6">
        <ElInput
          v-model="inputSearchValue"
          :placeholder="$t('system.user.placeholder')"
          clearable
          @clear="clearDeptSelection"
          :prefix-icon="Search"
        >
        </ElInput>

        <Tree
          :key="inputSearchValue.trim()"
          :tree-data="filteredDeptList"
          :model-value="selectedDeptId"
          label-field="label"
          value-field="id"
          :allow-clear="false"
          :default-expanded-level="inputSearchValue.trim() ? Number.MAX_SAFE_INTEGER : 2"
          @select="selectDept"
        />
      </ElCard>

      <div class="w-5/6 ml-4">
        <Grid :table-title="$t('system.user.list')">
          <template #toolbar-actions>
            <VbenTableToolbar
              :actions="toolbarActions"
              :loading="{ export: exporting }"
              :selected-count="selectedRows.length"
              @create="onCreate"
              @edit="onEditSelected"
              @delete="onDeleteSelected"
              @import="onImport"
              @export="onExport"
            />
          </template>
          <template #userName="{ row }">
            <VbenButton
              variant="link"
              class="h-auto p-0 font-normal"
              :disabled="!hasAccessByCodes([USER_PERMISSION.query])"
              @click.stop="onDetail(row)"
            >
              {{ row.userName }}
            </VbenButton>
          </template>
          <template #action="{ row }">
            <VbenTableAction
              v-if="row.id !== 1"
              :has-permission="hasActionPermission"
              :actions="[
                {
                  text: $t('common.edit'),
                  icon: 'lucide:square-pen',
                  onClick: () => onEdit(row),
                  auth: [USER_PERMISSION.edit],
                },
                {
                  text: $t('common.delete'),
                  icon: 'lucide:trash-2',
                  danger: true,
                  popConfirm: {
                    title: $t('ui.actionMessage.deleteConfirm', [row.userName]),
                    confirm: () => onDelete(row),
                  },
                  auth: [USER_PERMISSION.remove],
                },
              ]"
              :dropdown-actions="[
                {
                  text: $t('system.user.resetPassword'),
                  icon: 'lucide:key-round',
                  onClick: () => onResetPassword(row),
                  auth: [USER_PERMISSION.resetPassword],
                },
                {
                  text: $t('system.user.assignRoles'),
                  icon: 'lucide:users',
                  onClick: () => onAssignRoles(row),
                  auth: [USER_PERMISSION.edit],
                },
              ]"
              align="center"
            />
          </template>
        </Grid>
      </div>
    </div>
  </Page>
</template>
<script lang="ts" setup>
import { USER_PERMISSION } from '@/constants/permissions';
import type { Dayjs } from 'dayjs';

import type { Recordable } from '@/types';

import {
  changeUserStatus,
  deleteUser,
  deptTreeSelect,
  exportUser,
  getUser,
  listUser,
  resetUserPwd,
} from '@/api/system/user';
import { computed, onMounted, ref } from 'vue';

import { Page } from '@/components/page';
import { Tree } from '@/components/tree';
import { VbenTableToolbar } from '@/components/table-toolbar';
import { ElCard, ElMessage, ElInput, ElMessageBox } from 'element-plus';

import { $t } from '@/plugins/locale';

import { useColumns, useGridFormSchema } from './data.ts';
import Detail from './modules/detail.vue';
import Form from './modules/form.vue';
import { useVbenVxeGrid, type VxeTableGridOptions } from '@/components/vxe-table';
import { useVbenDrawer, useVbenModal } from '@/plugins/vben-ui/popup-ui';
import { VbenButton, VbenTableAction, type TreeProps } from '@/plugins/vben-ui/shadcn-ui';
import { createDateRangeCodec } from '@/utils/date-range-codec';
import type { SysUser } from '@/types/base/api/system/user.ts';
import type { TreeSelect } from '@/types/base/api/common.ts';
import AssignRoles from './modules/assign-roles.vue';
import ImportUsers from './modules/import.vue';
import { Search } from '@lucide/vue';
import { saveExcel } from './modules/download.ts';
import { useAccess } from '@/plugins/effects/access/use-access.ts';

interface UserSearchFormValues extends Record<string, unknown> {
  createTime?: [Dayjs, Dayjs];
}

const userSearchCodec = createDateRangeCodec<UserSearchFormValues>()({
  endField: 'endTime',
  rangeField: 'createTime',
  startField: 'startTime',
});

type UserSearchSubmitValues = ReturnType<typeof userSearchCodec.encode>;

const deptList = ref<TreeSelect[]>([]);
const inputSearchValue = ref('');
const selectedDeptId = ref<number>();

const [AssignRolesModal, assignRolesModalApi] = useVbenModal({
  connectedComponent: AssignRoles,
  destroyOnClose: true,
});

const [ImportModal, importModalApi] = useVbenModal({
  connectedComponent: ImportUsers,
  destroyOnClose: true,
});
const [FormDrawer, formDrawerApi] = useVbenDrawer({
  connectedComponent: Form,
  destroyOnClose: true,
});

const [DetailDrawer, detailDrawerApi] = useVbenDrawer({
  connectedComponent: Detail,
  destroyOnClose: true,
});

const [Grid, gridApi] = useVbenVxeGrid({
  formOptions: {
    codec: userSearchCodec,
    schema: useGridFormSchema(),
    submitOnChange: true,
  },
  gridOptions: {
    columns: useColumns(onStatusChange),
    height: 'auto',
    keepSource: true,
    checkboxConfig: {
      showHeader: true,
      checkStrictly: false,
      checkMethod: ({ row }) => row.id !== 1,
    },
    proxyConfig: {
      response: {
        result: 'records',
        total: 'total',
        list: 'records',
      },
      ajax: {
        query: async ({ page }, formValues: UserSearchSubmitValues) => {
          return await listUser({
            pageNum: page.currentPage,
            pageSize: page.pageSize,
            ...formValues,
            deptId: selectedDeptId.value,
          });
        },
      },
    },
    rowConfig: {
      keyField: 'id',
    },

    toolbarConfig: {
      custom: true,
      export: false,
      refresh: true,
      search: true,
      zoom: true,
    },
  } as VxeTableGridOptions<SysUser>,
  gridEvents: {
    checkboxChange: updateSelection,
    checkboxAll: updateSelection,
  },
});

const selectedRows = ref<SysUser[]>([]);
/**选择数据 */
function updateSelection() {
  selectedRows.value = gridApi.grid.getCheckboxRecords();
}

/**
 * 状态开关即将改变
 * @param newStatus 期望改变的状态值
 * @param row 行数据
 * @returns 返回false则中止改变，返回其他值（undefined、true）则允许改变
 */
async function onStatusChange(newStatus: string, row: SysUser) {
  const status: Recordable<string> = {
    '0': '禁用',
    '1': '启用',
  };
  try {
    await ElMessageBox.confirm(
      `你要将${row.userName}的状态切换为 【${status[newStatus]}】 吗？`,
      `切换状态`,
      {
        type: 'warning',
      },
    );
    await changeUserStatus(row.id, newStatus);
    return true;
  } catch {
    return false;
  }
}

/**编辑 */
function onEditSelected() {
  const row = selectedRows.value[0];
  if (selectedRows.value.length === 1 && row) {
    onEdit(row);
  }
}
async function onEdit(row: SysUser) {
  const data = await getUser(row.id);
  formDrawerApi.setData(data).open();
}

async function onDetail(row: SysUser) {
  const data = await getUser(row.id);
  detailDrawerApi.setData(data).open();
}
async function onDeleteSelected() {
  const ids = selectedRows.value.map((row) => row.id);
  if (!ids.length) return;

  try {
    await ElMessageBox.confirm(`确定删除选中的 ${ids.length} 个用户吗？`, '删除用户', {
      type: 'warning',
    });
  } catch {
    return;
  }

  await deleteUser(ids);

  selectedRows.value = [];
  await gridApi.grid.clearCheckboxRow();
  ElMessage.success('删除成功');
  await gridApi.reload();
}
function onDelete(row: SysUser) {
  const loadingMessage = ElMessage({
    message: $t('ui.actionMessage.deleting', [row.userName]),
    duration: 0,
    key: 'action_process_msg',
  });

  deleteUser(row.id)
    .then(() => {
      ElMessage.success({
        message: $t('ui.actionMessage.deleteSuccess', [row.userName]),
        key: 'action_process_msg',
      });
      onRefresh();
    })
    .catch(() => {
      loadingMessage.close();
    });
}

function onRefresh() {
  selectedRows.value = [];
  gridApi.grid.clearCheckboxRow();
  gridApi.query();
}

function onCreate() {
  formDrawerApi
    .setData({
      deptId: selectedDeptId.value,
    })
    .open();
}
function onImport() {
  importModalApi.open();
}

const exporting = ref(false);
async function onExport() {
  if (exporting.value) return;
  exporting.value = true;
  const blob = await exportUser();
  await saveExcel(blob, `${$t('system.user.list')}_${Date.now()}.xlsx`);
  exporting.value = false;
}
function onAssignRoles(row: SysUser) {
  assignRolesModalApi.setData({ id: row.id }).open();
}

/**修改密码 */
async function onResetPassword(row: SysUser) {
  let password: string;

  try {
    const result = await ElMessageBox.prompt(`请输入用户“${row.userName}”的新密码`, '重置密码', {
      inputType: 'password',
      closeOnClickModal: false,
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputValidator: (value) => Boolean(value?.trim()) || '请输入新密码',
    });

    password = result.value;
  } catch {
    return;
  }

  await resetUserPwd(row.id, password);
  ElMessage.success('密码重置成功');
}
//#region  Dept
const { hasAccessByCodes } = useAccess();
function hasActionPermission(auth?: string | string[]) {
  if (!auth) return true;

  return hasAccessByCodes(Array.isArray(auth) ? auth : [auth]);
}
type ToolbarAction = 'create' | 'edit' | 'delete' | 'import' | 'export';

const toolbarPermissionMap: Record<ToolbarAction, string> = {
  create: USER_PERMISSION.add,
  edit: USER_PERMISSION.edit,
  delete: USER_PERMISSION.remove,
  import: USER_PERMISSION.import,
  export: USER_PERMISSION.export,
};

const toolbarActions = computed(() =>
  (['create', 'edit', 'delete', 'import', 'export'] as ToolbarAction[]).filter((action) =>
    hasAccessByCodes([toolbarPermissionMap[action]]),
  ),
);

onMounted(loadDeptList);
const filteredDeptList = computed(() => {
  const keyword = inputSearchValue.value.trim().toLowerCase();
  return keyword ? filterDeptTree(deptList.value, keyword) : deptList.value;
});

async function loadDeptList() {
  try {
    deptList.value = await deptTreeSelect();
  } catch (error) {
    console.error('加载部门树失败：', error);
  }
}

type DeptTreeItem = Parameters<NonNullable<TreeProps['getNodeClass']>>[0];

function selectDept(node: DeptTreeItem) {
  // Tree 的 select 事件传的是节点，部门数据在 value 中。
  const deptId = node.value.id;

  if (typeof deptId !== 'number') return;

  selectedDeptId.value = deptId;

  // 更换部门后从第一页查询，避免停留在后面的页码而显示为空。
  gridApi.reload();
}

function clearDeptSelection() {
  selectedDeptId.value = undefined;
  inputSearchValue.value = '';
  gridApi.reload();
}

// 保留命中节点及其祖先，不修改原始部门树。
function filterDeptTree(nodes: TreeSelect[], keyword: string): TreeSelect[] {
  return nodes.flatMap((node) => {
    if (node.label.toLowerCase().includes(keyword)) {
      return [node];
    }

    const children = filterDeptTree(node.children ?? [], keyword);

    return children.length > 0 ? [{ ...node, children }] : [];
  });
}

//#endregion
</script>
