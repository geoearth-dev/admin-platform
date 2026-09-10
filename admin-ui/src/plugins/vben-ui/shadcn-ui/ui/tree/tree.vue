<template>
  <TreeRoot
    v-model:expanded="expanded"
    v-slot="{ flattenItems }"
    :get-key="getNodeKey"
    :get-children="getChildren"
    :items="treeData"
    :model-value="treeValue"
    :multiple="multiple"
    :disabled="disabled"
    :selection-behavior="allowClear || multiple ? 'toggle' : 'replace'"
    :class="cn('tree-container list-none rounded-lg text-sm font-medium', bordered && 'border')"
    @update:model-value="updateModelValue"
  >
    <div
      v-if="$slots.header"
      :class="cn('my-0.5 flex w-full items-center p-1', bordered && 'border-b')"
    >
      <slot name="header" />
    </div>
    <div
      v-if="treeData.length > 0"
      :class="cn('my-0.5 flex w-full items-center gap-1 p-1', bordered && 'border-b')"
    >
      <button
        type="button"
        :disabled="disabled"
        :aria-label="expanded.length ? '收起全部' : '展开全部'"
        :title="expanded.length ? '收起全部' : '展开全部'"
        class="flex size-5 shrink-0 items-center justify-center disabled:cursor-not-allowed disabled:opacity-50"
        @click="expanded.length ? collapseAll() : expandAll()"
      >
        <ChevronRight
          class="size-4 transition-transform"
          :class="{ 'rotate-90': expanded.length > 0 }"
        />
      </button>
      <Checkbox
        v-if="multiple"
        :model-value="selectAllStatus"
        :indeterminate="selectAllStatus === 'indeterminate'"
        :disabled="disabled"
        :aria-label="selectAllLabel || '全选'"
        @update:model-value="onSelectAllChange"
      />
      <span v-if="selectAllLabel">{{ selectAllLabel }}</span>
    </div>
    <TransitionGroup :name="transition ? 'fade' : ''">
      <TreeItem
        v-for="item in flattenItems"
        :key="item._id"
        v-slot="{ isExpanded, isSelected, handleToggle }"
        v-bind="item.bind"
        :disabled="isDisabled(item.value)"
        :style="{ 'margin-left': `${item.level - 1}rem` }"
        :class="
          cn(
            'tree-node my-0.5 flex items-center rounded p-1 outline-hidden focus-visible:ring-2 focus-visible:ring-ring',
            getNodeClass?.(item),
            {
              'cursor-pointer': !isDisabled(item.value),
              'data-selected:bg-accent': !multiple,
              'text-foreground/50 cursor-not-allowed': isDisabled(item.value),
            },
          )
        "
        @select="onTreeSelect($event, item)"
        @toggle="onTreeToggle($event, item)"
      >
        <button
          v-if="item.hasChildren"
          type="button"
          tabindex="-1"
          :disabled="isDisabled(item.value)"
          :aria-label="isExpanded ? '收起节点' : '展开节点'"
          :title="isExpanded ? '收起节点' : '展开节点'"
          class="flex size-4 shrink-0 items-center justify-center disabled:cursor-not-allowed"
          @click.stop="toggleNode(item, handleToggle)"
        >
          <ChevronRight class="size-4 transition-transform" :class="{ 'rotate-90': isExpanded }" />
        </button>
        <span v-else class="size-4 shrink-0" />
        <div class="flex min-w-0 flex-1 items-center gap-1">
          <Checkbox
            v-if="multiple"
            tabindex="-1"
            :model-value="isSelected && !isDisabled(item.value)"
            :disabled="isDisabled(item.value)"
            :indeterminate="isIndeterminate(item.value)"
            :aria-label="getNodeText(item.value, labelField)"
            @click.stop="onSelect(item)"
            @keydown.stop
          />
          <div
            class="flex min-w-0 flex-1 items-center gap-1"
            :title="getNodeText(item.value, labelField)"
          >
            <slot name="node" v-bind="item">
              <IconifyIcon
                v-if="showIcon && getNodeText(item.value, iconField)"
                class="size-4 shrink-0"
                :icon="getNodeText(item.value, iconField)"
              />
              <span class="truncate">{{ getNodeText(item.value, labelField) }}</span>
            </slot>
          </div>
        </div>
      </TreeItem>
    </TransitionGroup>
    <div
      v-if="$slots.footer"
      :class="cn('my-0.5 flex w-full items-center p-1', bordered && 'border-t')"
    >
      <slot name="footer" />
    </div>
  </TreeRoot>
</template>
<script lang="ts" setup>
import type { Arrayable } from '@vueuse/core';
import type { FlattenedItem } from 'reka-ui';
import type { Recordable } from '@/types';
import type { TreeProps } from './types';

import { computed, ref, watch } from 'vue';
import { ChevronRight, IconifyIcon } from '@/assets/icons';
import { TreeItem, TreeRoot } from 'reka-ui';
import { cn } from '@/utils/cn';
import { get } from 'es-toolkit/compat';
import { Checkbox } from '../checkbox';
import { treePropsDefaults } from './types';

type TreeNode = Recordable<unknown>;
type TreeKey = number | string;
type TreeItemData = FlattenedItem<TreeNode>;

const props = withDefaults(defineProps<TreeProps>(), treePropsDefaults());
const emits = defineEmits<{
  expand: [value: TreeItemData];
  select: [value: TreeItemData];
}>();
const modelValue = defineModel<Arrayable<TreeKey>>();
// 默认值只初始化一次，清空后不再回退到默认值。
if (modelValue.value === undefined && props.defaultValue !== undefined) {
  modelValue.value = Array.isArray(props.defaultValue)
    ? [...props.defaultValue]
    : props.defaultValue;
}
const expanded = ref<string[]>(props.defaultExpandedKeys.map(toInternalKey));

// Reka 使用字符串键；加上类型前缀，避免数字 1 和字符串 '1' 混淆。
function toInternalKey(value: TreeKey) {
  return `${typeof value}:${value}`;
}
function getNodeValue(node: TreeNode): TreeKey {
  return get(node, props.valueField) as TreeKey;
}
function getNodeKey(node: TreeNode) {
  return toInternalKey(getNodeValue(node));
}
function getChildren(node: TreeNode): TreeNode[] | undefined {
  const children: unknown = get(node, props.childrenField);
  return Array.isArray(children) && children.length > 0 ? children : undefined;
}
function getNodeText(node: TreeNode, field: string) {
  return String(get(node, field) ?? '');
}
function isDisabled(node: TreeNode) {
  return props.disabled || Boolean(get(node, props.disabledField));
}

interface FlatNode {
  id: TreeKey;
  level: number;
  parents: TreeKey[];
  value: TreeNode;
}
const flattenData = computed(() => {
  const result: FlatNode[] = [];
  function visit(nodes: TreeNode[], parents: TreeKey[] = []) {
    for (const node of nodes) {
      const id = getNodeValue(node);
      result.push({ id, level: parents.length, parents, value: node });
      const children = getChildren(node);
      if (children) visit(children, [...parents, id]);
    }
  }
  visit(props.treeData);
  return result;
});
const nodesByValue = computed(() => new Map(flattenData.value.map((node) => [node.id, node])));
function getItemByValue(value: TreeKey) {
  return nodesByValue.value.get(value)?.value;
}
const selectedKeys = computed(() => {
  const value = modelValue.value;
  return new Set(value === undefined ? [] : Array.isArray(value) ? value : [value]);
});
const treeValue = computed(() => {
  const nodes = [...selectedKeys.value]
    .map(getItemByValue)
    .filter((node): node is TreeNode => !!node && !isDisabled(node));
  return props.multiple ? nodes : nodes[0];
});
function updateModelValue(value: TreeNode | TreeNode[] | undefined) {
  if (props.disabled) return;
  if (Array.isArray(value)) {
    modelValue.value = value.filter((node) => !isDisabled(node)).map(getNodeValue);
  } else {
    modelValue.value = value && !isDisabled(value) ? getNodeValue(value) : undefined;
  }
}
watch(
  () => [props.treeData, props.childrenField, props.valueField, props.defaultExpandedLevel],
  () => {
    if (props.defaultExpandedLevel > 0) expandToLevel(props.defaultExpandedLevel);
  },
  { deep: true, immediate: true },
);
function expandToLevel(level: number) {
  expanded.value = flattenData.value
    .filter((node) => node.level < level && getChildren(node.value))
    .map((node) => toInternalKey(node.id));
}
function collapseNodes(value: Arrayable<TreeKey>) {
  const keys = new Set((Array.isArray(value) ? value : [value]).map(toInternalKey));
  expanded.value = expanded.value.filter((key) => !keys.has(key));
}
function expandNodes(value: Arrayable<TreeKey>) {
  const keys = new Set(expanded.value);
  for (const key of Array.isArray(value) ? value : [value]) {
    const node = getItemByValue(key);
    if (node && getChildren(node)) keys.add(toInternalKey(key));
  }
  expanded.value = [...keys];
}
function expandAll() {
  expanded.value = flattenData.value
    .filter((node) => getChildren(node.value))
    .map((node) => toInternalKey(node.id));
}
function collapseAll() {
  expanded.value = [];
}
const selectableNodes = computed(() => flattenData.value.filter((node) => !isDisabled(node.value)));
function checkAll() {
  if (!props.multiple || props.disabled) return;
  modelValue.value = selectableNodes.value.map((node) => node.id);
}
function unCheckAll() {
  if (!props.multiple || props.disabled) return;
  modelValue.value = [];
}
const selectAllStatus = computed<'indeterminate' | boolean>(() => {
  if (!props.multiple) return false;
  const count = selectableNodes.value.filter((node) => selectedKeys.value.has(node.id)).length;
  if (count === 0) return false;
  return count === selectableNodes.value.length ? true : 'indeterminate';
});
function onSelectAllChange(checked: 'indeterminate' | boolean) {
  if (checked === true) checkAll();
  else unCheckAll();
}
function isIndeterminate(node: TreeNode) {
  if (!props.multiple || props.checkStrictly || isDisabled(node)) return false;
  const id = getNodeValue(node);
  const children = selectableNodes.value.filter((child) => child.parents.includes(id));
  const count = children.filter((child) => selectedKeys.value.has(child.id)).length;
  return count > 0 && count < children.length;
}

// 使用同一份扁平数据处理关联选择，兼容自定义 childrenField 和禁用节点。
function onSelect(item: TreeItemData) {
  if (isDisabled(item.value)) return;
  const id = getNodeValue(item.value);
  const selected = !selectedKeys.value.has(id);
  if (!props.multiple) {
    modelValue.value = selected || !props.allowClear ? id : undefined;
  } else {
    const keys = new Set(
      selectableNodes.value
        .filter((node) => selectedKeys.value.has(node.id))
        .map((node) => node.id),
    );
    const setChecked = (key: TreeKey, checked: boolean) => {
      if (checked) keys.add(key);
      else keys.delete(key);
    };
    setChecked(id, selected);
    if (!props.checkStrictly) {
      for (const child of selectableNodes.value) {
        if (child.parents.includes(id)) setChecked(child.id, selected);
      }
    }
    if (!props.checkStrictly || props.autoCheckParent) {
      const parents = nodesByValue.value.get(id)?.parents ?? [];
      for (const parentId of [...parents].reverse()) {
        const parent = getItemByValue(parentId);
        if (!parent || isDisabled(parent)) continue;
        // 严格模式只补选祖先；取消时，仍有已选后代就保留祖先。
        const descendants = selectableNodes.value.filter((node) => node.parents.includes(parentId));
        const checked = props.checkStrictly
          ? descendants.some((node) => keys.has(node.id))
          : descendants.length > 0 && descendants.every((node) => keys.has(node.id));
        setChecked(parentId, checked);
      }
    }
    modelValue.value = [...keys];
  }
  emits('select', item);
}
function onTreeSelect(event: Event, item: TreeItemData) {
  event.preventDefault();
  onSelect(item);
}
function onTreeToggle(event: CustomEvent<{ originalEvent: Event }>, item: TreeItemData) {
  // 行点击只选择，箭头按钮和方向键负责展开。
  if (event.detail.originalEvent.type === 'click' || isDisabled(item.value)) {
    event.preventDefault();
    return;
  }
  emits('expand', item);
}
function toggleNode(item: TreeItemData, toggle: () => void) {
  if (isDisabled(item.value)) return;
  toggle();
  emits('expand', item);
}
defineExpose({
  collapseAll,
  collapseNodes,
  expandAll,
  expandNodes,
  checkAll,
  unCheckAll,
  expandToLevel,
  getItemByValue,
});
</script>

<style scoped>
.tree-container {
  position: relative;
  padding: 0;
  list-style-type: none;
}
.fade-move,
.fade-enter-active,
.fade-leave-active {
  transition: all 0.5s cubic-bezier(0.55, 0, 0.1, 1);
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: scaleY(0.01) translate(30px, 0);
}
.fade-leave-active {
  position: absolute;
}
</style>
