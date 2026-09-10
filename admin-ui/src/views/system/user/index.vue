<template>
  <Page auto-content-height>
    <FormDrawer @success="onRefresh" />
    <DetailDrawer @success="onRefresh" />
    <div class="flex size-full">
      <ElCard class="w-1/6">
        <ElInput
          v-model="inputSearchValue"
          :placeholder="$t('system.user.placeholder')"
          clearable
          @clear="clearDeptSelection"
          :prefix-icon="Search"
        >
          ></ElInput
        >

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
          <template #toolbar-tools>
            <ElButton type="primary" @click="onCreate">
              <Plus class="size-5" />
              {{ $t('ui.actionTitle.create', [$t('system.user.name')]) }}
            </ElButton>
          </template>
          <template #action="{ row }">
            <VbenTableAction
              :actions="[
                {
                  text: $t('common.detail'),
                  icon: 'lucide:eye',
                  onClick: () => onDetail(row),
                },
                {
                  text: $t('common.edit'),
                  icon: 'lucide:edit',
                  onClick: () => onEdit(row),
                },
              ]"
              :dropdown-actions="[
                {
                  text: $t('common.delete'),
                  icon: 'lucide:trash-2',
                  danger: true,
                  popConfirm: {
                    title: $t('ui.actionMessage.deleteConfirm', [row.userName]),
                    confirm: () => onDelete(row),
                  },
                  auth: ['AC_100100'],
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
import type { Dayjs } from 'dayjs';

import type { Recordable } from '@/types';

import { deleteUser, deptTreeSelect, listUser, updateUser } from '@/api/system/user';
import { computed, onMounted, ref } from 'vue';

import { Page } from '@/components/page';
import { Tree } from '@/components/tree';
import { Plus } from '@/assets/icons';

import { ElButton, ElCard, ElMessage, ElInput, ElDialog } from 'element-plus';

import { $t } from '@/plugins/locale';

import { useColumns, useGridFormSchema } from './data.ts';
import Detail from './modules/detail.vue';
import Form from './modules/form.vue';
import { useVbenVxeGrid, type VxeTableGridOptions } from '@/components/vxe-table';
import { useVbenDrawer } from '@/plugins/vben-ui/popup-ui';
import { VbenTableAction, type TreeProps } from '@/plugins/vben-ui/shadcn-ui';
import { createDateRangeCodec } from '../util/date-range-codec.ts';
import type { SysUser } from '@/types/base/api/system/user.ts';
import type { TreeSelect } from '@/types/base/api/common.ts';
import { Search } from '@element-plus/icons-vue';
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
});

/**
 * 将ElDialog封装为promise，方便在异步函数中调用。
 * @param content 提示内容
 * @param title 提示标题
 */
function confirm(content: string, title: string) {
  return new Promise((reslove, reject) => {
    ElDialog.confirm({
      content,
      onCancel() {
        reject(new Error('已取消'));
      },
      onOk() {
        reslove(true);
      },
      title,
    });
  });
}

/**
 * 状态开关即将改变
 * @param newStatus 期望改变的状态值
 * @param row 行数据
 * @returns 返回false则中止改变，返回其他值（undefined、true）则允许改变
 */
async function onStatusChange(newStatus: string, row: SysUser) {
  const status: Recordable<string> = {
    0: '禁用',
    1: '启用',
  };
  try {
    await confirm(
      `你要将${row.userName}的状态切换为 【${status[newStatus.toString()]}】 吗？`,
      `切换状态`,
    );
    await updateUser({ id: row.id, status: newStatus });
    return true;
  } catch {
    return false;
  }
}

function onEdit(row: SysUser) {
  formDrawerApi.setData(row).open();
}

function onDetail(row: SysUser) {
  detailDrawerApi.setData(row).open();
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
  gridApi.query();
}

function onCreate() {
  formDrawerApi.setData({}).open();
}

//#region

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
