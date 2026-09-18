<script setup lang="ts">
import type { NoticeDetail } from './types'
import { ref } from 'vue'
import { VbenTiptapPreview } from '@/components/tiptap'
import { $t } from '@/plugins/locale'
import { useVbenDrawer } from '@/plugins/vben-ui/popup-ui'
import { formatDateTime } from '@/utils/date'

const detail = ref<NoticeDetail>()
const [Drawer, drawerApi] = useVbenDrawer({ footer: false })

function open(value: NoticeDetail) {
  detail.value = value
  drawerApi.open()
}

defineExpose({ open })
</script>

<template>
  <Drawer
    :title="$t('system.notice.detail')"
    class="w-[720px] max-w-full sm:max-w-[720px]"
  >
    <article
      v-if="detail"
      class="space-y-5 p-3 text-foreground"
    >
      <div class="flex flex-wrap items-start gap-3">
        <h2 class="min-w-0 flex-1 wrap-break-word text-xl font-semibold">
          {{ detail.noticeTitle }}
        </h2>
        <span
          v-if="detail.typeLabel"
          class="rounded border border-border bg-muted px-2 py-1 text-xs"
          >{{ detail.typeLabel }}</span
        >
      </div>
      <div
        class="flex flex-wrap gap-x-5 gap-y-2 border-y border-border py-3 text-xs text-muted-foreground"
      >
        <span
          >{{ $t('system.notice.publisher') }}：{{
            detail.createBy || '—'
          }}</span
        >
        <span>{{ formatDateTime(detail.createTime) }}</span>
        <span v-if="detail.statusLabel">{{ detail.statusLabel }}</span>
      </div>
      <VbenTiptapPreview
        v-if="detail.noticeContent"
        :content="detail.noticeContent"
      />
      <div
        v-else
        class="py-10 text-center text-sm text-muted-foreground"
      >
        {{ $t('system.notice.noContent') }}
      </div>
    </article>
  </Drawer>
</template>
