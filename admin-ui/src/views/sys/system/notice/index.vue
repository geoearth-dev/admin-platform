<script setup lang="ts">
import type { NoticeQueryParams, SysNotice } from '@/types/base/api/system/notice';
import type { TableInstance } from 'element-plus';
import { computed, onMounted, ref } from 'vue';
import {
  ElForm,
  ElFormItem,
  ElInput,
  ElMessage,
  ElOption,
  ElPagination,
  ElSelect,
  ElTable,
  ElTableColumn,
} from 'element-plus';
import { RotateCw, Search } from '@/assets/icons';
import { delNotice, getNotice, listNotice } from '@/api/system/notice';
import DictTag from '@/components/DictTag/index.vue';
import NoticeDetailView from '@/components/HeaderNotice/DetailView.vue';
import { VbenTableToolbar } from '@/components/table-toolbar';
import { NOTICE_PERMISSION } from '@/constants/permissions';
import { useAccess } from '@/plugins/effects/access/use-access';
import { $t } from '@/plugins/locale';
import { confirm } from '@/plugins/vben-ui/popup-ui';
import { VbenButton, VbenIconButton, VbenTableAction } from '@/plugins/vben-ui/shadcn-ui';
import { formatDateTime } from '@/utils/date';
import { useDict } from '@/utils/dict';
import NoticeForm from './modules/form.vue';
import ReadUsers from './ReadUsers.vue';

const { hasAccessByCodes } = useAccess();
const { sys_notice_type, sys_notice_status } = useDict('sys_notice_type', 'sys_notice_status');
const formRef = ref<InstanceType<typeof NoticeForm>>();
const detailRef = ref<InstanceType<typeof NoticeDetailView>>();
const readersRef = ref<InstanceType<typeof ReadUsers>>();
const tableRef = ref<TableInstance>();
const rows = ref<SysNotice[]>([]);
const selected = ref<SysNotice[]>([]);
const total = ref(0);
const loading = ref(false);
const deleting = ref(false);
const loadFailed = ref(false);
const showSearch = ref(true);
const page = ref(1);
const pageSize = ref(10);
const filters = ref({
  noticeTitle: '',
  createBy: '',
  noticeType: '',
  status: '',
});

const toolbarActions = computed(() => {
  const actions: Array<'create' | 'edit' | 'delete'> = [];
  if (hasAccessByCodes([NOTICE_PERMISSION.add])) actions.push('create');
  if (hasAccessByCodes([NOTICE_PERMISSION.edit])) actions.push('edit');
  if (hasAccessByCodes([NOTICE_PERMISSION.remove])) actions.push('delete');
  return actions;
});

async function loadList() {
  if (loading.value) return;
  loading.value = true;
  loadFailed.value = false;
  selected.value = [];
  tableRef.value?.clearSelection();
  try {
    const query: NoticeQueryParams = {
      pageNum: page.value,
      pageSize: pageSize.value,
      noticeTitle: filters.value.noticeTitle || undefined,
      createBy: filters.value.createBy || undefined,
      noticeType: filters.value.noticeType || undefined,
      status:
        filters.value.status === '0' || filters.value.status === '1'
          ? filters.value.status
          : undefined,
    };
    const result = await listNotice(query);
    rows.value = result.records;
    total.value = result.total;
  } catch {
    loadFailed.value = true;
  } finally {
    loading.value = false;
  }
}

function search() {
  page.value = 1;
  void loadList();
}

function resetSearch() {
  filters.value = { noticeTitle: '', createBy: '', noticeType: '', status: '' };
  search();
}

function editSelected() {
  const row = selected.value[0];
  if (row) void formRef.value?.open(row.id);
}

async function showDetail(row: SysNotice) {
  try {
    const notice = await getNotice(row.id);
    detailRef.value?.open({
      ...notice,
      typeLabel: sys_notice_type.value.find((item) => item.value === notice.noticeType)?.label,
      statusLabel: sys_notice_status.value.find((item) => item.value === notice.status)?.label,
    });
  } catch {
    ElMessage.error($t('system.notice.loadFailed'));
  }
}

async function removeRows(records: SysNotice[]) {
  if (!records.length || deleting.value) return;
  try {
    await confirm({
      title: $t('common.delete'),
      content: $t('system.notice.deleteConfirm', { count: records.length }),
      icon: 'warning',
    });
  } catch {
    return;
  }
  deleting.value = true;
  try {
    await delNotice(records.map((row) => row.id));
    ElMessage.success($t('system.notice.deleteSuccess'));
    page.value = Math.min(
      page.value,
      Math.max(1, Math.ceil((total.value - records.length) / pageSize.value)),
    );
    await loadList();
  } catch {
    ElMessage.error($t('system.notice.deleteFailed'));
  } finally {
    deleting.value = false;
  }
}

function hasPermission(auth?: string | string[]) {
  return !auth || hasAccessByCodes(Array.isArray(auth) ? auth : [auth]);
}

function rowActions(row: SysNotice) {
  return [
    {
      text: $t('system.notice.readUsers'),
      icon: 'lucide:users',
      auth: NOTICE_PERMISSION.list,
      onClick: () => readersRef.value?.open(row),
    },
    {
      text: $t('common.edit'),
      icon: 'lucide:square-pen',
      auth: NOTICE_PERMISSION.edit,
      onClick: () => formRef.value?.open(row.id),
    },
    {
      text: $t('common.delete'),
      icon: 'lucide:trash-2',
      auth: NOTICE_PERMISSION.remove,
      danger: true,
      disabled: deleting.value,
      onClick: () => removeRows([row]),
    },
  ];
}

onMounted(loadList);
</script>

<template>
  <div class="notice-management m-4 rounded-xl border border-border bg-background p-4">
    <ElForm
      v-show="showSearch"
      :model="filters"
      :inline="true"
      :disabled="loading"
      @submit.prevent="search"
    >
      <ElFormItem :label="$t('system.notice.noticeTitle')"
        ><ElInput v-model="filters.noticeTitle" clearable class="w-52!" @keyup.enter="search"
      /></ElFormItem>
      <ElFormItem :label="$t('system.notice.publisher')"
        ><ElInput v-model="filters.createBy" clearable class="w-40!" @keyup.enter="search"
      /></ElFormItem>
      <ElFormItem :label="$t('system.notice.type')"
        ><ElSelect v-model="filters.noticeType" clearable class="w-36!"
          ><ElOption
            v-for="item in sys_notice_type"
            :key="item.value"
            :label="item.label"
            :value="item.value" /></ElSelect
      ></ElFormItem>
      <ElFormItem :label="$t('system.notice.status')"
        ><ElSelect v-model="filters.status" clearable class="w-36!"
          ><ElOption
            v-for="item in sys_notice_status"
            :key="item.value"
            :label="item.label"
            :value="item.value" /></ElSelect
      ></ElFormItem>
      <ElFormItem>
        <div class="flex gap-2">
          <VbenButton type="submit" size="sm" :disabled="loading"
            ><Search class="size-3.5" />{{ $t('common.search') }}</VbenButton
          >
          <VbenButton
            type="button"
            size="sm"
            variant="outline"
            :disabled="loading"
            @click="resetSearch"
            ><RotateCw class="size-3.5" />{{ $t('common.reset') }}</VbenButton
          >
        </div>
      </ElFormItem>
    </ElForm>
    <div class="mb-3 flex flex-wrap items-center justify-between gap-3">
      <VbenTableToolbar
        :actions="toolbarActions"
        :selected-count="selected.length"
        :loading="{ delete: deleting }"
        @create="formRef?.open()"
        @edit="editSelected"
        @delete="removeRows(selected)"
      />
      <div class="flex gap-1">
        <VbenIconButton :tooltip="$t('common.search')" @click="showSearch = !showSearch"
          ><Search class="size-4"
        /></VbenIconButton>
        <VbenIconButton :disabled="loading" :tooltip="$t('system.notice.refresh')" @click="loadList"
          ><RotateCw class="size-4" :class="{ 'animate-spin': loading }"
        /></VbenIconButton>
      </div>
    </div>
    <p v-if="loadFailed" class="mb-3 text-sm text-destructive">
      {{ $t('system.notice.loadFailed') }}
    </p>
    <ElTable
      ref="tableRef"
      v-loading="loading"
      :data="rows"
      row-key="id"
      @selection-change="selected = $event"
    >
      <ElTableColumn type="selection" width="46" align="center" />
      <ElTableColumn prop="id" :label="$t('system.notice.id')" width="90" align="center" />
      <ElTableColumn
        prop="noticeTitle"
        :label="$t('system.notice.noticeTitle')"
        min-width="260"
        show-overflow-tooltip
      >
        <template #default="{ row }">
          <VbenButton
            v-if="hasAccessByCodes([NOTICE_PERMISSION.query])"
            variant="link"
            class="h-auto max-w-full justify-start truncate p-0 font-normal"
            @click="showDetail(row as SysNotice)"
            >{{ row.noticeTitle }}</VbenButton
          >
          <span v-else>{{ row.noticeTitle }}</span>
        </template>
      </ElTableColumn>
      <ElTableColumn :label="$t('system.notice.type')" width="100" align="center"
        ><template #default="{ row }"
          ><DictTag :options="sys_notice_type" :value="row.noticeType" /></template
      ></ElTableColumn>
      <ElTableColumn :label="$t('system.notice.status')" width="100" align="center"
        ><template #default="{ row }"
          ><DictTag :options="sys_notice_status" :value="row.status" /></template
      ></ElTableColumn>
      <ElTableColumn
        prop="createBy"
        :label="$t('system.notice.publisher')"
        width="120"
        show-overflow-tooltip
      />
      <ElTableColumn :label="$t('system.notice.createdAt')" width="180"
        ><template #default="{ row }">{{ formatDateTime(row.createTime) }}</template></ElTableColumn
      >
      <ElTableColumn :label="$t('system.notice.actions')" fixed="right" width="250" align="center"
        ><template #default="{ row }"
          ><VbenTableAction
            :actions="rowActions(row as SysNotice)"
            :has-permission="hasPermission" /></template
      ></ElTableColumn>
    </ElTable>
    <div class="mt-4 flex justify-end overflow-x-auto">
      <ElPagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :total="total"
        :disabled="loading"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @current-change="loadList"
        @size-change="search"
      />
    </div>
    <NoticeForm ref="formRef" @success="loadList" />
    <NoticeDetailView ref="detailRef" />
    <ReadUsers ref="readersRef" />
  </div>
</template>
