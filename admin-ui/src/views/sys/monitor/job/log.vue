<template>
  <div class="app-container">
    <el-form
      @submit.prevent="handleQuery"
      :disabled="loading"
      :model="queryParams"
      ref="queryRef"
      :inline="true"
      v-show="showSearch"
      label-width="68px"
    >
      <el-form-item
        label="任务名称"
        prop="jobName"
      >
        <el-input
          v-model="queryParams.jobName"
          placeholder="请输入任务名称"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item
        label="任务组名"
        prop="jobGroup"
      >
        <el-select
          v-model="queryParams.jobGroup"
          placeholder="请选择任务组名"
          clearable
          style="width: 240px"
        >
          <el-option
            v-for="dict in sys_job_group"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item
        label="执行状态"
        prop="status"
      >
        <el-select
          v-model="queryParams.status"
          placeholder="请选择执行状态"
          clearable
          style="width: 240px"
        >
          <el-option
            v-for="dict in statusOptions"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item
        label="执行时间"
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
          type="danger"
          plain
          :icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-access="['monitor:job:remove']"
          >删除</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          :icon="Delete"
          @click="handleClean"
          v-access="['monitor:job:remove']"
          >清空</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          :icon="Download"
          :loading="exporting"
          @click="handleExport"
          v-access="['monitor:job:export']"
          >导出</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          :icon="Close"
          @click="handleClose"
          >关闭</el-button
        >
      </el-col>
      <right-toolbar
        v-model:showSearch="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>

    <el-table
      row-key="id"
      v-loading="loading"
      :data="jobLogList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column
        type="selection"
        width="55"
        align="center"
      />
      <el-table-column
        label="日志编号"
        width="80"
        align="center"
        prop="id"
      />
      <el-table-column
        label="任务名称"
        align="center"
        prop="jobName"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        label="任务组名"
        align="center"
        prop="jobGroup"
        :show-overflow-tooltip="true"
      >
        <template #default="scope">
          <dict-tag
            :options="sys_job_group"
            :value="scope.row.jobGroup"
          />
        </template>
      </el-table-column>
      <el-table-column
        label="调用目标字符串"
        align="center"
        prop="invokeTarget"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        label="日志信息"
        align="center"
        prop="jobMessage"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        label="执行状态"
        align="center"
        prop="status"
      >
        <template #default="scope">
          <el-tag :type="scope.row.status === '1' ? 'success' : 'danger'">{{
            scope.row.status === '1' ? '成功' : '失败'
          }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        label="执行时间"
        align="center"
        prop="createTime"
        width="180"
      >
        <template #default="scope">
          <span>{{ formatDateTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template #default="scope">
          <el-button
            link
            type="primary"
            :icon="View"
            @click="handleView(scope.row)"
            v-access="['monitor:job:query']"
            >详细</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 调度日志详细 -->
    <job-detail
      v-if="detailLog"
      v-model:visible="open"
      :row="detailLog"
      type="log"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import {
  Close,
  Delete,
  Download,
  Refresh,
  Search,
  View,
} from '@element-plus/icons-vue'
import JobDetail from './detail.vue'
import DictTag from '@/components/DictTag/index.vue'
import Pagination from '@/components/Pagination/index.vue'
import RightToolbar from '@/components/RightToolbar/index.vue'
import { getJob } from '@/api/monitor/job'
import {
  listJobLog,
  getJobLog,
  delJobLog,
  cleanJobLog,
  exportJobLog,
} from '@/api/monitor/jobLog'
import type {
  SysJobLog,
  JobLogQueryParams,
} from '@/types/base/api/monitor/jobLog'
import { useTabs } from '@/plugins/effects/hooks'
import { useDict } from '@/utils/dict'
import { formatDateTime } from '@/utils/date'
import { downloadFileFromBlob } from '@/utils/download'

defineOptions({ name: 'JobLog' })

const route = useRoute()
const router = useRouter()
const { closeCurrentTab } = useTabs()
const { sys_job_group } = useDict('sys_job_group')
const statusOptions = [
  { value: '1', label: '成功' },
  { value: '0', label: '失败' },
]
const queryRef = ref<FormInstance>()
const queryParams = reactive<JobLogQueryParams>({ pageNum: 1, pageSize: 10 })
const jobLogList = ref<SysJobLog[]>([])
const selectedIds = ref<number[]>([])
const total = ref(0)
const loading = ref(false)
const exporting = ref(false)
const showSearch = ref(true)
const open = ref(false)
const detailLog = ref<SysJobLog>()
const dateRange = ref<[string, string] | null>(null)
const multiple = computed(() => selectedIds.value.length === 0)

function getQuery(): JobLogQueryParams {
  return {
    ...queryParams,
    params: dateRange.value
      ? { beginTime: dateRange.value[0], endTime: dateRange.value[1] }
      : undefined,
  }
}

async function getList() {
  loading.value = true
  try {
    const result = await listJobLog(getQuery())
    jobLogList.value = result.records
    total.value = result.total
    selectedIds.value = []
  } catch {
    jobLogList.value = []
    total.value = 0
    // 请求层统一提示错误。
  } finally {
    loading.value = false
  }
}

function handleQuery() {
  queryParams.pageNum = 1
  return getList()
}

function resetQuery() {
  queryRef.value?.resetFields()
  dateRange.value = null
  return handleQuery()
}

function handleSelectionChange(selection: SysJobLog[]) {
  selectedIds.value = selection.map((item) => item.id)
}

async function handleView(row: SysJobLog) {
  detailLog.value = await getJobLog(row.id)
  open.value = true
}

async function handleClose() {
  const currentTab = router.currentRoute.value
  const failure = await router.push('/monitor/job')
  if (!failure) await closeCurrentTab(currentTab)
}

async function handleDelete() {
  const ids = [...selectedIds.value]
  if (!ids.length) return
  const confirmed = await ElMessageBox.confirm(
    `确认删除日志编号为“${ids.join('、')}”的数据吗？`,
    '删除日志',
  ).then(
    () => true,
    () => false,
  )
  if (!confirmed) return
  await delJobLog(ids)
  ElMessage.success('删除成功')
  if (ids.length === jobLogList.value.length && queryParams.pageNum > 1)
    queryParams.pageNum--
  await getList()
}

async function handleClean() {
  const confirmed = await ElMessageBox.confirm(
    '确认清空所有调度日志吗？',
    '清空日志',
  ).then(
    () => true,
    () => false,
  )
  if (!confirmed) return
  await cleanJobLog()
  ElMessage.success('清空成功')
  await handleQuery()
}

async function handleExport() {
  exporting.value = true
  try {
    const blob = await exportJobLog(getQuery())
    downloadFileFromBlob({
      source: blob,
      fileName: `job_log_${Date.now()}.xlsx`,
    })
  } finally {
    exporting.value = false
  }
}

async function loadRoute() {
  if (route.name !== 'JobLog') return
  loading.value = true
  jobLogList.value = []
  total.value = 0
  queryParams.pageNum = 1
  queryParams.jobName = undefined
  queryParams.jobGroup = undefined
  queryParams.status = undefined
  dateRange.value = null
  try {
    const id = Number(route.params.jobId)
    if (id > 0) {
      const job = await getJob(id)
      queryParams.jobName = job.jobName
      queryParams.jobGroup = job.jobGroup
    }
    await getList()
  } catch {
    // 任务查询失败时保留空列表，请求层统一提示错误。
  } finally {
    loading.value = false
  }
}

watch(() => route.params.jobId, loadRoute, { immediate: true })
</script>
