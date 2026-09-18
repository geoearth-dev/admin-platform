<script setup lang="ts">
import { computed, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { authUserSelectAll, unallocatedUserList } from '@/api/system/role';
import { useVbenVxeGrid } from '@/components/vxe-table';
import { ROLE_PERMISSION } from '@/constants/permissions';
import { useAccess } from '@/plugins/effects/access/use-access';
import { $t } from '@/plugins/locale';
import { useVbenModal } from '@/plugins/vben-ui/popup-ui';
import type { SysUser } from '@/types/base/api/system/user';
import { useUserColumns, useUserSearchSchema } from './user-data';

const emit = defineEmits<{ success: [] }>();
const roleId = ref<number>();
const selectedIds = ref<number[]>([]);
const busy = ref(false);
const querying = ref(false);
const { hasAccessByCodes } = useAccess();
const canEdit = computed(() => hasAccessByCodes([ROLE_PERMISSION.edit]));

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
    columns: useUserColumns(),
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
            return await unallocatedUserList({
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

const [Modal, modalApi] = useVbenModal({
  fullscreenButton: false,
  onOpenChange(open) {
    if (!open) return;
    roleId.value = modalApi.getData<{ roleId: number }>().roleId;
    selectedIds.value = [];
    querying.value = true;
  },
  async onOpened() {
    // 表格在弹窗打开时才挂载，等打开完成后再重置表单并查询。
    try {
      await gridApi.formApi.reset();
      await gridApi.reload();
    } finally {
      querying.value = false;
    }
  },
  async onConfirm() {
    if (busy.value || querying.value || roleId.value == null || !canEdit.value || !selectedIds.value.length) return;
    const ids = [...selectedIds.value];
    busy.value = true;
    modalApi.lock();
    try {
      await authUserSelectAll(roleId.value, ids);
      ElMessage.success($t('system.role.saveSuccess'));
      emit('success');
      await modalApi.close();
    } catch {
      // 请求层已提示，保留勾选供重试。
    } finally {
      busy.value = false;
      modalApi.unlock();
    }
  },
});
</script>

<template>
  <Modal
    :title="$t('system.role.selectUsers')"
    :confirm-disabled="busy || querying || !canEdit || selectedIds.length === 0"
    :z-index="1100"
    class="w-[1100px] max-w-[95vw]"
  >
    <div class="h-[min(60vh,560px)]">
      <Grid />
    </div>
  </Modal>
</template>
