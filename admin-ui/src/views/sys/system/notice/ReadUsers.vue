<script setup lang="ts">
import type { NoticeReadUser, SysNotice } from '@/types/base/api/system/notice';
import { ref } from 'vue';
import { ElInput, ElPagination, ElTable, ElTableColumn } from 'element-plus';
import { Search } from '@/assets/icons';
import { listNoticeReadUsers } from '@/api/system/notice';
import { $t } from '@/plugins/locale';
import { useVbenModal } from '@/plugins/vben-ui/popup-ui';
import { VbenButton } from '@/plugins/vben-ui/shadcn-ui';
import { formatDateTime } from '@/utils/date';

const notice = ref<SysNotice>();
const rows = ref<NoticeReadUser[]>([]);
const total = ref(0);
const page = ref(1);
const pageSize = ref(10);
const keyword = ref('');
const loading = ref(false);
const failed = ref(false);
let requestId = 0;
const [Modal, modalApi] = useVbenModal({
  footer: false,
  onClosed() {
    requestId++;
  },
});

function open(value: SysNotice) {
  notice.value = value;
  rows.value = [];
  total.value = 0;
  page.value = 1;
  keyword.value = '';
  modalApi.open();
  void loadList();
}

async function loadList() {
  if (!notice.value) return;
  const currentRequest = ++requestId;
  loading.value = true;
  failed.value = false;
  try {
    const result = await listNoticeReadUsers({
      noticeId: notice.value.id,
      searchValue: keyword.value || undefined,
      pageNum: page.value,
      pageSize: pageSize.value,
    });
    if (currentRequest !== requestId) return;
    rows.value = result.records;
    total.value = result.total;
  } catch {
    if (currentRequest === requestId) failed.value = true;
  } finally {
    if (currentRequest === requestId) loading.value = false;
  }
}

function search() {
  page.value = 1;
  void loadList();
}
function reset() {
  keyword.value = '';
  search();
}
defineExpose({ open });
</script>

<template>
  <Modal
    :title="$t('system.notice.readUsersTitle', { title: notice?.noticeTitle ?? '' })"
    class="w-[900px] max-w-[95vw]"
  >
    <form class="mb-4 flex flex-wrap items-center gap-2" @submit.prevent="search">
      <ElInput
        v-model="keyword"
        :disabled="loading"
        :placeholder="$t('system.notice.readerPlaceholder')"
        clearable
        class="w-64!"
      />
      <VbenButton type="submit" size="sm" :disabled="loading"
        ><Search class="size-3.5" />{{ $t('common.search') }}</VbenButton
      >
      <VbenButton type="button" size="sm" variant="outline" :disabled="loading" @click="reset">{{
        $t('common.reset')
      }}</VbenButton>
      <span class="ml-auto text-xs text-muted-foreground">{{
        $t('system.notice.readCount', { count: total })
      }}</span>
    </form>
    <p v-if="failed" class="mb-3 text-sm text-destructive">
      {{ $t('system.notice.loadFailed') }}
    </p>
    <ElTable v-loading="loading" :data="rows" height="360" stripe>
      <ElTableColumn
        prop="userName"
        :label="$t('system.notice.loginName')"
        min-width="110"
        show-overflow-tooltip
      />
      <ElTableColumn
        prop="nickName"
        :label="$t('system.notice.nickName')"
        min-width="110"
        show-overflow-tooltip
      />
      <ElTableColumn
        prop="deptName"
        :label="$t('system.notice.department')"
        min-width="120"
        show-overflow-tooltip
      />
      <ElTableColumn prop="phoneNumber" :label="$t('system.notice.phoneNumber')" width="140" />
      <ElTableColumn :label="$t('system.notice.readTime')" width="180"
        ><template #default="{ row }">{{ formatDateTime(row.readTime) }}</template></ElTableColumn
      >
    </ElTable>
    <div class="mt-4 flex justify-end overflow-x-auto">
      <ElPagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :total="total"
        :disabled="loading"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        background
        @current-change="loadList"
        @size-change="search"
      />
    </div>
  </Modal>
</template>
