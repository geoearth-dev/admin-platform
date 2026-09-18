<template>
  <el-drawer
    class="dict-detail-drawer"
    :model-value="visible"
    direction="rtl"
    size="700px"
    append-to-body
    @update:model-value="$emit('update:visible', $event)"
  >
    <!-- 自定义标题 -->
    <template #header>
      <div class="drawer-head">
        <el-icon class="drawer-head-icon">
          <List />
        </el-icon>
        <span class="drawer-head-name">{{ row.dictName }}</span>
        <span class="drawer-head-type">{{ row.dictType }}</span>
      </div>
    </template>

    <div class="drawer-wrap">
      <!-- 加载中 -->
      <div v-if="loading" class="drawer-loading">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>加载中...</span>
      </div>

      <!-- 空数据 -->
      <div v-else-if="!dataList.length" class="drawer-empty">
        <el-icon style="font-size: 36px"><Document /></el-icon>
        <div>暂无字典数据</div>
      </div>

      <template v-else>
        <!-- 统计卡片 -->
        <el-row :gutter="12" class="stat-row">
          <el-col :span="disabledCount > 0 ? 8 : 12">
            <div class="stat-card">
              <div class="stat-num">{{ dataList.length }}</div>
              <div class="stat-label">共计条目</div>
            </div>
          </el-col>
          <el-col :span="disabledCount > 0 ? 8 : 12">
            <div class="stat-card">
              <div class="stat-num success">{{ normalCount }}</div>
              <div class="stat-label">正常</div>
            </div>
          </el-col>
          <el-col v-if="disabledCount > 0" :span="8">
            <div class="stat-card">
              <div class="stat-num danger">{{ disabledCount }}</div>
              <div class="stat-label">停用</div>
            </div>
          </el-col>
        </el-row>

        <!-- 数据列表 -->
        <div v-for="item in dataList" :key="item.id" class="dict-item">
          <div class="dict-cell">
            <div class="dict-cell-key">标签</div>
            <div class="dict-cell-val">
              <el-tag
                v-if="item.listClass && item.listClass !== 'default'"
                :type="item.listClass === 'primary' ? undefined : item.listClass"
                size="small"
                >{{ item.dictLabel }}</el-tag
              >
              <span v-else>{{ item.dictLabel }}</span>
            </div>
          </div>
          <div class="dict-cell">
            <div class="dict-cell-key">键值</div>
            <div class="dict-cell-val">{{ item.dictValue }}</div>
          </div>
          <div class="dict-cell">
            <div class="dict-cell-key">状态</div>
            <div class="dict-cell-val">
              <dict-tag :options="sys_normal_disable" :value="item.status" />
            </div>
          </div>
        </div>
      </template>
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import { listData } from '@/api/system/dict/data';
import type { SysDictData, SysDictType } from '@/types/base/api/system/dict';
import { computed, ref, watch } from 'vue';
import { Document, List, Loading } from '@element-plus/icons-vue';
import { useDict } from '@/utils/dict';
const { sys_normal_disable } = useDict('sys_normal_disable');
const props = defineProps<{
  visible: boolean;
  row: SysDictType;
}>();
defineEmits<{
  'update:visible': [value: boolean];
}>();

const loading = ref<boolean>(false);
const dataList = ref<SysDictData[]>([]);

const normalCount = computed(() => dataList.value.filter((item) => item.status === '1').length);
const disabledCount = computed(() => dataList.value.filter((item) => item.status === '0').length);
watch(
  [() => props.visible, () => props.row.dictType],
  ([visible]) => {
    if (visible) {
      loadData();
    } else {
      dataList.value = [];
    }
  },
  { immediate: true },
);
function loadData() {
  if (!props.row?.dictType) return;
  loading.value = true;
  dataList.value = [];
  listData({ dictType: props.row.dictType, pageSize: 100, pageNum: 1 })
    .then((response) => {
      dataList.value = response.records || [];
    })
    .catch(() => {})
    .finally(() => {
      loading.value = false;
    });
}
</script>

<style scoped>
:global(.dict-detail-drawer) {
  --dict-drawer-title-color: var(--el-text-color-primary);
  --dict-drawer-subtext-color: var(--el-text-color-secondary);
  --dict-drawer-card-bg: var(--el-fill-color-light);
  --dict-drawer-border-color: var(--el-border-color);
  --dict-drawer-cell-divider: var(--el-border-color-light);
}
.drawer-head-icon {
  margin-right: 8px;
  color: var(--el-color-primary);
}
.drawer-head {
  display: flex;
  align-items: center;
}
.drawer-head-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--dict-drawer-title-color, #2c3e50);
  margin-right: 8px;
}
.drawer-head-type {
  font-size: 14px;
  color: var(--dict-drawer-subtext-color, #95a5a6);
  font-family: monospace;
}
.drawer-wrap {
  padding: 0 20px 20px;
}
.drawer-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 120px;
  color: var(--dict-drawer-subtext-color, #aaa);
  font-size: 13px;
  gap: 8px;
}
.drawer-empty {
  text-align: center;
  color: var(--dict-drawer-subtext-color, #bbb);
  padding: 60px 0;
  font-size: 13px;
}
.drawer-empty .el-icon {
  display: block;
  margin: 0 auto 8px;
}
.stat-row {
  margin-bottom: 16px;
}
.stat-card {
  background: var(--dict-drawer-card-bg, #f7f9fb);
  border: 1px solid var(--dict-drawer-border-color, #e8ecf0);
  border-radius: 6px;
  padding: 10px 14px;
  text-align: center;
}
.stat-num {
  font-size: 22px;
  font-weight: 700;
  color: var(--dict-drawer-title-color, #2c3e50);
}
.stat-num.success {
  color: var(--el-color-success);
}
.stat-num.danger {
  color: var(--el-color-danger);
}
.stat-label {
  font-size: 11px;
  color: var(--dict-drawer-subtext-color, #95a5a6);
  margin-top: 4px;
}
.dict-item {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  border: 1px solid var(--dict-drawer-border-color, #e8ecf0);
  border-radius: 6px;
  overflow: hidden;
  margin-bottom: 8px;
}
.dict-cell {
  display: grid;
  grid-template-columns: 70px 1fr;
  border-right: 1px solid var(--dict-drawer-cell-divider, #f0f4f8);
}
.dict-cell:last-child {
  border-right: 0;
}
.dict-cell-key {
  padding: 9px 14px;
  font-size: 12px;
  color: var(--dict-drawer-subtext-color, #888);
  background: var(--dict-drawer-card-bg, #f7f9fb);
  border-right: 1px solid var(--dict-drawer-cell-divider, #f0f4f8);
}
.dict-cell-val {
  padding: 9px 14px;
  font-size: 13px;
  color: var(--dict-drawer-title-color, #2c3e50);
  word-break: break-all;
  display: flex;
  align-items: center;
}
</style>
