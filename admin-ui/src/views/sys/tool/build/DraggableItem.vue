<template>
  <el-col
    :span="element.span"
    :class="className"
    @click.stop="activeItem(element)"
  >
    <el-form-item
      v-if="element.layout === 'colFormItem'"
      :label="element.label"
      :label-width="
        element.labelWidth == null ? undefined : element.labelWidth + 'px'
      "
      :required="element.required"
    >
      <render
        :key="element.tag"
        :conf="element"
        v-model="element.defaultValue"
      />
    </el-form-item>
    <el-row
      v-else
      :gutter="element.gutter"
      :justify="element.justify"
      :align="element.align"
      :class="element.class"
      @click.stop="activeItem(element)"
    >
      <span class="component-name">{{ element.componentName }}</span>
      <draggable
        group="componentsGroup"
        :animation="340"
        :list="element.children"
        class="drag-wrapper"
        item-key="formId"
      >
        <template
          #item="{
            element: child,
            index: childIndex,
          }: {
            element: DrawingItem
            index: number
          }"
        >
          <draggable-item
            :key="child.renderKey"
            :drawing-list="element.children"
            :element="child"
            :index="childIndex"
            :active-id="activeId"
            :form-conf="formConf"
            @active-item="activeItem"
            @copy-item="copyItem"
            @delete-item="deleteItem"
          />
        </template>
      </draggable>
    </el-row>
    <span
      class="drawing-item-copy"
      title="复制"
      @click.stop="copyItem(element)"
    >
      <el-icon><CopyDocument /></el-icon>
    </span>
    <span
      class="drawing-item-delete"
      title="删除"
      @click.stop="deleteItem(index)"
    >
      <el-icon><Delete /></el-icon>
    </span>
  </el-col>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { CopyDocument, Delete } from '@element-plus/icons-vue'
import draggable from 'vuedraggable'
import render from '@/utils/generator/render'
import type { DrawingItem, FormConfig } from '@/utils/generator/types'

const props = defineProps<{
  element: DrawingItem
  index: number
  drawingList: DrawingItem[]
  activeId: number
  formConf: FormConfig
}>()
// 与属性面板一样，直接编辑画布中已有节点的字段值。
const element = computed(() => props.element)
const emit = defineEmits<{
  activeItem: [item: DrawingItem]
  copyItem: [item: DrawingItem, parent: DrawingItem[]]
  deleteItem: [index: number, parent: DrawingItem[]]
}>()
const className = computed(() => [
  props.element.layout === 'rowFormItem' ? 'drawing-row-item' : 'drawing-item',
  {
    'active-from-item': props.activeId === props.element.formId,
    'unfocus-bordered': props.formConf.unFocusedComponentBorder,
  },
])

function activeItem(item: DrawingItem): void {
  emit('activeItem', item)
}
function copyItem(item: DrawingItem, parent = props.drawingList): void {
  emit('copyItem', item, parent)
}
function deleteItem(index: number, parent = props.drawingList): void {
  emit('deleteItem', index, parent)
}
</script>
