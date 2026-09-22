<script setup lang="ts">
import type { NoticeDetail, NotificationItem } from './types';

import { computed, ref, watch } from 'vue';
import { Bell, CircleCheckBig, Inbox, LoaderCircle, MailCheck, RotateCw } from '@/assets/icons';
import { $t } from '@/plugins/locale';
import {
  VbenButton,
  VbenIconButton,
  VbenPopover,
  VbenScrollbar,
} from '@/plugins/vben-ui/shadcn-ui';
import DetailView from './DetailView.vue';

const props = withDefaults(
  defineProps<{
    notifications?: NotificationItem[];
    dot?: boolean;
    unreadCount?: number;
    loading?: boolean;
    marking?: boolean;
    error?: string;
  }>(),
  {
    notifications: () => [],
    dot: false,
    unreadCount: 0,
    loading: false,
    marking: false,
    error: '',
  },
);

const emit = defineEmits<{
  click: [item: NotificationItem];
  read: [item: NotificationItem];
  makeAll: [];
  refresh: [];
  openChange: [open: boolean];
}>();

const open = ref(false);
const detailRef = ref<InstanceType<typeof DetailView>>();
const busy = computed(() => props.loading || props.marking);
const hasUnread = computed(() => props.notifications.some((item) => !item.isRead));

watch(open, (value) => emit('openChange', value));

function close() {
  open.value = false;
}

function toggle() {
  open.value = !open.value;
}

function openDetail(detail: NoticeDetail) {
  close();
  detailRef.value?.open(detail);
}

defineExpose({ close, openDetail, toggle });
</script>

<template>
  <VbenPopover
    v-model:open="open"
    content-class="notice-panel relative right-2 w-[420px] max-w-[calc(100vw-1.5rem)] overflow-hidden rounded-2xl border-border/80 p-0 shadow-2xl"
  >
    <template #trigger>
      <div class="mr-2 flex-center h-full">
        <VbenIconButton
          class="bell-button relative text-foreground"
          :aria-label="$t('system.notice.title')"
          :tooltip="$t('system.notice.title')"
          @click.stop="toggle"
        >
          <span
            v-if="dot"
            class="absolute right-0.5 top-0.5 size-2 rounded-full bg-primary ring-2 ring-background"
          />
          <Bell class="size-4" />
        </VbenIconButton>
      </div>
    </template>

    <section :aria-busy="busy">
      <header class="bg-linear-to-br from-primary/10 via-primary/5 to-transparent px-5 pb-4 pt-5">
        <div class="flex items-start justify-between gap-3">
          <div class="flex items-center gap-3">
            <span
              class="flex size-10 items-center justify-center rounded-xl border border-primary/15 bg-primary/10 text-primary"
              ><Bell class="size-5"
            /></span>
            <div>
              <h2 class="text-base font-semibold tracking-tight text-foreground">
                {{ $t('system.notice.title') }}
              </h2>
              <p class="mt-0.5 text-xs text-muted-foreground">
                {{ $t('system.notice.latest') }}
              </p>
            </div>
          </div>
          <VbenIconButton
            class="size-8 rounded-lg text-muted-foreground"
            :disabled="busy"
            :tooltip="$t('system.notice.refresh')"
            @click="emit('refresh')"
            ><RotateCw class="size-3.5" :class="{ 'animate-spin': loading }"
          /></VbenIconButton>
        </div>
        <div class="mt-4 flex items-center justify-between gap-3">
          <span
            class="inline-flex items-center gap-1.5 rounded-full border border-border/70 bg-background/70 px-2.5 py-1 text-xs text-muted-foreground"
          >
            <span
              class="size-1.5 rounded-full"
              :class="unreadCount ? 'bg-primary' : 'bg-muted-foreground/50'"
            />
            {{
              unreadCount
                ? $t('system.notice.unreadCount', { count: unreadCount })
                : $t('system.notice.allCaughtUp')
            }}
          </span>
          <VbenButton
            size="sm"
            variant="link"
            class="h-auto gap-1.5 p-0 text-xs font-normal"
            :disabled="busy || !hasUnread"
            @click="emit('makeAll')"
          >
            <LoaderCircle v-if="marking" class="size-3.5 animate-spin" /><MailCheck
              v-else
              class="size-3.5"
            />
            {{ $t(marking ? 'system.notice.marking' : 'system.notice.markCurrentRead') }}
          </VbenButton>
        </div>
      </header>

      <div
        v-if="error"
        class="mx-3 my-2 rounded-lg bg-destructive/10 px-3 py-2 text-xs text-destructive"
        role="alert"
      >
        {{ error }}
      </div>
      <div
        v-if="loading && !notifications.length"
        class="flex min-h-52 items-center justify-center gap-2 text-sm text-muted-foreground"
        role="status"
      >
        <LoaderCircle class="size-4 animate-spin" />{{ $t('system.notice.loading') }}
      </div>
      <VbenScrollbar v-if="notifications.length">
        <ul class="flex! max-h-[420px] flex-col gap-1 p-2">
          <li
            v-for="item in notifications"
            :key="item.id"
            class="notice-card group relative flex gap-2 rounded-xl border p-3 transition-colors"
            :class="
              item.isRead
                ? 'border-transparent hover:border-border/70 hover:bg-muted/60'
                : 'border-primary/10 bg-primary/5 hover:bg-primary/10'
            "
          >
            <span
              v-if="!item.isRead"
              class="pointer-events-none absolute right-3 top-3 size-1.5 rounded-full bg-primary"
            />
            <button
              type="button"
              class="min-w-0 flex-1 rounded-lg text-left outline-offset-4 focus-visible:outline-2 focus-visible:outline-primary disabled:cursor-wait"
              :disabled="busy"
              @click="emit('click', item)"
            >
              <div class="flex items-start gap-3">
                <img
                  :src="item.avatar"
                  alt=""
                  class="size-10 shrink-0 rounded-xl object-cover ring-1 ring-border/60"
                />
                <div class="min-w-0 flex-1">
                  <p
                    class="pr-2 text-sm leading-5 text-foreground wrap-break-word"
                    :class="item.isRead ? 'font-normal' : 'font-semibold'"
                  >
                    {{ item.title }}
                  </p>
                  <p
                    v-if="item.message"
                    class="mt-1 line-clamp-2 text-xs leading-5 text-muted-foreground wrap-break-word"
                  >
                    {{ item.message }}
                  </p>
                  <div
                    class="mt-2 flex flex-wrap items-center gap-2 text-[11px] text-muted-foreground"
                  >
                    <span
                      v-if="item.typeLabel"
                      class="rounded-md bg-muted px-1.5 py-0.5 text-foreground/75"
                      >{{ item.typeLabel }}</span
                    >
                    <span class="tabular-nums">{{ item.date }}</span>
                  </div>
                </div>
              </div>
            </button>
            <VbenIconButton
              v-if="!item.isRead"
              size="xs"
              variant="ghost"
              class="mt-5 size-7 shrink-0 self-center rounded-full p-0 text-muted-foreground hover:bg-primary/10 hover:text-primary"
              :disabled="busy"
              :tooltip="$t('common.confirm')"
              @click="emit('read', item)"
              ><CircleCheckBig class="size-4"
            /></VbenIconButton>
          </li>
        </ul>
      </VbenScrollbar>
      <div
        v-else-if="!loading && !error"
        class="flex min-h-52 flex-col items-center justify-center gap-3 text-muted-foreground"
      >
        <span class="rounded-2xl bg-muted p-4"><Inbox class="size-7 opacity-60" /></span>
        <p class="text-sm">{{ $t('common.noData') }}</p>
      </div>
      <footer
        v-if="notifications.length"
        class="flex items-center justify-between border-t border-border/60 bg-muted/20 px-5 py-2.5 text-[11px] text-muted-foreground"
      >
        <span>{{ $t('system.notice.shownCount', { count: notifications.length }) }}</span>
        <LoaderCircle v-if="loading" class="size-3 animate-spin" />
        <span v-else class="size-1 rounded-full bg-primary/60" />
      </footer>
    </section>
  </VbenPopover>
  <DetailView ref="detailRef" />
</template>

<style scoped>
:deep(.bell-button:hover svg) {
  animation: bell-ring 1s both;
}
@keyframes bell-ring {
  0%,
  100% {
    transform-origin: top;
  }
  15% {
    transform: rotateZ(10deg);
  }
  30% {
    transform: rotateZ(-10deg);
  }
  45% {
    transform: rotateZ(5deg);
  }
  60% {
    transform: rotateZ(-5deg);
  }
  75% {
    transform: rotateZ(2deg);
  }
}
</style>
