<template>
  <div class="app-container">
    <el-form
      @submit.prevent="handleQuery"
      :disabled="loading"
      :model="queryParams"
      ref="queryRef"
      :inline="true"
      v-show="showSearch"
    >
      <el-form-item
        label="任务名称"
        prop="jobName"
      >
        <el-input
          v-model="queryParams.jobName"
          placeholder="请输入任务名称"
          clearable
          style="width: 200px"
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
          style="width: 200px"
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
        label="任务状态"
        prop="status"
      >
        <el-select
          v-model="queryParams.status"
          placeholder="请选择任务状态"
          clearable
          style="width: 200px"
        >
          <el-option
            v-for="dict in statusOptions"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
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
          :icon="Plus"
          @click="handleAdd"
          v-access="['monitor:job:add']"
          >新增</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          :icon="Edit"
          :disabled="single"
          @click="handleUpdate()"
          v-access="['monitor:job:edit']"
          >修改</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          :icon="Delete"
          :disabled="multiple"
          @click="handleDelete()"
          v-access="['monitor:job:remove']"
          >删除</el-button
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
          type="info"
          plain
          :icon="Operation"
          @click="handleJobLog()"
          v-access="['monitor:job:list']"
          >日志</el-button
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
      :data="jobList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column
        type="selection"
        width="55"
        align="center"
      />
      <el-table-column
        label="任务编号"
        width="100"
        align="center"
        prop="id"
      />
      <el-table-column
        label="任务名称"
        align="center"
        :show-overflow-tooltip="true"
      >
        <template #default="scope">
          <el-link
            type="primary"
            underline="never"
            :disabled="!hasAccessByCodes(['monitor:job:query'])"
            @click="handleView(scope.row)"
            >{{ scope.row.jobName }}</el-link
          >
        </template>
      </el-table-column>
      <el-table-column
        label="任务组名"
        align="center"
        prop="jobGroup"
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
        label="cron执行表达式"
        align="center"
        prop="cronExpression"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        label="状态"
        align="center"
      >
        <template #default="scope">
          <el-switch
            class="job-status-switch"
            v-model="scope.row.status"
            active-value="1"
            inactive-value="0"
            :loading="changingId === scope.row.id"
            :disabled="
              changingId !== undefined ||
              !hasAccessByCodes(['monitor:job:changeStatus'])
            "
            @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        align="center"
        width="200"
        class-name="small-padding fixed-width"
      >
        <template #default="scope">
          <el-tooltip
            content="修改"
            placement="top"
          >
            <el-button
              link
              type="primary"
              :icon="Edit"
              @click="handleUpdate(scope.row)"
              v-access="['monitor:job:edit']"
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
              v-access="['monitor:job:remove']"
            ></el-button>
          </el-tooltip>
          <el-tooltip
            content="执行一次"
            placement="top"
          >
            <el-button
              link
              type="primary"
              :icon="CaretRight"
              :loading="runningId === scope.row.id"
              :disabled="runningId !== undefined"
              @click="handleRun(scope.row)"
              v-access="['monitor:job:changeStatus']"
            ></el-button>
          </el-tooltip>
          <el-tooltip
            content="调度日志"
            placement="top"
          >
            <el-button
              link
              type="primary"
              :icon="Operation"
              @click="handleJobLog(scope.row)"
              v-access="['monitor:job:query']"
            ></el-button>
          </el-tooltip>
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

    <!-- 添加或修改定时任务对话框 -->
    <el-dialog
      :close-on-click-modal="false"
      :close-on-press-escape="!submitting"
      :show-close="!submitting"
      :title="title"
      v-model="open"
      width="820px"
      append-to-body
    >
      <el-form
        :disabled="submitting"
        @submit.prevent="submitForm"
        ref="jobRef"
        :model="form"
        :rules="rules"
        label-width="120px"
      >
        <el-row>
          <el-col :span="12">
            <el-form-item
              label="任务名称"
              prop="jobName"
            >
              <el-input
                :maxlength="64"
                v-model="form.jobName"
                placeholder="请输入任务名称"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item
              label="任务分组"
              prop="jobGroup"
            >
              <el-select
                v-model="form.jobGroup"
                placeholder="请选择"
              >
                <el-option
                  v-for="dict in sys_job_group"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item prop="invokeTarget">
              <template #label>
                <span>
                  调用方法
                  <el-tooltip placement="top">
                    <template #content>
                      <div>
                        Bean调用示例：adminTask.taskParams('admin')
                        <br />Class类调用示例：dev.geo.admin.quartz.task.AdminTask.taskParams('admin')
                        <br />参数说明：支持字符串，布尔类型，长整型，浮点型，整型
                      </div>
                    </template>
                    <el-icon><question-filled /></el-icon>
                  </el-tooltip>
                </span>
              </template>
              <el-input
                :maxlength="500"
                v-model="form.invokeTarget"
                placeholder="请输入调用目标字符串"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item
              label="cron表达式"
              prop="cronExpression"
            >
              <el-input
                :maxlength="255"
                v-model="form.cronExpression"
                placeholder="请输入cron执行表达式"
              >
                <template #append>
                  <el-button
                    type="primary"
                    @click="handleShowCron"
                  >
                    生成表达式
                    <el-icon class="ml-1"><Clock /></el-icon>
                  </el-button>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col
            :span="24"
            v-if="form.id !== undefined"
          >
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio
                  v-for="dict in statusOptions"
                  :key="dict.value"
                  :value="dict.value"
                  >{{ dict.label }}</el-radio
                >
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item
              label="执行策略"
              prop="misfirePolicy"
            >
              <el-radio-group v-model="form.misfirePolicy">
                <el-radio-button value="0">默认策略</el-radio-button>
                <el-radio-button value="1">立即执行</el-radio-button>
                <el-radio-button value="2">执行一次</el-radio-button>
                <el-radio-button value="3">放弃执行</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item
              label="是否并发"
              prop="concurrent"
            >
              <el-radio-group v-model="form.concurrent">
                <el-radio-button value="1">允许</el-radio-button>
                <el-radio-button value="0">禁止</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button
            type="primary"
            :loading="submitting"
            @click="submitForm"
            >确 定</el-button
          >
          <el-button
            :disabled="submitting"
            @click="cancel"
            >取 消</el-button
          >
        </div>
      </template>
    </el-dialog>

    <el-dialog
      width="900px"
      title="Cron表达式生成器"
      v-model="openCron"
      append-to-body
      destroy-on-close
    >
      <crontab
        ref="crontabRef"
        @hide="openCron = false"
        @fill="crontabFill"
        :expression="expression"
      ></crontab>
    </el-dialog>

    <!-- 任务日志详细 -->
    <job-detail
      v-if="viewJob"
      v-model:visible="openView"
      :row="viewJob"
      type="job"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  ElMessage,
  ElMessageBox,
  type FormInstance,
  type FormRules,
} from 'element-plus'
import {
  CaretRight,
  Clock,
  Delete,
  Download,
  Edit,
  Operation,
  Plus,
  QuestionFilled,
  Refresh,
  Search,
} from '@element-plus/icons-vue'
import JobDetail from './detail.vue'
import Crontab from '@/components/Crontab/index.vue'
import DictTag from '@/components/DictTag/index.vue'
import Pagination from '@/components/Pagination/index.vue'
import RightToolbar from '@/components/RightToolbar/index.vue'
import {
  listJob,
  getJob,
  delJob,
  addJob,
  updateJob,
  runJob,
  changeJobStatus,
  exportJob,
} from '@/api/monitor/job'
import type {
  JobQueryParams,
  JobSaveParams,
  SysJob,
} from '@/types/base/api/monitor/job'
import { useAccess } from '@/plugins/effects/access/use-access'
import { useDict } from '@/utils/dict'
import { downloadFileFromBlob } from '@/utils/download'

defineOptions({ name: 'Job' })

const router = useRouter()
const { hasAccessByCodes } = useAccess()
const { sys_job_group } = useDict('sys_job_group')
const statusOptions = [
  { value: '1', label: '正常' },
  { value: '0', label: '暂停' },
]

const queryRef = ref<FormInstance>()
const jobRef = ref<FormInstance>()
const jobList = ref<SysJob[]>([])
const selectedIds = ref<number[]>([])
const total = ref(0)
const loading = ref(false)
const submitting = ref(false)
const exporting = ref(false)
const changingId = ref<number>()
const runningId = ref<number>()
const showSearch = ref(true)
const open = ref(false)
const title = ref('')
const openView = ref(false)
const viewJob = ref<SysJob>()
const openCron = ref(false)
const expression = ref('')
const single = computed(() => selectedIds.value.length !== 1)
const multiple = computed(() => selectedIds.value.length === 0)

const queryParams = reactive<JobQueryParams>({ pageNum: 1, pageSize: 10 })
const form = ref<JobSaveParams>(createForm())
const rules: FormRules<JobSaveParams> = {
  jobName: [
    {
      required: true,
      whitespace: true,
      message: '任务名称不能为空',
      trigger: 'blur',
    },
  ],
  jobGroup: [
    { required: true, message: '任务分组不能为空', trigger: 'change' },
  ],
  invokeTarget: [
    {
      required: true,
      whitespace: true,
      message: '调用目标字符串不能为空',
      trigger: 'blur',
    },
  ],
  cronExpression: [
    {
      required: true,
      whitespace: true,
      message: 'Cron 表达式不能为空',
      trigger: 'change',
    },
  ],
}

function createForm(): JobSaveParams {
  return {
    jobName: '',
    jobGroup: 'DEFAULT',
    invokeTarget: '',
    cronExpression: '',
    misfirePolicy: '1',
    concurrent: '0',
    status: '0',
    remark: '',
  }
}

async function getList() {
  loading.value = true
  try {
    const result = await listJob({ ...queryParams })
    jobList.value = result.records
    total.value = result.total
    selectedIds.value = []
  } catch {
    jobList.value = []
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
  return handleQuery()
}

function handleSelectionChange(selection: SysJob[]) {
  selectedIds.value = selection.map((item) => item.id)
}

async function handleStatusChange(row: SysJob) {
  const previousStatus = row.status === '1' ? '0' : '1'
  const action = row.status === '1' ? '启用' : '停用'
  changingId.value = row.id
  try {
    await ElMessageBox.confirm(
      `确认${action}“${row.jobName}”任务吗？`,
      '任务状态',
    )
    await changeJobStatus(row.id, row.status)
    ElMessage.success(`${action}成功`)
  } catch {
    row.status = previousStatus
  } finally {
    changingId.value = undefined
  }
}

async function handleRun(row: SysJob) {
  const confirmed = await ElMessageBox.confirm(
    `确认立即执行一次“${row.jobName}”任务吗？`,
    '执行任务',
  ).then(
    () => true,
    () => false,
  )
  if (!confirmed) return
  runningId.value = row.id
  try {
    await runJob(row.id, row.jobGroup)
    ElMessage.success('任务已提交执行，请在调度日志中查看结果')
  } finally {
    runningId.value = undefined
  }
}

async function handleView(row: SysJob) {
  viewJob.value = await getJob(row.id)
  openView.value = true
}

function handleShowCron() {
  expression.value = form.value.cronExpression
  openCron.value = true
}

function crontabFill(value: string) {
  form.value.cronExpression = value
}

function handleJobLog(row?: SysJob) {
  return router.push({ name: 'JobLog', params: { jobId: row?.id ?? 0 } })
}

async function handleAdd() {
  form.value = createForm()
  title.value = '添加任务'
  open.value = true
  await nextTick()
  jobRef.value?.clearValidate()
}

async function handleUpdate(row?: SysJob) {
  const id = row?.id ?? selectedIds.value[0]
  if (id === undefined) return
  const job = await getJob(id)
  form.value = {
    id: job.id,
    jobName: job.jobName,
    jobGroup: job.jobGroup,
    invokeTarget: job.invokeTarget,
    cronExpression: job.cronExpression,
    misfirePolicy: job.misfirePolicy,
    concurrent: job.concurrent,
    status: job.status,
    remark: job.remark,
  }
  title.value = '修改任务'
  open.value = true
  await nextTick()
  jobRef.value?.clearValidate()
}

function cancel() {
  open.value = false
}

async function submitForm() {
  if (submitting.value) return
  const valid = await jobRef.value?.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    const editing = form.value.id !== undefined
    if (editing) await updateJob({ ...form.value })
    else await addJob({ ...form.value })
    ElMessage.success(editing ? '修改成功' : '新增成功，任务默认暂停')
    open.value = false
    await getList()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row?: SysJob) {
  const ids = row ? [row.id] : [...selectedIds.value]
  if (!ids.length) return
  const confirmed = await ElMessageBox.confirm(
    `确认删除任务编号为“${ids.join('、')}”的数据吗？`,
    '删除任务',
  ).then(
    () => true,
    () => false,
  )
  if (!confirmed) return
  await delJob(ids)
  ElMessage.success('删除成功')
  if (ids.length === jobList.value.length && queryParams.pageNum > 1)
    queryParams.pageNum--
  await getList()
}

async function handleExport() {
  exporting.value = true
  try {
    const blob = await exportJob({ ...queryParams })
    downloadFileFromBlob({ source: blob, fileName: `job_${Date.now()}.xlsx` })
  } finally {
    exporting.value = false
  }
}

onMounted(getList)
</script>

<style scoped>
/* 停用轨道与表格悬停、选中背景保持区分。 */
.job-status-switch {
  --el-switch-off-color: var(--el-text-color-placeholder);
}

.job-status-switch:not(.is-checked) {
  --el-switch-border-color: var(--el-text-color-secondary);
}
</style>
