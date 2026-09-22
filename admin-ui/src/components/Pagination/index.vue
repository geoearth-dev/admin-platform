<template>
  <div :class="{ hidden: hidden }" class="pagination-container">
    <el-pagination
      :background="background"
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :layout="layout"
      :page-sizes="pageSizes"
      :pager-count="pagerCount"
      :total="total"
      @change="handlePaginationChange"
    />
  </div>
</template>

<script setup lang="ts">
import { getLayoutScrollElement } from '@/utils/dom';
import { computed, type PropType } from 'vue';

const props = defineProps({
  total: {
    required: true,
    type: Number,
  },
  page: {
    type: Number,
    default: 1,
  },
  limit: {
    type: Number,
    default: 20,
  },
  pageSizes: {
    type: Array as PropType<number[]>,
    default: () => [10, 20, 30, 50],
  },
  // 移动端页码按钮的数量端默认值5
  pagerCount: {
    type: Number,
    default: document.body.clientWidth < 992 ? 5 : 7,
  },
  layout: {
    type: String,
    default: 'total, sizes, prev, pager, next, jumper',
  },
  background: {
    type: Boolean,
    default: true,
  },
  autoScroll: {
    type: Boolean,
    default: true,
  },
  hidden: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits<{
  'update:page': [page: number];
  'update:limit': [limit: number];
  pagination: [value: { page: number; limit: number }];
}>();
const currentPage = computed({
  get() {
    return props.page;
  },
  set(val: number) {
    emit('update:page', val);
  },
});
const pageSize = computed({
  get() {
    return props.limit;
  },
  set(val: number) {
    emit('update:limit', val);
  },
});

function handlePaginationChange(page: number, limit: number) {
  emit('pagination', { page, limit });

  getLayoutScrollElement()?.scrollTo({
    top: 0,
    behavior: 'smooth',
  });
}
</script>
