<script lang="ts" setup>
import type { JsonViewerAction, JsonViewerProps, JsonViewerToggle, JsonViewerValue } from './types';

import { computed, onUnmounted, ref, useAttrs } from 'vue';
import VueJsonPretty from 'vue-json-pretty';
import JsonBigint from 'json-bigint';
import { get } from 'es-toolkit/compat';
import 'vue-json-pretty/lib/styles.css';

type PrettyProps = InstanceType<typeof VueJsonPretty>['$props'];
type JsonData = Exclude<PrettyProps['data'], undefined>;
type PrettyNode = Parameters<NonNullable<PrettyProps['onNodeClick']>>[0];

import { $t } from '@/plugins/locale';

defineOptions({ name: 'JsonViewer', inheritAttrs: false });

const props = withDefaults(defineProps<JsonViewerProps>(), {
  expandDepth: 1,
  copyable: false,
  sort: false,
  boxed: false,
  theme: 'default-json-theme',
  expanded: false,
  previewMode: false,
  showArrayIndex: true,
  showDoubleQuotes: false,
});

const emit = defineEmits<{
  click: [event: MouseEvent];
  copied: [event: JsonViewerAction];
  keyClick: [key: string];
  toggle: [param: JsonViewerToggle];
  valueClick: [value: JsonViewerValue];
}>();

const attrs = useAttrs();
const rootPath = computed(() => (typeof attrs.rootPath === 'string' ? attrs.rootPath : 'root'));

const copiedPath = ref<null | string>(null);

const copyConfig = computed(() => {
  return {
    copiedText: $t('ui.jsonViewer.copied'),
    copyText: $t('ui.jsonViewer.copy'),
    timeout: 2000,
  };
});

let copyTimer: ReturnType<typeof setTimeout> | undefined;
onUnmounted(() => clearTimeout(copyTimer));

function handleCopy(node: PrettyNode, defaultCopy: () => void, event: MouseEvent) {
  defaultCopy();
  copiedPath.value = node.path;
  emit('copied', {
    action: 'copy',
    text:
      JSON.stringify(
        node.path === rootPath.value
          ? jsonData.value
          : get(jsonData.value, node.path.slice(rootPath.value.length).replace(/^\./, '')),
        null,
        2,
      ) ?? '',
    trigger: event.currentTarget instanceof HTMLElement ? event.currentTarget : document.body,
  });
  clearTimeout(copyTimer);
  copyTimer = setTimeout(() => {
    if (copiedPath.value === node.path) {
      copiedPath.value = null;
    }
  }, copyConfig.value.timeout ?? 2000);
}

// 支持显示 bigint 数据，如较长的订单号
function isJsonData(value: unknown): value is Exclude<JsonData, undefined> {
  return (
    value === null ||
    typeof value === 'string' ||
    typeof value === 'number' ||
    typeof value === 'boolean' ||
    Array.isArray(value) ||
    (typeof value === 'object' && value !== null)
  );
}

const jsonData = computed<JsonData>(() => {
  if (typeof props.value !== 'string') {
    return isJsonData(props.value) ? props.value : {};
  }

  try {
    const parsed: unknown = JsonBigint({ storeAsString: true }).parse(props.value);
    return isJsonData(parsed) ? parsed : {};
  } catch (error) {
    console.error('JSON parse error:', error);
    return {};
  }
});

const bindProps = computed<PrettyProps>(() => {
  // 底层组件只接受对象形式的 style；Vue 的字符串/数组样式交给外层容器。
  const forwardedAttrs: Record<string, unknown> = { ...attrs };
  delete forwardedAttrs.style;
  const prettyTheme =
    props.theme === 'dark' || props.theme === 'dark-json-theme' ? 'dark' : 'light';

  return {
    ...forwardedAttrs,
    data: jsonData.value,
    rootPath: rootPath.value,
    deep: props.expanded ? Infinity : props.expandDepth,
    showDoubleQuotes: props.showDoubleQuotes,
    showLine: props.boxed,
    showLength: true,
    showIcon: true,
    theme: prettyTheme,
    collapsedNodeLength: props.previewMode ? 0 : Infinity,
    renderNodeActions: !!props.copyable,
  };
});
</script>
<template>
  <div
    :class="[props.theme, { boxed: props.boxed }]"
    :style="attrs.style"
    class="vben-json-viewer"
    @click="emit('click', $event)"
  >
    <VueJsonPretty v-bind="bindProps">
      <template #renderNodeActions="{ node, defaultActions }">
        <slot name="copy" :node="node" :default-actions="defaultActions">
          <span
            v-if="props.copyable"
            class="vben-json-copy-btn"
            :class="[{ 'is-copied': copiedPath === node.path }]"
            @click.stop="handleCopy(node, defaultActions.copy, $event)"
          >
            {{ copiedPath === node.path ? copyConfig.copiedText : copyConfig.copyText }}
          </span>
        </slot>
      </template>
    </VueJsonPretty>
  </div>
</template>
<style lang="scss">
@use './style.scss';
</style>
