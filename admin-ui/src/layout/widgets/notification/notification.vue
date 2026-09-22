<script setup lang="ts">
import type { NotificationItem } from '@/components/HeaderNotice/types'
import type { SysNotice } from '@/types/base/api/system/notice'

import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  listNoticeTop,
  markNoticeRead,
  markNoticeReadAll,
} from '@/api/system/notice'
import HeaderNotice from '@/components/HeaderNotice/index.vue'
import { $t } from '@/plugins/locale'
import { preferences } from '@/plugins/preference'
import { formatDate } from '@/utils/date'
import { useDict } from '@/utils/dict'

const router = useRouter()
const headerRef = ref<InstanceType<typeof HeaderNotice>>()
const { sys_notice_type, sys_notice_status } = useDict(
  'sys_notice_type',
  'sys_notice_status',
)
const notices = ref<SysNotice[]>([])
const unreadCount = ref(0)
const loading = ref(false)
const marking = ref(false)
const errorKey = ref('')
const busy = computed(() => loading.value || marking.value)

function getSummary(content = '') {
  if (!content) return ''
  const template = document.createElement('template')
  template.innerHTML = content
  template.content
    .querySelectorAll('script, style, iframe, object, template')
    .forEach((node) => node.remove())
  template.content
    .querySelectorAll('br')
    .forEach((node) => node.replaceWith(' '))
  template.content
    .querySelectorAll('p, div, li, h1, h2, h3, h4, blockquote')
    .forEach((node) => node.append(' '))
  return (template.content.textContent ?? '')
    .replace(/\s+/g, ' ')
    .trim()
    .slice(0, 120)
}

const notifications = computed<NotificationItem[]>(() =>
  notices.value.map((notice) => ({
    id: notice.id,
    title: notice.noticeTitle,
    avatar: notice.avatar || preferences.app.defaultAvatar,
    message: getSummary(notice.noticeContent ?? ''),
    date: formatDate(notice.createTime, 'YYYY-MM-DD HH:mm'),
    isRead: notice.isRead,
    typeLabel: sys_notice_type.value.find(
      (option) => option.value === notice.noticeType,
    )?.label,
  })),
)

async function loadNotices() {
  if (loading.value) return
  loading.value = true
  errorKey.value = ''
  try {
    const result = await listNoticeTop()
    notices.value = result.sysNotice
    unreadCount.value = result.unreadCount
  } catch {
    errorKey.value = 'system.notice.loadFailed'
  } finally {
    loading.value = false
  }
}

function handleOpenChange(open: boolean) {
  if (open && !busy.value) void loadNotices()
}

async function markRead(ids: number[]) {
  const firstId = ids[0]
  if (busy.value || firstId === undefined) return
  marking.value = true
  errorKey.value = ''
  try {
    if (ids.length === 1) await markNoticeRead(firstId)
    else await markNoticeReadAll(ids)

    const selected = new Set(ids)
    const changed = notices.value.filter(
      (notice) => selected.has(notice.id) && !notice.isRead,
    )
    changed.forEach((notice) => {
      notice.isRead = true
    })
    unreadCount.value = Math.max(0, unreadCount.value - changed.length)
    await loadNotices()
  } catch {
    errorKey.value = 'system.notice.markFailed'
  } finally {
    marking.value = false
  }
}

function handleRead(item: NotificationItem) {
  const notice = notices.value.find((row) => row.id === item.id)
  if (notice && !notice.isRead) void markRead([notice.id])
}

function handleMakeAll() {
  // 顶部仅展示最新几条，批量已读不影响列表之外的公告。
  void markRead(
    notices.value.filter((notice) => !notice.isRead).map((notice) => notice.id),
  )
}

async function handleClick(item: NotificationItem) {
  if (busy.value) return
  const notice = notices.value.find((row) => row.id === item.id)
  if (!notice) return

  const link = notice.link?.trim()
  errorKey.value = ''

  if (link) {
    try {
      const url = new URL(link, window.location.origin)
      if (!['http:', 'https:'].includes(url.protocol))
        throw new Error('Invalid notice link')
      if (url.origin === window.location.origin) {
        await router.push(url.pathname + url.search + url.hash)
      } else {
        window.open(url.href, '_blank', 'noopener,noreferrer')
      }
      headerRef.value?.close()
    } catch {
      errorKey.value = 'system.notice.openFailed'
      return
    }
  } else {
    // 顶部接口已返回正文，阅读公告无需访问管理端详情接口。
    headerRef.value?.openDetail({
      ...notice,
      typeLabel: item.typeLabel,
      statusLabel: sys_notice_status.value.find(
        (option) => option.value === notice.status,
      )?.label,
    })
  }

  if (!notice.isRead) await markRead([notice.id])
}

// 用户菜单复用通知面板的展开行为。
defineExpose({
  toggle: () => headerRef.value?.toggle(),
})

onMounted(() => {
  void loadNotices()
})
</script>

<template>
  <HeaderNotice
    ref="headerRef"
    :notifications="notifications"
    :dot="unreadCount > 0"
    :unread-count="unreadCount"
    :loading="loading"
    :marking="marking"
    :error="errorKey ? $t(errorKey) : ''"
    @click="handleClick"
    @read="handleRead"
    @make-all="handleMakeAll"
    @refresh="loadNotices"
    @open-change="handleOpenChange"
  />
</template>
