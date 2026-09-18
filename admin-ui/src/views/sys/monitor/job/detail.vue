<template>
  <el-dialog
    :title="type === 'log' ? '调度日志详细' : '任务详细'"
    v-model="dialogVisible"
    width="min(960px, calc(100vw - 32px))"
    top="5vh"
    append-to-body
  >
    <div class="detail-wrap">
      <template v-if="logForm">
        <!-- 基本信息 -->
        <div class="detail-card">
          <div class="detail-card-title">
            <el-icon><InfoFilled /></el-icon> 基本信息
          </div>
          <el-row class="detail-row">
            <el-col :span="12">
              <div class="detail-item">
                <span class="detail-label">日志编号</span
                ><span class="detail-value">{{ logForm.id }}</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="detail-item">
                <span class="detail-label">执行状态</span>
                <el-tag
                  v-if="logForm.status === '1'"
                  type="success"
                  size="small"
                  >成功</el-tag
                >
                <el-tag
                  v-else
                  type="danger"
                  size="small"
                  >失败</el-tag
                >
              </div>
            </el-col>
          </el-row>
          <el-row class="detail-row">
            <el-col :span="12">
              <div class="detail-item">
                <span class="detail-label">开始时间</span
                ><span class="detail-value">{{
                  formatDateTime(logForm.startTime ?? undefined) || '-'
                }}</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="detail-item">
                <span class="detail-label">结束时间</span
                ><span class="detail-value">{{
                  formatDateTime(logForm.endTime ?? undefined) || '-'
                }}</span>
              </div>
            </el-col>
          </el-row>
          <el-row class="detail-row">
            <el-col :span="12">
              <div class="detail-item">
                <span class="detail-label">记录时间</span
                ><span class="detail-value">{{
                  formatDateTime(logForm.createTime) || '-'
                }}</span>
              </div>
            </el-col>
            <el-col
              :span="12"
              v-if="costTime !== undefined"
            >
              <div class="detail-item">
                <span class="detail-label">执行耗时</span
                ><span class="detail-value">{{ costTime }} 毫秒</span>
              </div>
            </el-col>
          </el-row>
        </div>
        <!-- 任务信息 -->
        <div class="detail-card">
          <div class="detail-card-title">
            <el-icon><Clock /></el-icon> 任务信息
          </div>
          <el-row class="detail-row">
            <el-col :span="12">
              <div class="detail-item">
                <span class="detail-label">任务名称</span
                ><span class="detail-value">{{ logForm.jobName }}</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="detail-item">
                <span class="detail-label">任务分组</span>
                <dict-tag
                  :options="sys_job_group"
                  :value="logForm.jobGroup"
                />
              </div>
            </el-col>
          </el-row>
          <el-row class="detail-row">
            <el-col :span="24">
              <div class="detail-item">
                <span class="detail-label">日志信息</span
                ><span class="detail-value">{{ logForm.jobMessage }}</span>
              </div>
            </el-col>
          </el-row>
        </div>
        <!-- 调用目标 -->
        <div class="detail-card">
          <div class="detail-card-title">
            <el-icon><Operation /></el-icon> 调用目标
          </div>
          <div class="code-body">
            <div class="code-wrap">
              <pre class="code-pre">{{ logForm.invokeTarget || '（无）' }}</pre>
            </div>
          </div>
        </div>
        <!-- 异常信息 -->
        <div
          class="detail-card"
          v-if="logForm.status === '0'"
        >
          <div class="detail-card-title error-title">
            <el-icon><Warning /></el-icon> 异常信息
          </div>
          <div class="error-body">
            <div class="error-msg">{{ logForm.exceptionInfo }}</div>
          </div>
        </div>
      </template>

      <template v-else-if="jobForm">
        <!-- 任务配置 -->
        <div class="detail-card">
          <div class="detail-card-title">
            <el-icon><Setting /></el-icon> 任务配置
          </div>
          <el-row class="detail-row">
            <el-col :span="12">
              <div class="detail-item">
                <span class="detail-label">任务编号</span
                ><span class="detail-value">{{ jobForm.id }}</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="detail-item">
                <span class="detail-label">任务名称</span
                ><span class="detail-value">{{ jobForm.jobName }}</span>
              </div>
            </el-col>
          </el-row>
          <el-row class="detail-row">
            <el-col :span="12">
              <div class="detail-item">
                <span class="detail-label">任务分组</span>
                <dict-tag
                  :options="sys_job_group"
                  :value="jobForm.jobGroup"
                />
              </div>
            </el-col>
            <el-col :span="12">
              <div class="detail-item">
                <span class="detail-label">执行状态</span>
                <el-tag
                  v-if="jobForm.status === '1'"
                  type="success"
                  size="small"
                  >正常</el-tag
                >
                <el-tag
                  v-else
                  type="info"
                  size="small"
                  >暂停</el-tag
                >
              </div>
            </el-col>
          </el-row>
        </div>
        <!-- 调度信息 -->
        <div class="detail-card">
          <div class="detail-card-title">
            <el-icon><Calendar /></el-icon> 调度信息
          </div>
          <el-row class="detail-row">
            <el-col :span="12">
              <div class="detail-item">
                <span class="detail-label">cron 表达式</span
                ><span class="detail-value mono">{{
                  jobForm.cronExpression
                }}</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="detail-item">
                <span class="detail-label">下次执行时间</span
                ><span class="detail-value">{{
                  formatDateTime(jobForm.nextValidTime ?? undefined) || '-'
                }}</span>
              </div>
            </el-col>
          </el-row>
          <el-row class="detail-row">
            <el-col :span="12">
              <div class="detail-item">
                <span class="detail-label">执行策略</span>
                <el-tag
                  v-if="jobForm.misfirePolicy === '0'"
                  type="info"
                  size="small"
                  >默认策略</el-tag
                >
                <el-tag
                  v-else-if="jobForm.misfirePolicy === '1'"
                  type="warning"
                  size="small"
                  >立即执行</el-tag
                >
                <el-tag
                  v-else-if="jobForm.misfirePolicy === '2'"
                  type="primary"
                  size="small"
                  >执行一次</el-tag
                >
                <el-tag
                  v-else-if="jobForm.misfirePolicy === '3'"
                  type="danger"
                  size="small"
                  >放弃执行</el-tag
                >
              </div>
            </el-col>
            <el-col :span="12">
              <div class="detail-item">
                <span class="detail-label">并发执行</span>
                <el-tag
                  v-if="jobForm.concurrent === '1'"
                  type="success"
                  size="small"
                  >允许</el-tag
                >
                <el-tag
                  v-else
                  type="danger"
                  size="small"
                  >禁止</el-tag
                >
              </div>
            </el-col>
          </el-row>
        </div>
        <!-- 执行方法 -->
        <div class="detail-card">
          <div class="detail-card-title">
            <el-icon><Operation /></el-icon> 执行方法
          </div>
          <div class="code-body">
            <div class="code-wrap">
              <pre class="code-pre">{{ jobForm.invokeTarget || '（无）' }}</pre>
            </div>
          </div>
        </div>
        <!-- 元信息 -->
        <div class="detail-card">
          <div class="detail-card-title">
            <el-icon><Document /></el-icon> 元信息
          </div>
          <el-row class="detail-row">
            <el-col :span="12">
              <div class="detail-item">
                <span class="detail-label">创建人</span
                ><span class="detail-value">{{ jobForm.createBy || '-' }}</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="detail-item">
                <span class="detail-label">创建时间</span
                ><span class="detail-value">{{
                  formatDateTime(jobForm.createTime) || '-'
                }}</span>
              </div>
            </el-col>
          </el-row>
          <el-row class="detail-row">
            <el-col :span="12">
              <div class="detail-item">
                <span class="detail-label">更新人</span
                ><span class="detail-value">{{ jobForm.updateBy || '-' }}</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="detail-item">
                <span class="detail-label">更新时间</span
                ><span class="detail-value">{{
                  formatDateTime(jobForm.updateTime) || '-'
                }}</span>
              </div>
            </el-col>
          </el-row>
          <el-row
            class="detail-row"
            v-if="jobForm.remark"
          >
            <el-col :span="24">
              <div class="detail-item">
                <span class="detail-label">备注</span
                ><span class="detail-value">{{ jobForm.remark }}</span>
              </div>
            </el-col>
          </el-row>
        </div>
      </template>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">关 闭</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import dayjs from 'dayjs'
import {
  Calendar,
  Clock,
  Document,
  InfoFilled,
  Operation,
  Setting,
  Warning,
} from '@element-plus/icons-vue'
import DictTag from '@/components/DictTag/index.vue'
import type { SysJob } from '@/types/base/api/monitor/job'
import type { SysJobLog } from '@/types/base/api/monitor/jobLog'
import { useDict } from '@/utils/dict'
import { formatDateTime } from '@/utils/date'

defineOptions({ name: 'JobDetail' })

const props = defineProps<
  { type: 'job'; row: SysJob } | { type: 'log'; row: SysJobLog }
>()
const dialogVisible = defineModel<boolean>('visible', { default: false })
const { sys_job_group } = useDict('sys_job_group')
const jobForm = computed(() => (props.type === 'job' ? props.row : undefined))
const logForm = computed(() => (props.type === 'log' ? props.row : undefined))
const costTime = computed(() => {
  const log = logForm.value
  if (!log?.startTime || !log.endTime) return undefined
  return dayjs(log.endTime).diff(dayjs(log.startTime))
})
</script>

<style scoped>
.detail-wrap {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 4px 6px;
}

.detail-card {
  overflow: hidden;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 6px;
  background: var(--el-bg-color);
}

.detail-card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  background: var(--el-fill-color-light);
  color: var(--el-text-color-primary);
  font-size: 14px;
  font-weight: 600;
  line-height: 20px;
}

.detail-card-title .el-icon {
  color: var(--el-color-primary);
  font-size: 16px;
}

.detail-row {
  padding: 0 16px;
}

.detail-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  min-height: 46px;
  padding: 12px 0;
  font-size: 14px;
  line-height: 22px;
}

.detail-label {
  flex: 0 0 100px;
  color: var(--el-text-color-secondary);
}

.detail-value {
  flex: 1;
  min-width: 0;
  color: var(--el-text-color-primary);
  overflow-wrap: anywhere;
}

.mono,
.code-pre,
.error-msg {
  font-family: Consolas, Monaco, monospace;
}

.code-body,
.error-body {
  padding: 16px;
}

.code-wrap {
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 4px;
  background: var(--el-fill-color-light);
}

.code-pre,
.error-msg {
  margin: 0;
  padding: 14px 16px;
  font-size: 13px;
  line-height: 22px;
  white-space: pre-wrap;
  overflow-wrap: anywhere;
}

.code-pre {
  color: var(--el-text-color-regular);
}

.error-title,
.error-title .el-icon {
  color: var(--el-color-danger);
}

.error-msg {
  max-height: 280px;
  overflow: auto;
  border: 1px solid var(--el-color-danger-light-8);
  border-radius: 4px;
  background: var(--el-color-danger-light-9);
  color: var(--el-color-danger);
}

@media (max-width: 767px) {
  .detail-wrap {
    padding: 0;
  }

  .detail-row :deep(.el-col-12) {
    max-width: 100%;
    flex-basis: 100%;
  }

  .detail-item {
    gap: 12px;
    padding: 10px 0;
  }
}
</style>
