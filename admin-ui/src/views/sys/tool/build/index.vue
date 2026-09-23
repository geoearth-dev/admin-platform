<template>
  <div class="form-builder">
    <div class="builder-header">
      <div class="logo">
        <img
          :src="logo"
          alt="logo"
        />
        <span>Form Generator</span>
      </div>
      <div class="action-bar">
        <el-button
          :icon="Download"
          type="primary"
          text
          title="导出vue文件"
          aria-label="导出vue文件"
          @click="download"
        >
          导出vue文件
        </el-button>
        <el-button
          class="copy-btn-main"
          :icon="DocumentCopy"
          type="primary"
          text
          title="复制代码"
          aria-label="复制代码"
          @click="copy"
        >
          复制代码
        </el-button>
        <el-button
          class="delete-btn"
          :icon="Delete"
          text
          title="清空"
          aria-label="清空"
          @click="empty"
          type="danger"
        >
          清空
        </el-button>
      </div>
    </div>
    <div class="left-board">
      <el-scrollbar class="left-scrollbar">
        <div class="components-list">
          <div class="components-title">
            <IconifyIcon
              class="svg-icon inline-block align-[-2px]"
              icon="svg:component"
            />输入型组件
          </div>
          <draggable
            class="components-draggable"
            :list="inputComponents"
            :group="{ name: 'componentsGroup', pull: 'clone', put: false }"
            :clone="cloneComponent"
            draggable=".components-item"
            :sort="false"
            @end="onEnd"
            item-key="label"
          >
            <template
              #item="{
                element,
                index,
              }: {
                element: PaletteItem
                index: number
              }"
            >
              <div
                :key="index"
                class="components-item"
                @click="addComponent(element)"
              >
                <div class="components-body">
                  <IconifyIcon
                    class="svg-icon inline-block align-[-2px]"
                    :icon="`svg:${element.tagIcon}`"
                  />
                  {{ element.label }}
                </div>
              </div>
            </template>
          </draggable>
          <div class="components-title">
            <IconifyIcon
              class="svg-icon inline-block align-[-2px]"
              icon="svg:component"
            />选择型组件
          </div>
          <draggable
            class="components-draggable"
            :list="selectComponents"
            :group="{ name: 'componentsGroup', pull: 'clone', put: false }"
            :clone="cloneComponent"
            draggable=".components-item"
            :sort="false"
            @end="onEnd"
            item-key="label"
          >
            <template
              #item="{
                element,
                index,
              }: {
                element: PaletteItem
                index: number
              }"
            >
              <div
                :key="index"
                class="components-item"
                @click="addComponent(element)"
              >
                <div class="components-body">
                  <IconifyIcon
                    class="svg-icon inline-block align-[-2px]"
                    :icon="`svg:${element.tagIcon}`"
                  />
                  {{ element.label }}
                </div>
              </div>
            </template>
          </draggable>
          <div class="components-title">
            <IconifyIcon
              class="svg-icon inline-block align-[-2px]"
              icon="svg:component"
            />
            布局型组件
          </div>
          <draggable
            class="components-draggable"
            :list="layoutComponents"
            :group="{ name: 'componentsGroup', pull: 'clone', put: false }"
            :clone="cloneComponent"
            draggable=".components-item"
            :sort="false"
            @end="onEnd"
            item-key="label"
          >
            <template
              #item="{
                element,
                index,
              }: {
                element: PaletteItem
                index: number
              }"
            >
              <div
                :key="index"
                class="components-item"
                @click="addComponent(element)"
              >
                <div class="components-body">
                  <IconifyIcon
                    class="svg-icon inline-block align-[-2px]"
                    :icon="`svg:${element.tagIcon}`"
                  />
                  {{ element.label }}
                </div>
              </div>
            </template>
          </draggable>
        </div>
      </el-scrollbar>
    </div>
    <div class="center-board">
      <el-scrollbar class="center-scrollbar">
        <el-row
          class="center-board-row"
          :gutter="formConf.gutter"
        >
          <el-form
            :size="formConf.size"
            :label-position="formConf.labelPosition"
            :disabled="formConf.disabled"
            :label-width="formConf.labelWidth + 'px'"
          >
            <draggable
              class="drawing-board"
              :list="drawingList"
              :animation="340"
              group="componentsGroup"
              item-key="formId"
            >
              <template
                #item="{
                  element,
                  index,
                }: {
                  element: DrawingItem
                  index: number
                }"
              >
                <draggable-item
                  :key="element.renderKey"
                  :drawing-list="drawingList"
                  :element="element"
                  :index="index"
                  :active-id="activeId"
                  :form-conf="formConf"
                  @activeItem="activeFormItem"
                  @copyItem="drawingItemCopy"
                  @deleteItem="drawingItemDelete"
                />
              </template>
            </draggable>
            <div
              v-show="!drawingList.length"
              class="empty-info"
            >
              从左侧拖入或点选组件进行表单设计
            </div>
          </el-form>
        </el-row>
      </el-scrollbar>
    </div>
    <right-panel
      :active-data="activeData"
      :form-conf="formConf"
      :show-field="!!drawingList.length"
      @tag-change="tagChange"
    />

    <code-type-dialog
      v-model="dialogVisible"
      title="选择生成类型"
      :showFileName="showFileName"
      @confirm="generate"
    />
  </div>
</template>

<script setup lang="ts">
import { IconifyIcon } from '@/assets/icons'
import draggable from 'vuedraggable'
import { useClipboard } from '@vueuse/core'
import beautifier from 'js-beautify'
import { Download, DocumentCopy, Delete } from '@element-plus/icons-vue'
import { preferences } from '@/plugins/preference'
import type { SortableEvent } from 'sortablejs'
import {
  inputComponents,
  selectComponents,
  layoutComponents,
  formConf as formConfData,
} from '@/utils/generator/config'

import { beautifierConf } from '@/utils/generator/beautifier'
import {
  drawingDefaultValue,
  initDrawingDefaultValue,
  cleanDrawingDefaultValue,
} from '@/utils/generator/drawingDefault'
import {
  makeUpHtml,
  vueTemplate,
  vueScript,
  cssStyle,
  type HtmlFormConfig,
} from '@/utils/generator/html'
import { generateScript } from '@/utils/generator/script'
import { makeUpCss } from '@/utils/generator/css'
import { downloadFileFromBlob } from '@/utils/download'
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import DraggableItem from './DraggableItem.vue'
import RightPanel from './RightPanel.vue'
import CodeTypeDialog from './CodeTypeDialog.vue'
import { computed, nextTick, ref, watch } from 'vue'
import { cloneDeep } from 'es-toolkit'
import type {
  DrawingItem,
  PaletteItem,
  FormConfig,
  GenerateOptions,
} from '@/utils/generator/types'

const logo = computed(() => preferences.logo.source)

const { copy: copyText } = useClipboard({ legacy: true })

initDrawingDefaultValue()

const drawingList = ref<DrawingItem[]>(cloneDeep(drawingDefaultValue))
const dialogVisible = ref<boolean>(false)
const showFileName = ref<boolean>(false)
const operationType = ref<'copy' | 'download' | ''>('')
const idGlobal = ref<number>(getMaxFormId(drawingList.value))
let renderKeyGlobal = Date.now()
const activeData = ref<DrawingItem | null>(drawingList.value[0] ?? null)
const activeId = ref<number>(drawingDefaultValue[0]?.formId ?? 0)
const generateConf = ref<GenerateOptions | null>(null)

const formData = ref<HtmlFormConfig>({ ...formConfData, fields: [] })
const formConf = ref<FormConfig>(cloneDeep(formConfData))
let oldActiveId: number
let tempActiveData: DrawingItem | undefined

function getMaxFormId(list: DrawingItem[]): number {
  return list.reduce(
    (max, item) =>
      Math.max(
        max,
        typeof item.formId === 'number' ? item.formId : 0,
        Array.isArray(item.children) ? getMaxFormId(item.children) : 0,
      ),
    100,
  )
}

function activeFormItem(element: DrawingItem): void {
  activeData.value = element
  activeId.value = element.formId
}
function copy(): void {
  dialogVisible.value = true
  showFileName.value = false
  operationType.value = 'copy'
}
function download(): void {
  dialogVisible.value = true
  showFileName.value = true
  operationType.value = 'download'
}
async function empty(): Promise<void> {
  try {
    await ElMessageBox.confirm('确定要清空所有组件吗？', '提示', {
      type: 'warning',
    })
  } catch {
    return
  }
  idGlobal.value = 100
  drawingList.value = []
  activeData.value = null
  activeId.value = 0
  tempActiveData = undefined
  cleanDrawingDefaultValue()
}

function onEnd(event: SortableEvent): void {
  if (event.from !== event.to && tempActiveData) {
    activeFormItem(tempActiveData)
  }
  tempActiveData = undefined
}

function addComponent(item: PaletteItem): void {
  const clone = cloneComponent(item)
  drawingList.value.push(clone)
  activeFormItem(clone)
}

function cloneComponent(origin: PaletteItem): DrawingItem {
  const clone: DrawingItem = {
    ...cloneDeep(origin),
    formId: 0,
    layout: origin.layout ?? 'colFormItem',
    children: [],
  }
  clone.formId = ++idGlobal.value
  clone.span = formConf.value.span
  clone.renderKey = ++renderKeyGlobal // 改变renderKey后可以实现强制更新组件
  if (!clone.layout) clone.layout = 'colFormItem'
  if (clone.layout === 'colFormItem') {
    clone.vModel = `field${idGlobal.value}`
    if (clone.placeholder !== undefined) {
      clone.placeholder += clone.label ?? ''
    }
    tempActiveData = clone
  } else if (clone.layout === 'rowFormItem') {
    delete clone.label
    clone.componentName = `row${idGlobal.value}`
    clone.gutter = formConf.value.gutter
    tempActiveData = clone
  }
  return clone
}

function drawingItemCopy(item: DrawingItem, parent: DrawingItem[]): void {
  let clone = cloneDeep(item)
  clone = createIdAndKey(clone)
  parent.push(clone)
  activeFormItem(clone)
}

function createIdAndKey(item: DrawingItem): DrawingItem {
  item.formId = ++idGlobal.value
  item.renderKey = ++renderKeyGlobal
  if (item.layout === 'colFormItem') {
    item.vModel = `field${idGlobal.value}`
  } else if (item.layout === 'rowFormItem') {
    item.componentName = `row${idGlobal.value}`
  }
  if (Array.isArray(item.children)) {
    item.children = item.children.map((childItem) => createIdAndKey(childItem))
  }
  return item
}

function drawingItemDelete(index: number, parent: DrawingItem[]): void {
  if (index < 0 || index >= parent.length) return
  parent.splice(index, 1)
  nextTick(() => {
    const lastItem = drawingList.value.at(-1)
    if (lastItem) {
      activeFormItem(lastItem)
    } else {
      activeData.value = null
      activeId.value = 0
    }
  })
}

function tagChange(newTag: PaletteItem): void {
  const current = activeData.value
  if (!current) return
  const clonedTag = cloneComponent(newTag)
  clonedTag.vModel = current.vModel
  clonedTag.formId = activeId.value
  clonedTag.span = current.span
  function retainProperty<K extends keyof DrawingItem>(key: K): void {
    if (['tag', 'tagIcon', 'document', 'renderKey'].includes(key)) return
    const value = current?.[key]
    if (
      value !== undefined &&
      typeof value === typeof clonedTag[key] &&
      Array.isArray(value) === Array.isArray(clonedTag[key])
    ) {
      clonedTag[key] = value
    }
  }
  // 两个对象均为 DrawingItem；仅复制新组件已经支持的配置项。
  ;(Object.keys(clonedTag) as Array<keyof DrawingItem>).forEach(retainProperty)
  activeData.value = clonedTag
  updateDrawingList(clonedTag, drawingList.value)
}

function updateDrawingList(newTag: DrawingItem, list: DrawingItem[]): void {
  const index = list.findIndex((item) => item.formId === activeId.value)
  if (index > -1) {
    list.splice(index, 1, newTag)
  } else {
    list.forEach((item) => {
      if (Array.isArray(item.children)) updateDrawingList(newTag, item.children)
    })
  }
}
function generate(data: GenerateOptions): void {
  generateConf.value = data
  if (operationType.value === 'copy') {
    void execCopy()
  } else if (operationType.value === 'download') {
    try {
      execDownload(data)
    } catch (error) {
      ElMessage.error(
        error instanceof Error ? error.message : '代码导出失败，请重试',
      )
    }
  }
}

function execDownload(data: GenerateOptions): void {
  const codeStr = generateCode()
  const blob = new Blob([codeStr], { type: 'text/plain;charset=utf-8' })
  downloadFileFromBlob({
    source: blob,
    fileName: data.fileName?.trim() || 'form.vue',
  })
}

async function execCopy(): Promise<void> {
  try {
    const codeStr = generateCode()
    if (!codeStr) {
      ElMessage.warning('暂无可复制的代码')
      return
    }
    await copyText(codeStr)
    ElNotification({
      title: '成功',
      message: '代码已复制到剪贴板，可粘贴。',
      type: 'success',
    })
  } catch (error) {
    ElMessage.error(
      error instanceof Error ? error.message : '代码复制失败，请重试',
    )
  }
}
function AssembleFormData(): void {
  formData.value = {
    ...formConf.value,
    fields: cloneDeep(drawingList.value),
  }
}
function generateCode(): string {
  if (!generateConf.value) return ''

  const { type } = generateConf.value
  AssembleFormData()

  const script = vueScript(generateScript(formData.value, type))
  const html = vueTemplate(makeUpHtml(formData.value, type))
  const css = cssStyle(makeUpCss(formData.value))

  return beautifier.html(html + script + css, beautifierConf.html)
}

watch(
  () => activeData.value?.label,
  (val: string | undefined, oldVal: string | undefined) => {
    if (
      !activeData.value ||
      typeof activeData.value.placeholder !== 'string' ||
      typeof val !== 'string' ||
      !activeData.value.tag ||
      oldActiveId !== activeId.value
    ) {
      return
    }
    activeData.value.placeholder =
      activeData.value.placeholder.replace(oldVal ?? '', '') + val
  },
)
watch(
  activeId,
  (val: number) => {
    oldActiveId = val
  },
  { immediate: true },
)
</script>

<style>
.form-builder {
  --builder-accent: var(--el-color-primary);
  --builder-card-bg: color-mix(in srgb, var(--builder-accent) 8%, var(--el-bg-color-overlay));
  --builder-hover-bg: color-mix(in srgb, var(--builder-accent) 12%, var(--el-bg-color-overlay));
  --builder-active-bg: color-mix(in srgb, var(--builder-accent) 18%, var(--el-bg-color-overlay));
  --builder-border-color: color-mix(in srgb, var(--builder-accent) 35%, var(--el-border-color));
  position: relative;
  width: 100%;
  background-color: var(--el-bg-color-overlay);
  height: calc(100vh - 50px - 40px);
  overflow: hidden;
}
.form-builder .builder-header {
  container: builder-toolbar/inline-size;
  position: absolute;
  left: 0;
  right: 350px;
  top: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  height: 42px;
  padding: 0 12px;
  border-bottom: 1px solid var(--el-border-color-extra-light);
  box-sizing: border-box;
}
.form-builder .builder-header .logo {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
  line-height: 30px;
  color: var(--el-color-primary);
  font-weight: 600;
  font-size: 17px;
  white-space: nowrap;
}
.form-builder .builder-header .logo > img {
  display: block;
  flex: 0 0 30px;
  width: 30px;
  height: 30px;
  object-fit: contain;
}
.form-builder .builder-header .logo > span {
  overflow: hidden;
  text-overflow: ellipsis;
}
.form-builder .builder-header .action-bar {
  display: flex;
  flex-shrink: 0;
  align-items: center;
  gap: 4px;
  margin-left: auto;
}
.form-builder .builder-header .action-bar .el-button + .el-button {
  margin-left: 0;
}
@container builder-toolbar (max-width: 550px) {
  .form-builder .logo > span {
    display: none;
  }
}
@container builder-toolbar (max-width: 400px) {
  .form-builder .action-bar .el-button {
    width: 32px;
    height: 32px;
    padding: 0;
  }
  .form-builder .action-bar .el-button > span {
    display: none;
  }
}
@container builder-toolbar (max-width: 160px) {
  .form-builder .logo {
    display: none;
  }
}
.form-builder .left-board {
  width: 260px;
  position: absolute;
  left: 0;
  top: 42px;
  height: calc(100vh - 50px - 40px - 42px);
}
.form-builder .left-board .left-scrollbar {
  height: 100%;
}
.form-builder .left-board .left-scrollbar .el-scrollbar__wrap {
  box-sizing: border-box;
  overflow-x: hidden !important;
  margin-bottom: 0 !important;
}
.form-builder .left-board .left-scrollbar .el-scrollbar__wrap .components-list {
  padding: 8px;
  box-sizing: border-box;
  height: 100%;
}
.form-builder .left-board .left-scrollbar .el-scrollbar__wrap .components-list .svg-icon {
  color: inherit;
  fill: currentColor;
}
.form-builder .left-board .left-scrollbar .el-scrollbar__wrap .components-list .components-title {
  font-size: 14px;
  margin: 6px 2px;
}
.form-builder .left-board .left-scrollbar .el-scrollbar__wrap .components-list .components-title .svg-icon {
  font-size: 18px;
  margin-right: 5px;
}
.form-builder .left-board .left-scrollbar .el-scrollbar__wrap .components-list .components-draggable {
  padding-bottom: 20px;
}
.form-builder .left-board .left-scrollbar .el-scrollbar__wrap .components-list .components-draggable .components-item {
  display: inline-block;
  width: 48%;
  margin: 1%;
  transition: transform 0ms !important;
}
.form-builder .left-board .left-scrollbar .el-scrollbar__wrap .components-list .components-draggable .components-item .components-body {
  padding: 8px 10px;
  background: var(--builder-card-bg);
  font-size: 12px;
  cursor: move;
  border: 1px dashed var(--builder-border-color);
  border-radius: 3px;
}
.form-builder .left-board .left-scrollbar .el-scrollbar__wrap .components-list .components-draggable .components-item .components-body .svg-icon {
  font-size: 15px;
  margin-right: 5px;
}
.form-builder .left-board .left-scrollbar .el-scrollbar__wrap .components-list .components-draggable .components-item .components-body:hover {
  background: var(--builder-hover-bg);
  border-color: var(--builder-accent);
  color: var(--builder-accent);
}
.form-builder .left-board .left-scrollbar .el-scrollbar__wrap .components-list .components-draggable .components-item .components-body:hover .svg-icon {
  color: var(--builder-accent);
}
.form-builder .center-board {
  height: calc(100vh - 50px - 40px);
  width: auto;
  margin: 0 350px 0 260px;
  padding-top: 42px;
  box-sizing: border-box;
}
.form-builder .center-board .center-scrollbar {
  height: calc(100vh - 50px - 40px - 42px);
  overflow: hidden;
  border-left: 1px solid var(--el-border-color-extra-light);
  border-right: 1px solid var(--el-border-color-extra-light);
  box-sizing: border-box;
}
.form-builder .center-board .center-scrollbar .el-scrollbar__view {
  overflow-x: hidden;
}
.form-builder .center-board .center-scrollbar .center-board-row {
  padding: 12px 12px 15px 12px;
  box-sizing: border-box;
}
.form-builder .center-board .center-scrollbar .center-board-row > .el-form {
  height: calc(100vh - 50px - 40px - 69px);
  flex: 1;
}
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-board {
  height: 100%;
  position: relative;
}
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-board .components-body {
  padding: 0;
  margin: 0;
  font-size: 0;
}
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-board .sortable-ghost {
  position: relative;
  display: block;
  overflow: hidden;
}
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-board .sortable-ghost::before {
  content: " ";
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  height: 3px;
  background: var(--el-color-primary);
  z-index: 2;
}
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-board .components-item.sortable-ghost {
  width: 100%;
  height: 60px;
  background: var(--builder-active-bg);
}
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-board .active-from-item > .el-form-item {
  background: var(--builder-active-bg);
  border-radius: 6px;
  box-shadow: inset 0 0 0 1px var(--builder-border-color);
}
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-board .active-from-item > .drawing-item-copy, .form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-board .active-from-item > .drawing-item-delete {
  display: initial;
}
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-board .active-from-item > .component-name {
  color: var(--el-color-primary);
}
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-board .active-from-item .el-input__wrapper {
  box-shadow: 0 0 0 1px var(--el-input-hover-border-color) inset;
}
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-board .el-form-item {
  margin-bottom: 15px;
}
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-item {
  position: relative;
  cursor: move;
}
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-item.unfocus-bordered:not(.active-from-item) > div:first-child {
  border: 1px dashed var(--el-border-color);
}
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-item .el-form-item {
  padding: 12px 10px;
}
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-row-item {
  position: relative;
  cursor: move;
  box-sizing: border-box;
  border: 1px dashed var(--el-border-color);
  border-radius: 3px;
  padding: 0 2px;
  margin-bottom: 15px;
}
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-row-item .drawing-row-item {
  margin-bottom: 2px;
}
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-row-item .el-col {
  margin-top: 22px;
}
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-row-item .el-form-item {
  margin-bottom: 0;
}
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-row-item .drag-wrapper {
  min-height: 80px;
  flex: 1;
  display: flex;
  flex-wrap: wrap;
}
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-row-item.active-from-item {
  border: 1px dashed var(--el-color-primary);
  background: var(--builder-card-bg);
}
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-row-item .component-name {
  position: absolute;
  top: 0;
  left: 0;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  display: inline-block;
  padding: 0 6px;
}
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-item:hover:not(.active-from-item) > .el-form-item,
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-row-item:hover:not(.active-from-item) > .el-form-item {
  background: var(--builder-hover-bg);
  border-radius: 6px;
}
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-item:hover > .drawing-item-copy, .form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-item:hover > .drawing-item-delete,
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-row-item:hover > .drawing-item-copy,
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-row-item:hover > .drawing-item-delete {
  display: initial;
}
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-item > .drawing-item-copy, .form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-item > .drawing-item-delete,
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-row-item > .drawing-item-copy,
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-row-item > .drawing-item-delete {
  display: none;
  position: absolute;
  top: -10px;
  width: 22px;
  height: 22px;
  line-height: 22px;
  text-align: center;
  border-radius: 50%;
  font-size: 12px;
  border: 1px solid;
  cursor: pointer;
  z-index: 1;
}
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-item > .drawing-item-copy,
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-row-item > .drawing-item-copy {
  right: 56px;
  border-color: var(--el-color-primary);
  color: var(--el-color-primary);
  background: var(--el-bg-color-overlay);
}
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-item > .drawing-item-copy:hover,
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-row-item > .drawing-item-copy:hover {
  background: var(--el-color-primary);
  color: #fff;
}
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-item > .drawing-item-delete,
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-row-item > .drawing-item-delete {
  right: 24px;
  border-color: var(--el-color-danger);
  color: var(--el-color-danger);
  background: var(--el-bg-color-overlay);
}
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-item > .drawing-item-delete:hover,
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .drawing-row-item > .drawing-item-delete:hover {
  background: var(--el-color-danger);
  color: #fff;
}
.form-builder .center-board .center-scrollbar .center-board-row > .el-form .empty-info {
  position: absolute;
  top: 46%;
  left: 0;
  right: 0;
  text-align: center;
  font-size: 18px;
  color: var(--el-text-color-placeholder);
  letter-spacing: 4px;
}
</style>
