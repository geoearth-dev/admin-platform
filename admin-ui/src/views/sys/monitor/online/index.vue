<template>
  <div class="app-container">
    <el-form
      ref="queryRef"
      :model="queryParams"
      :inline="true"
      @submit.prevent="handleQuery"
    >
      <el-form-item
        label="用户账号"
        prop="userName"
      >
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入用户账号"
          clearable
        />
      </el-form-item>
      <el-form-item
        label="登录 IP"
        prop="ipAddress"
      >
        <el-input
          v-model="queryParams.ipAddress"
          placeholder="请输入登录 IP"
          clearable
        />
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          :icon="Search"
          native-type="submit"
          :loading="loading"
        >
          搜索
        </el-button>
        <el-button
          :icon="Refresh"
          @click="resetQuery"
          >重置</el-button
        >
      </el-form-item>
    </el-form>

    <div class="mb-3 flex flex-wrap items-center justify-between gap-3">
      <div
        class="flex flex-wrap items-center gap-2 text-sm text-muted-foreground"
      >
        <span
          >当前结果：{{ userCount }} 个账号 /
          {{ onlineList.length }} 条在线会话</span
        >
        <span v-if="updatedAt">· 更新于 {{ formatDateTime(updatedAt) }}</span>
      </div>
      <el-button
        :icon="Refresh"
        :loading="fetching"
        @click="getList()"
        >刷新</el-button
      >
    </div>
    <p
      v-if="loadFailed"
      role="alert"
      class="mb-3 text-sm text-destructive"
    >
      {{
        updatedAt
          ? '更新失败，当前显示上次查询结果，请刷新重试。'
          : '在线用户加载失败，请刷新重试。'
      }}
    </p>

    <el-table
      v-loading="loading"
      :data="pageRows"
      row-key="sessionId"
      empty-text="暂无匹配的在线会话"
    >
      <el-table-column
        label="账号 / 昵称"
        min-width="200"
        show-overflow-tooltip
      >
        <template #default="{ row }">
          <div
            class="flex items-center gap-2"
            :title="'会话编号：' + row.sessionId"
          >
            <span>{{ row.userName }}</span>
            <el-tag
              v-if="row.currentSession"
              size="small"
              type="success"
              >当前会话</el-tag
            >
          </div>
          <div class="text-xs text-muted-foreground">
            {{ row.nickName || '—' }}
          </div>
        </template>
      </el-table-column>
      <el-table-column
        label="所属部门"
        min-width="120"
        show-overflow-tooltip
      >
        <template #default="{ row }">{{ row.deptName || '—' }}</template>
      </el-table-column>
      <el-table-column
        label="登录 IP / 地点"
        min-width="180"
        show-overflow-tooltip
      >
        <template #default="{ row }">
          <div>{{ row.ip || '—' }}</div>
          <div class="text-xs text-muted-foreground">
            {{ row.loginLocation || '未知地点' }}
          </div>
        </template>
      </el-table-column>
      <el-table-column
        label="浏览器 / 系统"
        min-width="180"
        show-overflow-tooltip
      >
        <template #default="{ row }">
          <div>{{ row.browser || '未知浏览器' }}</div>
          <div class="text-xs text-muted-foreground">
            {{ row.os || '未知系统' }}
          </div>
        </template>
      </el-table-column>
      <el-table-column
        label="登录时间"
        width="180"
      >
        <template #default="{ row }">{{
          formatDateTime(row.loginTime)
        }}</template>
      </el-table-column>
      <el-table-column
        label="操作"
        width="120"
        align="center"
        fixed="right"
      >
        <template #default="{ row }">
          <el-button
            v-access:code="[ONLINE_SESSION_PERMISSION.forceLogout]"
            link
            type="danger"
            :icon="SwitchButton"
            :disabled="!row.forceLogoutAllowed || !!forcingSessionId"
            :loading="forcingSessionId === row.sessionId"
            :title="
              row.currentSession
                ? '当前会话请使用退出登录'
                : row.forceLogoutAllowed
                  ? ''
                  : '不允许强制下线超级管理员'
            "
            @click="handleForceLogout(row)"
            >强制下线</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <Pagination
      v-show="onlineList.length > 0"
      v-model:page="pageNum"
      v-model:limit="pageSize"
      :total="onlineList.length"
    />
  </div>
</template>

<script setup lang="ts" name="Online">
import {
  computed,
  h,
  onActivated,
  onBeforeUnmount,
  onDeactivated,
  onMounted,
  ref,
} from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import { Refresh, Search, SwitchButton } from '@element-plus/icons-vue'

import { forceLogout, list } from '@/api/monitor/online'
import Pagination from '@/components/Pagination/index.vue'
import { ONLINE_SESSION_PERMISSION } from '@/constants/permissions'
import type {
  OnlineQueryParams,
  OnlineSession,
} from '@/types/base/api/monitor/online'
import { formatDateTime } from '@/utils/date'
import { subscribeOnlineChanges } from '@/utils/online-stream'

const queryRef = ref<FormInstance>()
const queryParams = ref<OnlineQueryParams>({ userName: '', ipAddress: '' })
// 自动刷新沿用已提交的条件，不把正在输入的内容当成查询条件。
let appliedQuery: OnlineQueryParams = {}
const onlineList = ref<OnlineSession[]>([])
const pageNum = ref(1)
const pageSize = ref(10)
const loading = ref(false)
const fetching = ref(false)
const loadFailed = ref(false)
const updatedAt = ref<number>()
const forcingSessionId = ref<string>()
const userCount = computed(
  () => new Set(onlineList.value.map((row) => row.userId)).size,
)
const pageRows = computed(() =>
  onlineList.value.slice(
    (pageNum.value - 1) * pageSize.value,
    pageNum.value * pageSize.value,
  ),
)

let pageActive = false
let refreshQueued = false
const unsubscribeOnline = subscribeOnlineChanges(() => {
  if (pageActive) void getList(true)
})

onMounted(activatePage)
onActivated(activatePage)
onDeactivated(() => {
  pageActive = false
})
onBeforeUnmount(() => {
  pageActive = false
  unsubscribeOnline()
})

function activatePage() {
  if (pageActive) return
  pageActive = true
  void getList()
}

/** 名单变化静默刷新，保留分页和旧数据，不遮挡用户操作。 */
async function getList(silent = false) {
  if (fetching.value) {
    refreshQueued = true
    return
  }
  fetching.value = true
  loading.value = !silent
  try {
    onlineList.value = await list(appliedQuery, silent)
    pageNum.value = Math.min(
      pageNum.value,
      Math.max(1, Math.ceil(onlineList.value.length / pageSize.value)),
    )
    updatedAt.value = Date.now()
    loadFailed.value = false
  } catch {
    loadFailed.value = true
  } finally {
    fetching.value = false
    loading.value = false
    if (refreshQueued) {
      refreshQueued = false
      if (pageActive) void getList(true)
    }
  }
}

function handleQuery() {
  appliedQuery = {
    userName: queryParams.value.userName?.trim(),
    ipAddress: queryParams.value.ipAddress?.trim(),
  }
  pageNum.value = 1
  void getList()
}

function resetQuery() {
  queryRef.value?.resetFields()
  handleQuery()
}

async function handleForceLogout(row: OnlineSession) {
  forcingSessionId.value = row.sessionId
  try {
    await ElMessageBox.confirm(
      h('div', { class: 'space-y-2' }, [
        h('p', '账号：' + row.userName),
        h(
          'p',
          '登录地址：' +
            (row.ip || '未知 IP') +
            ' · ' +
            (row.loginLocation || '未知地点'),
        ),
        h(
          'p',
          '设备：' +
            (row.browser || '未知浏览器') +
            ' / ' +
            (row.os || '未知系统'),
        ),
        h(
          'p',
          { class: 'text-muted-foreground' },
          '仅退出此登录会话，其他登录会话不受影响。',
        ),
      ]),
      '确认强制下线',
      {
        type: 'warning',
        confirmButtonText: '强制下线',
        cancelButtonText: '取消',
      },
    )
  } catch {
    forcingSessionId.value = undefined
    return
  }
  try {
    await forceLogout(row.sessionId)
    ElMessage.success('该会话已强制下线')
    await getList(true)
  } catch {
    // 接口错误由统一请求层提示。
  } finally {
    forcingSessionId.value = undefined
  }
}
</script>
