<template>
  <div
    class="app-container cache-page cache-manager"
    v-loading="loading || clearing"
    :element-loading-text="clearing ? '正在清理缓存，请稍候！' : '正在加载缓存列表，请稍候！'"
  >
    <el-row :gutter="16">
      <el-col :xs="24" :lg="8" class="cache-card">
        <el-card shadow="never" class="cache-list-card">
          <template #header>
            <div class="cache-card-header">
              <div class="cache-card-title">
                <Collection class="cache-card-icon" /><span>缓存列表</span>
              </div>
              <el-button
                :icon="Refresh"
                :loading="loading"
                link
                type="primary"
                aria-label="刷新缓存列表"
                @click="getCacheNames"
              />
            </div>
          </template>
          <el-table
            :data="cacheNames"
            height="100%"
            row-key="cacheName"
            highlight-current-row
            @row-click="selectCacheName"
          >
            <el-table-column label="序号" width="60" type="index" />
            <el-table-column
              label="缓存名称"
              prop="cacheName"
              min-width="140"
              show-overflow-tooltip
            >
              <template #default="{ row }">{{ row.cacheName.replace(/:$/, '') }}</template>
            </el-table-column>
            <el-table-column label="备注" prop="remark" min-width="110" show-overflow-tooltip />
            <el-table-column label="操作" width="64" align="center">
              <template #default="{ row }">
                <el-button
                  v-access="['monitor:cache:remove']"
                  :icon="Delete"
                  link
                  type="danger"
                  aria-label="清理此缓存分组"
                  @click.stop="handleClearCacheName(row)"
                />
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="8" class="cache-card">
        <el-card shadow="never" class="cache-list-card">
          <template #header>
            <div class="cache-card-header">
              <div class="cache-card-title">
                <Key class="cache-card-icon" /><span>键名列表</span
                ><span class="cache-count">{{ cacheKeys.length }}</span>
              </div>
              <el-button
                :icon="Refresh"
                :loading="subLoading"
                :disabled="!selectedName"
                link
                type="primary"
                aria-label="刷新键名列表"
                @click="loadCacheKeys(selectedName)"
              />
            </div>
          </template>
          <el-table
            v-loading="subLoading"
            :data="cacheKeys"
            height="100%"
            row-key="key"
            highlight-current-row
            :empty-text="selectedName ? '暂无缓存键' : '请先选择缓存分组'"
            @row-click="handleCacheValue"
          >
            <el-table-column label="序号" width="60" type="index" />
            <el-table-column label="缓存键名" prop="key" min-width="180" show-overflow-tooltip>
              <template #default="{ row }">{{ displayKey(row.key) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="64" align="center">
              <template #default="{ row }">
                <el-button
                  v-access="['monitor:cache:remove']"
                  :icon="Delete"
                  link
                  type="danger"
                  aria-label="删除此缓存键"
                  @click.stop="handleClearCacheKey(row.key)"
                />
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="8" class="cache-card">
        <el-card shadow="never" class="cache-list-card">
          <template #header>
            <div class="cache-card-header">
              <div class="cache-card-title">
                <Document class="cache-card-icon" /><span>缓存内容</span>
              </div>
              <el-button
                :icon="Refresh"
                :loading="valueLoading"
                :disabled="!selectedKey"
                link
                type="primary"
                aria-label="刷新缓存内容"
                @click="handleCacheValue({ key: selectedKey })"
              />
            </div>
          </template>
          <div v-loading="valueLoading" class="cache-value">
            <el-form v-if="cacheValue" label-position="top">
              <el-form-item label="缓存名称">
                <el-input :model-value="selectedName" readonly />
              </el-form-item>
              <el-form-item label="完整键名">
                <el-input :model-value="selectedKey" readonly />
              </el-form-item>
              <el-form-item label="缓存内容">
                <el-input
                  class="cache-value-input"
                  :model-value="formattedValue"
                  type="textarea"
                  :autosize="{ minRows: 12, maxRows: 24 }"
                  readonly
                />
              </el-form-item>
            </el-form>
            <el-empty
              v-else
              :description="valueFailed ? '缓存内容加载失败，请刷新重试' : '请选择缓存键查看内容'"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import type { SysCache } from '@/types/base/api/monitor/cache';
import {
  clearCacheKey,
  clearCacheName,
  getCacheValue,
  listCacheKey,
  listCacheName,
} from '@/api/monitor/cache';
import { Collection, Delete, Document, Key, Refresh } from '@element-plus/icons-vue';
import { ElEmpty, ElMessage, ElMessageBox, vLoading } from 'element-plus';
import { computed, onMounted, ref } from 'vue';

defineOptions({ name: 'CacheList' });

interface CacheKeyRow {
  key: string;
}

const cacheNames = ref<SysCache[]>([]);
const cacheKeys = ref<CacheKeyRow[]>([]);
const cacheValue = ref<SysCache | null>(null);
const selectedName = ref('');
const selectedKey = ref('');
const loading = ref(true);
const subLoading = ref(false);
const valueLoading = ref(false);
const valueFailed = ref(false);
const clearing = ref(false);
// 快速切换分组或键时，只接收最后一次查询结果。
let keysRequestId = 0;
let valueRequestId = 0;

const formattedValue = computed(() => {
  const value = cacheValue.value?.cacheValue ?? '';
  try {
    return JSON.stringify(JSON.parse(value), null, 2);
  } catch {
    return value;
  }
});

function resetValue() {
  valueRequestId++;
  selectedKey.value = '';
  cacheValue.value = null;
  valueLoading.value = false;
  valueFailed.value = false;
}

async function getCacheNames() {
  loading.value = true;
  keysRequestId++;
  selectedName.value = '';
  cacheKeys.value = [];
  subLoading.value = false;
  resetValue();
  try {
    cacheNames.value = await listCacheName();
  } catch {
    cacheNames.value = [];
  } finally {
    loading.value = false;
  }
}

function selectCacheName(row: SysCache) {
  selectedName.value = row.cacheName;
  void loadCacheKeys(row.cacheName);
}

async function loadCacheKeys(cacheName: string) {
  const requestId = ++keysRequestId;
  subLoading.value = true;
  cacheKeys.value = [];
  resetValue();
  try {
    const keys = await listCacheKey(cacheName);
    if (requestId === keysRequestId) {
      cacheKeys.value = keys.map((key) => ({ key }));
    }
  } catch {
    // 请求层统一提示错误；列表已清空，避免显示其他分组的数据。
  } finally {
    if (requestId === keysRequestId) subLoading.value = false;
  }
}

function displayKey(key: string) {
  return key.startsWith(selectedName.value) ? key.slice(selectedName.value.length) : key;
}

async function handleCacheValue(row: CacheKeyRow) {
  const requestId = ++valueRequestId;
  const cacheName = selectedName.value;
  selectedKey.value = row.key;
  cacheValue.value = null;
  valueLoading.value = true;
  valueFailed.value = false;
  try {
    const value = await getCacheValue(cacheName, row.key);
    if (requestId === valueRequestId) cacheValue.value = value;
  } catch {
    if (requestId === valueRequestId) valueFailed.value = true;
  } finally {
    if (requestId === valueRequestId) valueLoading.value = false;
  }
}

async function handleClearCacheName(row: SysCache) {
  const confirmed = await ElMessageBox.confirm(
    `确认清理分组“${row.cacheName}”下的全部缓存？`,
    '清理缓存分组',
    { type: 'warning' },
  ).then(
    () => true,
    () => false,
  );
  if (!confirmed) return;
  clearing.value = true;
  try {
    await clearCacheName(row.cacheName);
    ElMessage.success('缓存分组已清理');
    if (selectedName.value === row.cacheName) await loadCacheKeys(row.cacheName);
  } catch {
    // 请求层统一提示错误。
  } finally {
    clearing.value = false;
  }
}

async function handleClearCacheKey(key: string) {
  const cacheName = selectedName.value;
  const confirmed = await ElMessageBox.confirm(`确认删除缓存键“${key}”？`, '删除缓存键', {
    type: 'warning',
  }).then(
    () => true,
    () => false,
  );
  if (!confirmed) return;
  clearing.value = true;
  try {
    // 始终提交列表中的完整键名，详情接口返回的键名已去除前缀。
    await clearCacheKey(key);
    ElMessage.success('缓存键已删除');
    if (selectedName.value === cacheName) await loadCacheKeys(cacheName);
  } catch {
    // 请求层统一提示错误。
  } finally {
    clearing.value = false;
  }
}

onMounted(getCacheNames);
</script>

<style scoped src="./cache.css"></style>
<style scoped>
.cache-list-card {
  height: max(480px, calc(var(--vben-content-height, 100dvh - 120px) - 56px));
  display: flex;
  flex-direction: column;
}

.cache-list-card :deep(.el-card__header) {
  flex-shrink: 0;
}

.cache-list-card :deep(.el-card__body) {
  flex: 1;
  min-height: 0;
  overflow: auto;
  padding: 12px;
}

.cache-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.cache-count {
  color: var(--el-text-color-secondary);
  font-size: 13px;
  font-weight: normal;
}

.cache-value {
  min-height: 100%;
}

.cache-value-input :deep(textarea) {
  font-family: ui-monospace, SFMono-Regular, Consolas, monospace;
  line-height: 1.6;
}

@media (max-width: 1199px) {
  .cache-list-card {
    height: 480px;
  }
}
</style>
