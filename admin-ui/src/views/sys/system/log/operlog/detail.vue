<script setup lang="ts">
import type { SysOperationLog } from '@/types/base/api/system/log/operlog';
import { computed } from 'vue';
import { ElDialog, ElMessage, ElTag } from 'element-plus';
import { IconifyIcon } from '@/assets/icons';
import DictTag from '@/components/DictTag/index.vue';
import { $t } from '@/plugins/locale';
import { VbenButton } from '@/plugins/vben-ui/shadcn-ui';
import { formatDateTime } from '@/utils/date';
import { useDict } from '@/utils/dict';

const props = defineProps<{ visible: boolean; row: SysOperationLog }>();
const emit = defineEmits<{ 'update:visible': [value: boolean] }>();
const dialogVisible = computed({
  get: () => props.visible,
  set: (value: boolean) => emit('update:visible', value),
});
const { sys_oper_type } = useDict('sys_oper_type');
const payloads = computed(() => [
  {
    key: 'requestParams',
    icon: 'lucide:upload',
    label: $t('system.log.requestParams'),
    value: props.row.requestParams,
  },
  {
    key: 'responseBody',
    icon: 'lucide:download',
    label: $t('system.log.responseBody'),
    value: props.row.responseBody,
  },
]);

const methodClass = computed(() => {
  switch (props.row.httpMethod?.toUpperCase()) {
    case 'GET':
      return 'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400';
    case 'POST':
      return 'bg-blue-500/10 text-blue-600 dark:text-blue-400';
    case 'PUT':
    case 'PATCH':
      return 'bg-amber-500/10 text-amber-600 dark:text-amber-400';
    case 'DELETE':
      return 'bg-destructive/10 text-destructive';
    default:
      return 'bg-muted text-muted-foreground';
  }
});

function formatJson(value?: string) {
  if (!value) return $t('common.noData');
  try {
    return JSON.stringify(JSON.parse(value), null, 2);
  } catch {
    return value;
  }
}

async function copyText(value?: string) {
  if (!value) return;
  try {
    await navigator.clipboard.writeText(formatJson(value));
    ElMessage.success($t('system.log.copied'));
  } catch {
    ElMessage.warning($t('system.log.copyFailed'));
  }
}
</script>

<template>
  <ElDialog
    v-model="dialogVisible"
    :title="$t('system.log.detail')"
    class="mb-[5dvh]! flex max-h-[90dvh] max-w-[calc(100vw-2rem)] flex-col overflow-hidden"
    header-class="shrink-0"
    body-class="min-h-0 overflow-y-auto overscroll-contain"
    top="5dvh"
    append-to-body
  >
    <div class="space-y-4 py-1 text-sm text-foreground">
      <section class="overflow-hidden rounded-lg border border-border bg-background">
        <h3 class="detail-heading border-b border-border bg-muted/40">
          <IconifyIcon icon="lucide:info" class="size-4 shrink-0 text-primary" aria-hidden="true" />
          {{ $t('system.log.basicInfo') }}
        </h3>
        <dl class="detail-fields grid grid-cols-1 gap-x-8 gap-y-3 sm:grid-cols-2">
          <div class="detail-field">
            <dt>{{ $t('system.log.module') }}</dt>
            <dd>{{ row.title || '—' }}</dd>
          </div>
          <div class="detail-field">
            <dt>{{ $t('system.log.businessType') }}</dt>
            <dd>
              <DictTag :options="sys_oper_type" :value="row.businessType" />
            </dd>
          </div>
          <div class="detail-field">
            <dt>{{ $t('system.log.operationTime') }}</dt>
            <dd class="tabular-nums">
              {{ formatDateTime(row.operationTime) || '—' }}
            </dd>
          </div>
          <div class="detail-field">
            <dt>{{ $t('system.log.status') }}</dt>
            <dd>
              <ElTag
                :type="row.status === 1 ? 'success' : 'danger'"
                size="small"
                disable-transitions
              >
                <span class="inline-flex items-center gap-1">
                  <IconifyIcon
                    :icon="row.status === 1 ? 'lucide:check' : 'lucide:x'"
                    class="size-3.5"
                    aria-hidden="true"
                  />
                  {{ $t(row.status === 1 ? 'system.log.normal' : 'system.log.error') }}
                </span>
              </ElTag>
            </dd>
          </div>
        </dl>
      </section>

      <section class="overflow-hidden rounded-lg border border-border bg-background">
        <h3 class="detail-heading border-b border-border bg-muted/40">
          <IconifyIcon
            icon="lucide:user-round"
            class="size-4 shrink-0 text-primary"
            aria-hidden="true"
          />
          {{ $t('system.log.operator') }}
        </h3>
        <dl class="detail-fields grid grid-cols-1 gap-x-8 gap-y-3 sm:grid-cols-2">
          <div class="detail-field">
            <dt>{{ $t('system.log.operator') }}</dt>
            <dd>{{ row.userName || '—' }}</dd>
          </div>
          <div class="detail-field">
            <dt>{{ $t('system.log.department') }}</dt>
            <dd>{{ row.deptName || '—' }}</dd>
          </div>
          <div class="detail-field sm:col-span-2">
            <dt>{{ $t('system.log.ipAddress') }}</dt>
            <dd class="flex flex-wrap gap-x-2">
              <span class="tabular-nums">{{ row.ipAddress || '—' }}</span>
              <span class="text-muted-foreground">{{ row.operationLocation }}</span>
            </dd>
          </div>
        </dl>
      </section>

      <section class="overflow-hidden rounded-lg border border-border bg-background">
        <h3 class="detail-heading border-b border-border bg-muted/40">
          <IconifyIcon
            icon="lucide:arrow-down-up"
            class="size-4 shrink-0 text-primary"
            aria-hidden="true"
          />
          {{ $t('system.log.requestInfo') }}
        </h3>
        <dl class="detail-fields space-y-3">
          <div class="detail-field">
            <dt>{{ $t('system.log.requestUri') }}</dt>
            <dd class="flex flex-wrap items-start gap-x-3 gap-y-1">
              <span
                :class="methodClass"
                class="mt-0.5 shrink-0 rounded px-2 font-mono text-xs font-semibold leading-5"
              >
                {{ row.httpMethod || '—' }}
              </span>
              <span class="min-w-0 wrap-anywhere">{{ row.requestUri || '—' }}</span>
            </dd>
          </div>
          <div class="detail-field">
            <dt>{{ $t('system.log.method') }}</dt>
            <dd class="font-mono text-xs">{{ row.methodName || '—' }}</dd>
          </div>
          <div class="detail-field">
            <dt>{{ $t('system.log.duration') }}</dt>
            <dd class="tabular-nums">
              {{ row.durationMs ?? '—' }}
              <span class="text-muted-foreground">ms</span>
            </dd>
          </div>
        </dl>
      </section>

      <section
        v-for="item in payloads"
        :key="item.key"
        class="overflow-hidden rounded-lg border border-border bg-background"
      >
        <h3 class="detail-heading border-b border-border bg-muted/40">
          <IconifyIcon :icon="item.icon" class="size-4 shrink-0 text-primary" aria-hidden="true" />
          {{ item.label }}
        </h3>
        <div class="p-4">
          <div class="relative rounded-md border border-border bg-muted/30">
            <VbenButton
              size="sm"
              variant="outline"
              class="absolute right-3 top-3 z-10 h-7 gap-1 rounded px-2 text-xs font-normal shadow-none"
              :disabled="!item.value"
              @click="copyText(item.value)"
            >
              <IconifyIcon icon="lucide:copy" class="size-3.5" aria-hidden="true" />
              {{ $t('system.log.copy') }}
            </VbenButton>
            <pre
              class="max-h-64 min-h-20 overflow-auto whitespace-pre-wrap wrap-anywhere p-4 pr-24 font-mono text-xs leading-6"
              :class="{ 'text-muted-foreground': !item.value }"
              >{{ formatJson(item.value) }}</pre>
          </div>
        </div>
      </section>

      <section
        v-if="row.status === 0"
        class="overflow-hidden rounded-lg border border-destructive/25 bg-background"
      >
        <h3 class="detail-heading border-b border-destructive/20 bg-destructive/5 text-destructive">
          <IconifyIcon icon="lucide:circle-alert" class="size-4 shrink-0" aria-hidden="true" />
          {{ $t('system.log.errorMessage') }}
        </h3>
        <pre
          class="max-h-64 overflow-auto whitespace-pre-wrap wrap-anywhere p-4 font-mono text-xs leading-6 text-destructive"
          >{{ row.errorMessage || $t('common.noData') }}</pre>
      </section>
    </div>
  </ElDialog>
</template>

<style scoped>
.detail-heading {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
  padding: 10px 18px;
  font-size: 14px;
  font-weight: 600;
  line-height: 20px;
}

.detail-fields {
  margin: 0;
  padding: 16px 18px;
}

.detail-field {
  display: grid;
  grid-template-columns: 88px minmax(0, 1fr);
  align-items: start;
  column-gap: 12px;
  line-height: 26px;
}

.detail-field dt {
  color: var(--el-text-color-secondary);
}

.detail-field dd {
  min-width: 0;
  margin: 0;
  overflow-wrap: anywhere;
}

@media (max-width: 639px) {
  .detail-heading {
    padding: 10px 14px;
  }

  .detail-fields {
    padding: 14px;
  }

  .detail-field {
    grid-template-columns: 76px minmax(0, 1fr);
    column-gap: 8px;
  }
}
</style>
