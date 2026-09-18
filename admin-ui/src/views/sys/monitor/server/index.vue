<template>
  <div
    class="app-container server-monitor"
    v-loading="loading"
    element-loading-text="正在加载服务监控数据，请稍候！"
  >
    <div class="server-toolbar">
      <el-button :icon="Refresh" :loading="loading" @click="getList">刷新</el-button>
    </div>
    <el-row v-if="server" :gutter="16">
      <el-col :xs="24" :lg="12" class="server-card">
        <el-card shadow="never">
          <template #header>
            <div class="server-card-title">
              <Cpu class="server-card-icon" />
              <span>CPU</span>
            </div>
          </template>
          <div class="server-table">
            <table>
              <thead>
                <tr>
                  <th>
                    <div class="cell">属性</div>
                  </th>
                  <th>
                    <div class="cell">值</div>
                  </th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>
                    <div class="cell">逻辑处理器数</div>
                  </td>
                  <td>
                    <div class="cell">{{ server.cpu.cpuNum }}</div>
                  </td>
                </tr>
                <tr>
                  <td>
                    <div class="cell">用户使用率</div>
                  </td>
                  <td>
                    <div class="cell">{{ server.cpu.used }}%</div>
                  </td>
                </tr>
                <tr>
                  <td>
                    <div class="cell">系统使用率</div>
                  </td>
                  <td>
                    <div class="cell">{{ server.cpu.sys }}%</div>
                  </td>
                </tr>
                <tr>
                  <td>
                    <div class="cell">当前空闲率</div>
                  </td>
                  <td>
                    <div class="cell">{{ server.cpu.free }}%</div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12" class="server-card">
        <el-card shadow="never">
          <template #header>
            <div class="server-card-title">
              <Tickets class="server-card-icon" />
              <span>内存</span>
            </div>
          </template>
          <div class="server-table">
            <table>
              <thead>
                <tr>
                  <th>
                    <div class="cell">属性</div>
                  </th>
                  <th>
                    <div class="cell">内存</div>
                  </th>
                  <th>
                    <div class="cell">JVM 堆内存</div>
                  </th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>
                    <div class="cell">总内存</div>
                  </td>
                  <td>
                    <div class="cell">{{ server.mem.total }} GB</div>
                  </td>
                  <td>
                    <div class="cell">{{ server.jvm.total }} MB</div>
                  </td>
                </tr>
                <tr>
                  <td>
                    <div class="cell">已用内存</div>
                  </td>
                  <td>
                    <div class="cell">{{ server.mem.used }} GB</div>
                  </td>
                  <td>
                    <div class="cell">{{ server.jvm.used }} MB</div>
                  </td>
                </tr>
                <tr>
                  <td>
                    <div class="cell">剩余内存</div>
                  </td>
                  <td>
                    <div class="cell">{{ server.mem.free }} GB</div>
                  </td>
                  <td>
                    <div class="cell">{{ server.jvm.free }} MB</div>
                  </td>
                </tr>
                <tr>
                  <td>
                    <div class="cell">使用率</div>
                  </td>
                  <td>
                    <div class="cell" :class="{ 'text-danger': server.mem.usage > 80 }">
                      {{ server.mem.usage }}%
                    </div>
                  </td>
                  <td>
                    <div class="cell" :class="{ 'text-danger': server.jvm.usage > 80 }">
                      {{ server.jvm.usage }}%
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </el-card>
      </el-col>

      <el-col :span="24" class="server-card">
        <el-card shadow="never">
          <template #header>
            <div class="server-card-title">
              <Monitor class="server-card-icon" />
              <span>服务器信息</span>
            </div>
          </template>
          <div class="server-table">
            <table class="server-details-table">
              <tbody>
                <tr>
                  <td>
                    <div class="cell">服务器名称</div>
                  </td>
                  <td>
                    <div class="cell">{{ server.sys.computerName }}</div>
                  </td>
                  <td>
                    <div class="cell">操作系统</div>
                  </td>
                  <td>
                    <div class="cell">{{ server.sys.osName }}</div>
                  </td>
                </tr>
                <tr>
                  <td>
                    <div class="cell">服务器IP</div>
                  </td>
                  <td>
                    <div class="cell">{{ server.sys.computerIp }}</div>
                  </td>
                  <td>
                    <div class="cell">系统架构</div>
                  </td>
                  <td>
                    <div class="cell">{{ server.sys.osArch }}</div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </el-card>
      </el-col>

      <el-col :span="24" class="server-card">
        <el-card shadow="never">
          <template #header>
            <div class="server-card-title">
              <CoffeeCup class="server-card-icon" />
              <span>Java虚拟机信息</span>
            </div>
          </template>
          <div class="server-table">
            <table class="server-details-table">
              <tbody>
                <tr>
                  <td>
                    <div class="cell">Java名称</div>
                  </td>
                  <td>
                    <div class="cell">{{ server.jvm.name }}</div>
                  </td>
                  <td>
                    <div class="cell">Java版本</div>
                  </td>
                  <td>
                    <div class="cell">{{ server.jvm.version }}</div>
                  </td>
                </tr>
                <tr>
                  <td>
                    <div class="cell">启动时间</div>
                  </td>
                  <td>
                    <div class="cell">{{ server.jvm.startTime }}</div>
                  </td>
                  <td>
                    <div class="cell">运行时长</div>
                  </td>
                  <td>
                    <div class="cell">{{ server.jvm.runTime }}</div>
                  </td>
                </tr>
                <tr>
                  <td>
                    <div class="cell">安装路径</div>
                  </td>
                  <td colspan="3">
                    <div class="cell">{{ server.jvm.home }}</div>
                  </td>
                </tr>
                <tr>
                  <td>
                    <div class="cell">项目路径</div>
                  </td>
                  <td colspan="3">
                    <div class="cell">{{ server.sys.userDir }}</div>
                  </td>
                </tr>
                <tr>
                  <td>
                    <div class="cell">运行参数</div>
                  </td>
                  <td colspan="3">
                    <div class="cell">{{ server.jvm.inputArgs }}</div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </el-card>
      </el-col>

      <el-col :span="24" class="server-card">
        <el-card shadow="never">
          <template #header>
            <div class="server-card-title">
              <MessageBox class="server-card-icon" />
              <span>磁盘状态</span>
            </div>
          </template>
          <div class="server-table server-disk-table">
            <table>
              <thead>
                <tr>
                  <th>
                    <div class="cell">盘符路径</div>
                  </th>
                  <th>
                    <div class="cell">文件系统</div>
                  </th>
                  <th>
                    <div class="cell">存储名称</div>
                  </th>
                  <th>
                    <div class="cell">总大小</div>
                  </th>
                  <th>
                    <div class="cell">可用大小</div>
                  </th>
                  <th>
                    <div class="cell">已用大小</div>
                  </th>
                  <th>
                    <div class="cell">已用百分比</div>
                  </th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="sysFile in server.sysFiles" :key="sysFile.dirName">
                  <td>
                    <div class="cell">{{ sysFile.dirName }}</div>
                  </td>
                  <td>
                    <div class="cell">{{ sysFile.sysTypeName }}</div>
                  </td>
                  <td>
                    <div class="cell">{{ sysFile.typeName }}</div>
                  </td>
                  <td>
                    <div class="cell">{{ sysFile.total }}</div>
                  </td>
                  <td>
                    <div class="cell">{{ sysFile.free }}</div>
                  </td>
                  <td>
                    <div class="cell">{{ sysFile.used }}</div>
                  </td>
                  <td>
                    <div class="cell" :class="{ 'text-danger': sysFile.usage > 80 }">
                      {{ sysFile.usage }}%
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-empty v-else-if="!loading" description="服务监控数据加载失败">
      <el-button type="primary" @click="getList">重新加载</el-button>
    </el-empty>
  </div>
</template>

<script setup lang="ts">
import type { ServerMetrics } from '@/types/base/api/monitor/server';
import { getServer } from '@/api/monitor/server';
import { CoffeeCup, Cpu, MessageBox, Monitor, Refresh, Tickets } from '@element-plus/icons-vue';
import { ElEmpty, vLoading } from 'element-plus';
import { onMounted, ref } from 'vue';

const server = ref<ServerMetrics | null>(null);
const loading = ref(true);

async function getList() {
  loading.value = true;
  try {
    server.value = await getServer();
  } catch {
    server.value = null;
  } finally {
    loading.value = false;
  }
}

onMounted(getList);
</script>

<style scoped>
.server-monitor {
  min-height: var(--vben-content-height, calc(100dvh - 120px));
}

.server-toolbar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 16px;
}

.server-card {
  margin-bottom: 16px;
}

.server-card :deep(.el-card) {
  height: 100%;
  border-radius: 8px;
}

.server-card :deep(.el-card__header) {
  padding: 16px 20px;
}

.server-card :deep(.el-card__body) {
  padding: 8px 20px 16px;
}

.server-card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--el-text-color-primary);
  font-size: 16px;
  font-weight: 500;
  line-height: 24px;
}

.server-card-icon {
  width: 18px;
  height: 18px;
  flex-shrink: 0;
  color: var(--el-color-primary);
}

.server-table {
  overflow-x: auto;
  color: var(--el-text-color-regular);
  font-size: 14px;
}

.server-table table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
}

.server-table th,
.server-table td {
  padding: 14px 12px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  text-align: left;
  vertical-align: middle;
  line-height: 22px;
}

.server-table th {
  color: var(--el-text-color-secondary);
  font-weight: 500;
}

.server-table tbody tr:hover {
  background: var(--el-fill-color-light);
}

.server-table td:first-child,
.server-details-table td:nth-child(3) {
  color: var(--el-text-color-secondary);
}

.server-details-table td:nth-child(odd) {
  width: 18%;
}

.server-table .cell {
  overflow-wrap: anywhere;
  font-variant-numeric: tabular-nums;
}

.server-disk-table table {
  min-width: 720px;
}

.text-danger {
  color: var(--el-color-danger);
}

.server-monitor :deep(.el-loading-mask) {
  background-color: var(--el-mask-color);
  backdrop-filter: blur(3px);
}

.server-monitor :deep(.el-loading-spinner) {
  top: min(50%, calc(var(--vben-content-height, 100dvh) / 2));
}

.server-monitor :deep(.el-loading-text) {
  margin-top: 12px;
  color: var(--el-text-color-regular);
}

@media (max-width: 767px) {
  .server-card :deep(.el-card__header) {
    padding: 12px 16px;
  }

  .server-card :deep(.el-card__body) {
    padding: 4px 8px 12px;
  }

  .server-table th,
  .server-table td {
    padding: 12px 8px;
  }
}
</style>
