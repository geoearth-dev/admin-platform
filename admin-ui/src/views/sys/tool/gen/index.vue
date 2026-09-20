<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryRef"
      :inline="true"
      v-show="showSearch"
    >
      <el-form-item
        label="表名称"
        prop="tableName"
      >
        <el-input
          v-model="queryParams.tableName"
          placeholder="请输入表名称"
          clearable
          style="width: 200px"
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
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item
        label="创建时间"
        style="width: 308px"
      >
        <el-date-picker
          v-model="dateRange"
          value-format="YYYY-MM-DD"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
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

    <el-row
      :gutter="10"
      class="mb8"
    >
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          :icon="Download"
          :disabled="!selection.length"
          @click="handleGenTable()"
          v-access:code="['tool:gen:code']"
          >生成</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          :icon="Plus"
          @click="createRef?.show()"
          v-access:role="['admin']"
          >创建</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          :icon="Upload"
          @click="importRef?.show()"
          v-access:code="['tool:gen:import']"
          >导入</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          :icon="Edit"
          :disabled="selection.length !== 1"
          @click="handleEditTable()"
          v-access:code="['tool:gen:edit']"
          >修改</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          :icon="Delete"
          :disabled="!selection.length"
          @click="handleDelete()"
          v-access:code="['tool:gen:remove']"
          >删除</el-button
        >
      </el-col>
      <right-toolbar
        v-model:showSearch="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>

    <el-table
      ref="genRef"
      v-loading="loading"
      :data="tableList"
      @selection-change="handleSelectionChange"
      :default-sort="defaultSort"
      @sort-change="handleSortChange"
    >
      <Column
        type="selection"
        align="center"
        width="55"
      ></Column>
      <Column
        label="序号"
        type="index"
        width="50"
        align="center"
      >
        <template #default="scope">
          <span>{{
            (queryParams.pageNum - 1) * queryParams.pageSize + scope.$index + 1
          }}</span>
        </template>
      </Column>
      <Column
        label="表名称"
        align="center"
        prop="tableName"
        :show-overflow-tooltip="true"
      />
      <Column
        label="表描述"
        align="center"
        prop="tableComment"
        :show-overflow-tooltip="true"
      />
      <Column
        label="实体"
        align="center"
        prop="className"
        :show-overflow-tooltip="true"
      />
      <Column
        label="创建时间"
        align="center"
        prop="createTime"
        width="160"
        sortable="custom"
        :sort-orders="['descending', 'ascending']"
      />
      <Column
        label="更新时间"
        align="center"
        prop="updateTime"
        width="160"
        sortable="custom"
        :sort-orders="['descending', 'ascending']"
      />
      <Column
        label="操作"
        align="center"
        width="330"
        class-name="small-padding fixed-width"
      >
        <template #default="scope">
          <el-tooltip
            content="预览"
            placement="top"
          >
            <el-button
              link
              type="primary"
              :icon="View"
              @click="handlePreview(scope.row)"
              v-access:code="['tool:gen:preview']"
            ></el-button>
          </el-tooltip>
          <el-tooltip
            content="编辑"
            placement="top"
          >
            <el-button
              link
              type="primary"
              :icon="Edit"
              @click="handleEditTable(scope.row)"
              v-access:code="['tool:gen:edit']"
            ></el-button>
          </el-tooltip>
          <el-tooltip
            content="删除"
            placement="top"
          >
            <el-button
              link
              type="primary"
              :icon="Delete"
              @click="handleDelete(scope.row)"
              v-access:code="['tool:gen:remove']"
            ></el-button>
          </el-tooltip>
          <el-tooltip
            content="同步"
            placement="top"
          >
            <el-button
              link
              type="primary"
              :icon="Refresh"
              @click="handleSynchDb(scope.row)"
              v-access:code="['tool:gen:edit']"
            ></el-button>
          </el-tooltip>
          <el-tooltip
            content="生成代码"
            placement="top"
          >
            <el-button
              link
              type="primary"
              :icon="Download"
              @click="handleGenTable(scope.row)"
              v-access:code="['tool:gen:code']"
            ></el-button>
          </el-tooltip>
        </template>
      </Column>
    </el-table>
    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
    <!-- 预览界面 -->
    <el-dialog
      :title="preview.title"
      v-model="preview.open"
      width="80%"
      top="5vh"
      append-to-body
      class="scrollbar"
    >
      <el-tabs v-model="preview.activeName">
        <el-tab-pane
          v-for="(value, key) in preview.data"
          :label="String(key).split('/').pop()"
          :name="String(key)"
          :key="key"
        >
          <el-link
            underline="never"
            :icon="DocumentCopy"
            @click="copyCode(value)"
            style="float: right"
            >&nbsp;复制</el-link
          >
          <pre>{{ value }}</pre>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
    <import-table
      ref="importRef"
      @ok="handleQuery"
    />
    <create-table
      ref="createRef"
      @ok="handleQuery"
    />
  </div>
</template>

<script setup lang="ts">
import { ElTableColumn } from 'element-plus'
const Column = ElTableColumn<GenTable>
import { onActivated, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import {
  Search,
  Refresh,
  Download,
  Plus,
  Upload,
  Edit,
  Delete,
  View,
  DocumentCopy,
} from '@element-plus/icons-vue'
import { useClipboard } from '@vueuse/core'
import {
  listTable,
  previewTable,
  delTable,
  genCode,
  synchDb,
  downloadCode,
} from '@/api/tool/gen'
import type { GenTable, GenQueryParams } from '@/types/base/api/tool/gen'
import { downloadFileFromBlob } from '@/utils/download'
import ImportTable from './importTable.vue'
import CreateTable from './createTable.vue'

defineOptions({ name: 'Gen' })
const router = useRouter()
const { copy } = useClipboard({ legacy: true })
const tableList = ref<GenTable[]>([])
const selection = ref<GenTable[]>([])
const loading = ref(false)
const showSearch = ref(true)
const total = ref(0)
const dateRange = ref<[string, string] | null>(null)
const queryRef = ref<FormInstance>()
const importRef = ref<InstanceType<typeof ImportTable>>()
const createRef = ref<InstanceType<typeof CreateTable>>()
const defaultSort = { prop: 'createTime', order: 'descending' as const }
const queryParams = reactive<GenQueryParams>({
  pageNum: 1,
  pageSize: 10,
  orderByColumn: 'createTime',
  isAsc: 'descending',
})
const preview = reactive({
  open: false,
  title: '代码预览',
  data: {} as Record<string, string>,
  activeName: '',
})
let requestId = 0
let mounted = false
async function getList(): Promise<void> {
  const current = ++requestId
  loading.value = true
  try {
    const result = await listTable({
      ...queryParams,
      params: dateRange.value
        ? { beginTime: dateRange.value[0], endTime: dateRange.value[1] }
        : {},
    })
    if (current !== requestId) return
    tableList.value = result.records
    total.value = result.total
    selection.value = []
  } finally {
    if (current === requestId) loading.value = false
  }
}
function handleQuery(): void {
  queryParams.pageNum = 1
  void getList()
}
function resetQuery(): void {
  queryRef.value?.resetFields()
  dateRange.value = null
  handleQuery()
}
function handleSelectionChange(rows: GenTable[]): void {
  selection.value = rows
}
function handleSortChange(column: {
  prop: string | null
  order: 'ascending' | 'descending' | null
}): void {
  queryParams.orderByColumn =
    column.prop === 'updateTime' ? 'updateTime' : 'createTime'
  queryParams.isAsc = column.order ?? 'descending'
  handleQuery()
}
async function handleGenTable(row?: GenTable): Promise<void> {
  const rows = row ? [row] : selection.value
  if (!rows.length) {
    ElMessage.warning('请选择要生成的数据')
    return
  }
  if (row?.genType === '1') {
    await genCode(row.tableName)
    ElMessage.success(`已生成到服务端路径：${row.genPath}`)
  } else {
    const blob = await downloadCode(rows.map((item) => item.tableName))
    downloadFileFromBlob({
      source: blob,
      fileName:
        rows.length === 1 ? `${rows[0]?.tableName}.zip` : 'generated-code.zip',
    })
  }
}
async function handleSynchDb(row: GenTable): Promise<void> {
  try {
    await ElMessageBox.confirm(`确认同步“${row.tableName}”表结构？`, '提示', {
      type: 'warning',
    })
  } catch {
    return
  }
  await synchDb(row.tableName)
  ElMessage.success('同步成功')
  await getList()
}
async function handlePreview(row: GenTable): Promise<void> {
  preview.data = await previewTable(row.id)
  preview.activeName = Object.keys(preview.data)[0] ?? ''
  preview.open = true
}
async function copyCode(value: string): Promise<void> {
  try {
    await copy(value)
    ElMessage.success('复制成功')
  } catch {
    ElMessage.error('复制失败，请手动选择代码复制')
  }
}
function handleEditTable(row?: GenTable): void {
  const target = row ?? selection.value[0]
  if (target)
    void router.push({
      name: 'GenEdit',
      params: { id: target.id },
      query: { pageNum: queryParams.pageNum },
    })
}
async function handleDelete(row?: GenTable): Promise<void> {
  const rows = row ? [row] : selection.value
  if (!rows.length) return
  try {
    await ElMessageBox.confirm(
      '确认删除选中的生成配置？不会删除业务表。',
      '提示',
      { type: 'warning' },
    )
  } catch {
    return
  }
  await delTable(rows.map((item) => item.id))
  ElMessage.success('删除成功')
  await getList()
}
onMounted(() => {
  mounted = true
  void getList()
})
onActivated(() => {
  if (mounted && !loading.value) void getList()
})
</script>
