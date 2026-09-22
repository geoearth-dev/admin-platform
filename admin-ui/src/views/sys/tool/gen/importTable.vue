<template>
  <!-- 导入表 -->
  <el-dialog
    title="导入表"
    v-model="visible"
    width="800px"
    top="5vh"
    append-to-body
  >
    <el-form
      :model="queryParams"
      ref="queryRef"
      :inline="true"
    >
      <el-form-item
        label="表名称"
        prop="tableName"
      >
        <el-input
          v-model="queryParams.tableName"
          placeholder="请输入表名称"
          clearable
          style="width: 180px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item
        label="表描述"
        prop="tableComment"
      >
        <el-input
          v-model="queryParams.tableComment"
          placeholder="请输入表描述"
          clearable
          style="width: 180px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          :icon="Search"
          @click="handleQuery"
          >搜索</el-button
        >
        <el-button
          :icon="Refresh"
          @click="resetQuery"
          >重置</el-button
        >
      </el-form-item>
    </el-form>
    <el-row>
      <el-table
        v-loading="loading"
        @row-click="clickRow"
        ref="table"
        :data="dbTableList"
        @selection-change="handleSelectionChange"
        height="260px"
      >
        <el-table-column
          type="selection"
          width="55"
        ></el-table-column>
        <el-table-column
          prop="tableName"
          label="表名称"
          :show-overflow-tooltip="true"
        ></el-table-column>
        <el-table-column
          prop="tableComment"
          label="表描述"
          :show-overflow-tooltip="true"
        ></el-table-column>
        <el-table-column
          prop="createTime"
          label="创建时间"
          width="180"
        >
          <template #default="{ row }">{{ formatDateTime(row.createTime) || '—' }}</template>
        </el-table-column>
        <el-table-column
          prop="updateTime"
          label="更新时间"
          width="180"
        >
          <template #default="{ row }">{{ formatDateTime(row.updateTime) || '—' }}</template>
        </el-table-column>
      </el-table>
      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </el-row>
    <template #footer>
      <div class="dialog-footer">
        <el-button
          type="primary"
          :loading="saving"
          @click="handleImportTable"
          >确 定</el-button
        >
        <el-button @click="visible = false">取 消</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, TableInstance } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { listDbTable, importTable } from '@/api/tool/gen'
import type { DbTable, GenQueryParams } from '@/types/base/api/tool/gen'
import { formatDateTime } from '@/utils/date'
const emit = defineEmits<{ ok: [] }>()
const total = ref(0),
  visible = ref(false),
  loading = ref(false),
  saving = ref(false)
const dbTableList = ref<DbTable[]>([]),
  tables = ref<string[]>([])
const queryRef = ref<FormInstance>(),
  table = ref<TableInstance>()
const queryParams = reactive<GenQueryParams>({ pageNum: 1, pageSize: 10 })
function show(): void {
  visible.value = true
  tables.value = []
  queryParams.pageNum = 1
  void getList()
}
function clickRow(row: DbTable): void {
  table.value?.toggleRowSelection(row)
}
function handleSelectionChange(rows: DbTable[]): void {
  tables.value = rows.map((item) => item.tableName)
}
let requestId = 0
async function getList(): Promise<void> {
  const id = ++requestId
  loading.value = true
  try {
    const result = await listDbTable({ ...queryParams })
    if (id === requestId) {
      dbTableList.value = result.records
      total.value = result.total
      tables.value = []
    }
  } finally {
    if (id === requestId) loading.value = false
  }
}
function handleQuery(): void {
  queryParams.pageNum = 1
  void getList()
}
function resetQuery(): void {
  queryRef.value?.resetFields()
  handleQuery()
}
async function handleImportTable(): Promise<void> {
  if (!tables.value.length) {
    ElMessage.warning('请选择要导入的表')
    return
  }
  saving.value = true
  try {
    await importTable(tables.value)
    ElMessage.success('导入成功')
    visible.value = false
    emit('ok')
  } finally {
    saving.value = false
  }
}
defineExpose({ show })
</script>
