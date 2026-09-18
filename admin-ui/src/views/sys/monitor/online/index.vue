<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryRef"
      :inline="true"
      :disabled="loading || !!forcingTokenId"
      @submit.prevent="handleQuery"
    >
      <el-form-item label="登录地址" prop="ipAddress">
        <el-input
          v-model="queryParams.ipAddress"
          placeholder="请输入登录地址"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户名称" prop="userName">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入用户名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
        <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <p v-if="loadFailed" class="mb-3 text-sm text-destructive">在线用户加载失败，请重新查询。</p>
    <el-table
      v-loading="loading"
      :data="onlineList.slice((pageNum - 1) * pageSize, pageNum * pageSize)"
      row-key="tokenId"
      style="width: 100%"
    >
      <el-table-column label="序号" width="50" type="index" align="center">
        <template #default="scope">
          <span>{{ (pageNum - 1) * pageSize + scope.$index + 1 }}</span>
        </template>
      </el-table-column>
      <el-table-column
        label="会话编号"
        align="center"
        prop="tokenId"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        label="登录名称"
        align="center"
        prop="userName"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        label="所属部门"
        align="center"
        prop="deptName"
        :show-overflow-tooltip="true"
      />
      <el-table-column label="主机" align="center" prop="ip" :show-overflow-tooltip="true" />
      <el-table-column
        label="登录地点"
        align="center"
        prop="loginLocation"
        :show-overflow-tooltip="true"
      />
      <el-table-column label="操作系统" align="center" prop="os" :show-overflow-tooltip="true" />
      <el-table-column label="浏览器" align="center" prop="browser" :show-overflow-tooltip="true" />
      <el-table-column label="登录时间" align="center" prop="loginTime" width="180">
        <template #default="scope">
          <span>{{ formatDateTime(scope.row.loginTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button
            link
            type="primary"
            :icon="Delete"
            :disabled="loading || !!forcingTokenId"
            :loading="forcingTokenId === scope.row.tokenId"
            @click="handleForceLogout(scope.row as SysUserOnline)"
            v-access="[ONLINE_SESSION_PERMISSION.forceLogout]"
            >强退</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <Pagination v-show="total > 0" :total="total" v-model:page="pageNum" v-model:limit="pageSize" />
  </div>
</template>

<script setup lang="ts" name="Online">
import { forceLogout, list as initData } from '@/api/monitor/online';
import type { OnlineQueryParams, SysUserOnline } from '@/types/base/api/monitor/online';
import { formatDateTime } from '@/utils/date';
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus';
import { Delete, Refresh, Search } from '@element-plus/icons-vue';
import { computed, onActivated, onBeforeUnmount, onDeactivated, onMounted, ref } from 'vue';
import Pagination from '@/components/Pagination/index.vue';
import { ONLINE_SESSION_PERMISSION } from '@/constants/permissions';
import { subscribeOnlineChanges } from '@/utils/online-stream';
const queryRef = ref<FormInstance>();
const onlineList = ref<SysUserOnline[]>([]);
const loading = ref(false);
const loadFailed = ref(false);
const forcingTokenId = ref<string>();
const total = computed(() => onlineList.value.length);
const pageNum = ref<number>(1);
const pageSize = ref<number>(10);

const queryParams = ref<OnlineQueryParams>({
  ipAddress: undefined,
  userName: undefined,
});

onMounted(activatePage);
onActivated(activatePage);
onDeactivated(() => {
  pageActive = false;
});

onBeforeUnmount(() => {
  pageActive = false;
  unsubscribeOnline();
});
let pageActive = false;
let refreshQueued = false;
function requestOnlineRefresh() {
  if (!pageActive) return;

  if (loading.value) {
    refreshQueued = true;
    return;
  }

  void getList();
}
const unsubscribeOnline = subscribeOnlineChanges(requestOnlineRefresh);
function activatePage() {
  if (pageActive) return;

  pageActive = true;
  requestOnlineRefresh();
}
/** 在线会话接口返回数组，分页由页面处理。 */
async function getList() {
  if (loading.value) return;
  loading.value = true;
  loadFailed.value = false;
  try {
    onlineList.value = await initData(queryParams.value);
    pageNum.value = Math.min(pageNum.value, Math.max(1, Math.ceil(total.value / pageSize.value)));
  } catch {
    loadFailed.value = true;
  } finally {
    loading.value = false;
    if (refreshQueued) {
      refreshQueued = false;

      if (pageActive) {
        void getList();
      }
    }
  }
}

/** 搜索按钮操作 */
function handleQuery() {
  pageNum.value = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  queryRef.value?.resetFields();
  handleQuery();
}

/** 强退按钮操作 */
async function handleForceLogout(row: SysUserOnline) {
  if (loading.value || forcingTokenId.value) return;
  forcingTokenId.value = row.tokenId;
  try {
    await ElMessageBox.confirm('是否确认强退名称为"' + row.userName + '"的用户?');
  } catch {
    forcingTokenId.value = undefined;
    return;
  }
  try {
    await forceLogout(row.tokenId);
    ElMessage.success('强退成功');
    await getList();
  } catch {
    // 接口异常由请求层提示。
  } finally {
    forcingTokenId.value = undefined;
  }
}
</script>
