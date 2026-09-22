<script setup lang="ts">
import type { TableInstance } from 'element-plus';
import type { SysLoginLog, LoginLogQueryParams } from '@/types/base/api/system/log/loginlog';
import { computed, onMounted, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { IconifyIcon, RotateCw, Search } from '@/assets/icons';
import {
  listLoginLog,
  delLoginLog,
  cleanLoginLog,
  exportLoginLog,
  unlockLoginLog,
} from '@/api/system/log/loginlog';
import Pagination from '@/components/Pagination/index.vue';
import RightToolbar from '@/components/RightToolbar/index.vue';
import { VbenTableToolbar } from '@/components/table-toolbar';
import { LOGIN_LOG_PERMISSION } from '@/constants/permissions';
import { useAccess } from '@/plugins/effects/access/use-access';
import { $t } from '@/plugins/locale';
import { confirm } from '@/plugins/vben-ui/popup-ui';
import { VbenButton } from '@/plugins/vben-ui/shadcn-ui';
import { formatDateTime } from '@/utils/date';
import { downloadFileFromBlob } from '@/utils/download';

defineOptions({ name: 'LoginLog' });

const { hasAccessByCodes } = useAccess();

const tableRef = ref<TableInstance>();
const rows = ref<SysLoginLog[]>([]);
const selected = ref<SysLoginLog[]>([]);
const loading = ref(false);
const loadFailed = ref(false);
const pending = ref<'delete' | 'clean' | 'unlock'>();
const exporting = ref(false);
const busy = computed(() => loading.value || !!pending.value);
const showSearch = ref(true);
const total = ref(0);
const dateRange = ref<[string, string] | null>(null);
const queryParams = ref<LoginLogQueryParams>({ pageNum: 1, pageSize: 10 });
let requestId = 0;

const toolbarActions = computed(() => {
  const actions: Array<'delete' | 'export'> = [];
  if (hasAccessByCodes([LOGIN_LOG_PERMISSION.remove])) actions.push('delete');
  if (hasAccessByCodes([LOGIN_LOG_PERMISSION.export])) actions.push('export');
  return actions;
});

function buildQuery(): LoginLogQueryParams {
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
    const result = await listLoginLog(buildQuery());
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
        ? $t('system.log.loginClearConfirm')
        : $t('system.log.deleteConfirm', { count: ids.length }),
      icon: 'warning',
    });
  } catch {
    pending.value = undefined;
    return;
  }
  try {
    if (clearAll) await cleanLoginLog();
    else await delLoginLog(ids);
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
    const blob = await exportLoginLog(buildQuery());
    downloadFileFromBlob({
      source: blob,
      fileName: `login_log_${Date.now()}.xlsx`,
    });
  } catch {
    ElMessage.error($t('system.log.exportFailed'));
  } finally {
    exporting.value = false;
  }
}

async function handleUnlock() {
  const row = selected.value[0];
  if (busy.value || selected.value.length !== 1 || !row?.userName) return;
  pending.value = 'unlock';
  try {
    await confirm({
      title: $t('system.log.unlock'),
      content: $t('system.log.unlockConfirm', { name: row.userName }),
      icon: 'warning',
    });
  } catch {
    pending.value = undefined;
    return;
  }
  try {
    await unlockLoginLog(row.userName);
    ElMessage.success($t('system.log.unlockSuccess', { name: row.userName }));
  } catch {
    ElMessage.error($t('system.log.actionFailed'));
  } finally {
    pending.value = undefined;
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
      <ElFormItem :label="$t('system.log.ipAddress')">
        <ElInput
          v-model="queryParams.ipAddress"
          clearable
          class="w-48!"
          @keyup.enter="handleQuery"
        />
      </ElFormItem>
      <ElFormItem :label="$t('system.log.userName')">
        <ElInput
          v-model="queryParams.userName"
          clearable
          class="w-48!"
          @keyup.enter="handleQuery"
        />
      </ElFormItem>

      <ElFormItem :label="$t('system.log.status')">
        <ElSelect v-model="queryParams.status" clearable class="w-32!">
          <ElOption :label="$t('system.log.success')" value="1" />
          <ElOption :label="$t('system.log.failure')" value="0" />
        </ElSelect>
      </ElFormItem>
      <ElFormItem :label="$t('system.log.loginTime')">
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
          v-if="hasAccessByCodes([LOGIN_LOG_PERMISSION.remove])"
          variant="outline"
          class="border-destructive/30 bg-destructive/10 text-destructive hover:text-destructive"
          :disabled="busy"
          :loading="pending === 'clean'"
          @click="handleRemove(true)"
        >
          <IconifyIcon icon="lucide:trash-2" class="size-3.5" />{{ $t('system.log.clear') }}
        </VbenButton>
        <VbenButton
          v-if="hasAccessByCodes([LOGIN_LOG_PERMISSION.unlock])"
          variant="outline"
          :disabled="busy || selected.length !== 1 || !selected[0]?.userName"
          :loading="pending === 'unlock'"
          @click="handleUnlock"
        >
          <IconifyIcon icon="lucide:lock-keyhole-open" class="size-3.5" />{{
            $t('system.log.unlock')
          }}
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
        prop="loginLocation"
        :label="$t('system.log.location')"
        min-width="140"
        show-overflow-tooltip
      />
      <ElTableColumn
        prop="operatingSystem"
        :label="$t('system.log.operatingSystem')"
        min-width="130"
        show-overflow-tooltip
      />
      <ElTableColumn
        prop="browser"
        :label="$t('system.log.browser')"
        min-width="130"
        show-overflow-tooltip
      />
      <ElTableColumn :label="$t('system.log.status')" width="100" align="center">
        <template #default="{ row }">
          <ElTag :type="row.status === '1' ? 'success' : 'danger'" disable-transitions>
            {{ $t(row.status === '1' ? 'system.log.success' : 'system.log.failure') }}
          </ElTag>
        </template>
      </ElTableColumn>
      <ElTableColumn
        prop="message"
        :label="$t('system.log.message')"
        min-width="180"
        show-overflow-tooltip
      />
      <ElTableColumn :label="$t('system.log.loginTime')" width="180">
        <template #default="{ row }">{{ formatDateTime(row.loginTime) }}</template>
      </ElTableColumn>
    </ElTable>
    <Pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>
