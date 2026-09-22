<script setup lang="ts">
import { ElMessage, type TableInstance } from 'element-plus';
import type { SysOperationLog, OperationLogQueryParams } from '@/types/base/api/system/log/operlog';
import { computed, onMounted, ref } from 'vue';
import { IconifyIcon, RotateCw, Search } from '@/assets/icons';
import {
  listOperationLog,
  delOperationLog,
  cleanOperationLog,
  exportOperationLog,
  getOperationLog,
} from '@/api/system/log/operlog';
import Pagination from '@/components/Pagination/index.vue';
import RightToolbar from '@/components/RightToolbar/index.vue';
import { VbenTableToolbar } from '@/components/table-toolbar';
import { OPERATION_LOG_PERMISSION } from '@/constants/permissions';
import { useAccess } from '@/plugins/effects/access/use-access';
import { $t } from '@/plugins/locale';
import { confirm } from '@/plugins/vben-ui/popup-ui';
import { VbenButton, VbenTableAction } from '@/plugins/vben-ui/shadcn-ui';
import { formatDateTime } from '@/utils/date';
import { downloadFileFromBlob } from '@/utils/download';
import DictTag from '@/components/DictTag/index.vue';
import { useDict } from '@/utils/dict';
import OperlogDetail from './detail.vue';

defineOptions({ name: 'OperationLog' });

const { hasAccessByCodes } = useAccess();
const { sys_oper_type } = useDict('sys_oper_type');
const detailVisible = ref(false);
const detailRow = ref<SysOperationLog>();
const detailLoading = ref(false);
const tableRef = ref<TableInstance>();
const rows = ref<SysOperationLog[]>([]);
const selected = ref<SysOperationLog[]>([]);
const loading = ref(false);
const loadFailed = ref(false);
const pending = ref<'delete' | 'clean'>();
const exporting = ref(false);
const busy = computed(() => loading.value || !!pending.value);
const showSearch = ref(true);
const total = ref(0);
const dateRange = ref<[string, string] | null>(null);
const queryParams = ref<OperationLogQueryParams>({ pageNum: 1, pageSize: 10 });
let requestId = 0;

const toolbarActions = computed(() => {
  const actions: Array<'delete' | 'export'> = [];
  if (hasAccessByCodes([OPERATION_LOG_PERMISSION.remove])) actions.push('delete');
  if (hasAccessByCodes([OPERATION_LOG_PERMISSION.export])) actions.push('export');
  return actions;
});

function buildQuery(): OperationLogQueryParams {
  return {
    ...queryParams.value,
    beginDate: dateRange.value?.[0],
    endDate: dateRange.value?.[1],
  };
}

async function getList() {
  const currentRequest = ++requestId;
  loading.value = true;
  loadFailed.value = false;
  selected.value = [];
  tableRef.value?.clearSelection();
  try {
    const result = await listOperationLog(buildQuery());
    if (currentRequest !== requestId) return;
    rows.value = result.records;
    total.value = result.total;
  } catch {
    if (currentRequest === requestId) loadFailed.value = true;
  } finally {
    if (currentRequest === requestId) loading.value = false;
  }
}

function handleQuery() {
  queryParams.value.pageNum = 1;
  void getList();
}

function resetQuery() {
  queryParams.value = { pageNum: 1, pageSize: queryParams.value.pageSize };
  dateRange.value = null;
  void getList();
}

async function handleRemove(clearAll = false) {
  if (busy.value || (!clearAll && !selected.value.length)) return;
  const ids = selected.value.map((row) => row.id);
  pending.value = clearAll ? 'clean' : 'delete';
  try {
    await confirm({
      title: $t(clearAll ? 'system.log.clear' : 'common.delete'),
      content: clearAll
        ? $t('system.log.operationClearConfirm')
        : $t('system.log.deleteConfirm', { count: ids.length }),
      icon: 'warning',
    });
  } catch {
    pending.value = undefined;
    return;
  }
  try {
    if (clearAll) await cleanOperationLog();
    else await delOperationLog(ids);
    ElMessage.success($t(clearAll ? 'system.log.clearSuccess' : 'system.log.deleteSuccess'));
    queryParams.value.pageNum = clearAll
      ? 1
      : Math.min(
          queryParams.value.pageNum ?? 1,
          Math.max(1, Math.ceil((total.value - ids.length) / (queryParams.value.pageSize ?? 10))),
        );
    await getList();
  } catch {
    ElMessage.error($t('system.log.actionFailed'));
  } finally {
    pending.value = undefined;
  }
}

async function handleExport() {
  if (busy.value || exporting.value) return;
  exporting.value = true;
  try {
    const blob = await exportOperationLog(buildQuery());
    downloadFileFromBlob({
      source: blob,
      fileName: `operation_log_${Date.now()}.xlsx`,
    });
  } catch {
    ElMessage.error($t('system.log.exportFailed'));
  } finally {
    exporting.value = false;
  }
}

async function handleDetail(id: number) {
  if (detailLoading.value) return;
  detailLoading.value = true;
  try {
    detailRow.value = await getOperationLog(id);
    detailVisible.value = true;
  } catch {
    ElMessage.error($t('system.log.loadFailed'));
  } finally {
    detailLoading.value = false;
  }
}

onMounted(getList);
</script>

<template>
  <div class="log-management m-4 rounded-xl border border-border bg-background p-4">
    <ElForm
      v-show="showSearch"
      :model="queryParams"
      inline
      :disabled="busy"
      @submit.prevent="handleQuery"
    >
      <ElFormItem :label="$t('system.log.module')">
        <ElInput v-model="queryParams.title" clearable class="w-48!" @keyup.enter="handleQuery" />
      </ElFormItem>
      <ElFormItem :label="$t('system.log.userName')">
        <ElInput
          v-model="queryParams.userName"
          clearable
          class="w-48!"
          @keyup.enter="handleQuery"
        />
      </ElFormItem>
      <ElFormItem :label="$t('system.log.businessType')">
        <ElSelect v-model="queryParams.businessType" clearable class="w-36!">
          <ElOption
            v-for="item in sys_oper_type"
            :key="item.value"
            :label="item.label"
            :value="Number(item.value)"
          />
        </ElSelect>
      </ElFormItem>
      <ElFormItem :label="$t('system.log.status')">
        <ElSelect v-model="queryParams.status" clearable class="w-32!">
          <ElOption :label="$t('system.log.normal')" :value="1" />
          <ElOption :label="$t('system.log.error')" :value="0" />
        </ElSelect>
      </ElFormItem>
      <ElFormItem :label="$t('system.log.operationTime')">
        <ElDatePicker
          v-model="dateRange"
          type="daterange"
          value-format="YYYY-MM-DD"
          class="w-64!"
          range-separator="—"
          :start-placeholder="$t('system.log.beginDate')"
          :end-placeholder="$t('system.log.endDate')"
        />
      </ElFormItem>
      <ElFormItem>
        <div class="flex gap-2">
          <VbenButton type="submit" size="sm" :disabled="busy">
            <Search class="size-3.5" />{{ $t('common.search') }}
          </VbenButton>
          <VbenButton
            type="button"
            size="sm"
            variant="outline"
            :disabled="busy"
            @click="resetQuery"
          >
            <RotateCw class="size-3.5" />{{ $t('common.reset') }}
          </VbenButton>
        </div>
      </ElFormItem>
    </ElForm>
    <div class="mb-3 flex flex-wrap items-center justify-between gap-3">
      <VbenTableToolbar
        :actions="toolbarActions"
        :selected-count="busy ? 0 : selected.length"
        :loading="{ delete: pending === 'delete', export: exporting }"
        @delete="handleRemove()"
        @export="handleExport"
      >
        <VbenButton
          v-if="hasAccessByCodes([OPERATION_LOG_PERMISSION.remove])"
          variant="outline"
          class="border-destructive/30 bg-destructive/10 text-destructive hover:text-destructive"
          :disabled="busy"
          :loading="pending === 'clean'"
          @click="handleRemove(true)"
        >
          <IconifyIcon icon="lucide:trash-2" class="size-3.5" />{{ $t('system.log.clear') }}
        </VbenButton>
      </VbenTableToolbar>
      <RightToolbar v-model:show-search="showSearch" @query-table="getList" />
    </div>
    <p v-if="loadFailed" class="mb-3 text-sm text-destructive">
      {{ $t('system.log.loadFailed') }}
    </p>
    <ElTable
      ref="tableRef"
      v-loading="loading"
      :data="rows"
      row-key="id"
      @selection-change="selected = $event"
    >
      <ElTableColumn type="selection" width="50" align="center" />
      <ElTableColumn prop="id" :label="$t('system.log.id')" width="90" align="center" />
      <ElTableColumn
        prop="title"
        :label="$t('system.log.module')"
        min-width="150"
        show-overflow-tooltip
      />
      <ElTableColumn :label="$t('system.log.businessType')" width="100" align="center">
        <template #default="{ row }"
          ><DictTag :options="sys_oper_type" :value="row.businessType"
        /></template>
      </ElTableColumn>
      <ElTableColumn
        prop="userName"
        :label="$t('system.log.userName')"
        min-width="120"
        show-overflow-tooltip
      />
      <ElTableColumn
        prop="ipAddress"
        :label="$t('system.log.ipAddress')"
        min-width="140"
        show-overflow-tooltip
      />
      <ElTableColumn
        prop="operationLocation"
        :label="$t('system.log.location')"
        min-width="140"
        show-overflow-tooltip
      />

      <ElTableColumn :label="$t('system.log.status')" width="100" align="center">
        <template #default="{ row }">
          <ElTag :type="row.status === 1 ? 'success' : 'danger'" disable-transitions>
            {{ $t(row.status === 1 ? 'system.log.normal' : 'system.log.error') }}
          </ElTag>
        </template>
      </ElTableColumn>

      <ElTableColumn :label="$t('system.log.operationTime')" width="180">
        <template #default="{ row }">{{ formatDateTime(row.operationTime) }}</template>
      </ElTableColumn>
      <ElTableColumn
        prop="durationMs"
        :label="$t('system.log.duration')"
        width="110"
        align="center"
      >
        <template #default="{ row }">{{ row.durationMs ?? '—' }} ms</template>
      </ElTableColumn>
      <ElTableColumn
        v-if="hasAccessByCodes([OPERATION_LOG_PERMISSION.query])"
        :label="$t('system.log.actions')"
        width="100"
        fixed="right"
        align="center"
      >
        <template #default="{ row }">
          <VbenTableAction
            :actions="[
              {
                text: $t('common.detail'),
                icon: 'lucide:eye',
                disabled: detailLoading,
                onClick: () => handleDetail(row.id),
              },
            ]"
          />
        </template>
      </ElTableColumn>
    </ElTable>
    <Pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
    <OperlogDetail v-if="detailRow" v-model:visible="detailVisible" :row="detailRow" />
  </div>
</template>
