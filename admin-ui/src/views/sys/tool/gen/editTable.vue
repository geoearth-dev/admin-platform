<template>
  <el-card v-loading="loading">
    <el-tabs
      v-if="info"
      v-model="activeName"
    >
      <el-tab-pane
        label="基本信息"
        name="basic"
      >
        <basic-info-form
          ref="basicInfo"
          :info="info"
        />
      </el-tab-pane>
      <el-tab-pane
        label="字段信息"
        name="columnInfo"
      >
        <el-table
          ref="dragTable"
          :data="columns"
          row-key="id"
          :max-height="tableHeight"
        >
          <Column
            label="序号"
            type="index"
            min-width="5%"
            class-name="allowDrag"
          />
          <Column
            label="字段列名"
            prop="columnName"
            min-width="10%"
            :show-overflow-tooltip="true"
            class-name="allowDrag"
          />
          <Column
            label="字段描述"
            min-width="10%"
          >
            <template #default="scope: { row: GenTableColumn }">
              <el-input v-model="scope.row.columnComment"></el-input>
            </template>
          </Column>
          <Column
            label="物理类型"
            prop="columnType"
            min-width="10%"
            :show-overflow-tooltip="true"
          />
          <Column
            label="Java类型"
            min-width="11%"
          >
            <template #default="scope: { row: GenTableColumn }">
              <el-select v-model="scope.row.javaType">
                <el-option
                  label="Long"
                  value="Long"
                />
                <el-option
                  label="String"
                  value="String"
                />
                <el-option
                  label="Integer"
                  value="Integer"
                />
                <el-option
                  label="Double"
                  value="Double"
                />
                <el-option
                  label="BigDecimal"
                  value="BigDecimal"
                />
                <el-option
                  label="Instant"
                  value="Instant"
                />
                <el-option
                  label="LocalDate"
                  value="LocalDate"
                />
                <el-option
                  label="LocalTime"
                  value="LocalTime"
                />
                <el-option
                  label="LocalDateTime"
                  value="LocalDateTime"
                />
                <el-option
                  label="Date（兼容已有配置）"
                  value="Date"
                />
                <el-option
                  label="Boolean"
                  value="Boolean"
                />
              </el-select>
            </template>
          </Column>
          <Column
            label="java属性"
            min-width="10%"
          >
            <template #default="scope: { row: GenTableColumn }">
              <el-input v-model="scope.row.javaField"></el-input>
            </template>
          </Column>

          <Column
            label="插入"
            min-width="5%"
          >
            <template #default="scope: { row: GenTableColumn }">
              <el-checkbox
                true-value="1"
                false-value="0"
                v-model="scope.row.isInsert"
              ></el-checkbox>
            </template>
          </Column>
          <Column
            label="编辑"
            min-width="5%"
          >
            <template #default="scope: { row: GenTableColumn }">
              <el-checkbox
                true-value="1"
                false-value="0"
                v-model="scope.row.isEdit"
              ></el-checkbox>
            </template>
          </Column>
          <Column
            label="列表"
            min-width="5%"
          >
            <template #default="scope: { row: GenTableColumn }">
              <el-checkbox
                true-value="1"
                false-value="0"
                v-model="scope.row.isList"
              ></el-checkbox>
            </template>
          </Column>
          <Column
            label="查询"
            min-width="5%"
          >
            <template #default="scope: { row: GenTableColumn }">
              <el-checkbox
                true-value="1"
                false-value="0"
                v-model="scope.row.isQuery"
              ></el-checkbox>
            </template>
          </Column>
          <Column
            label="查询方式"
            min-width="10%"
          >
            <template #default="scope: { row: GenTableColumn }">
              <el-select v-model="scope.row.queryType">
                <el-option
                  label="="
                  value="EQ"
                />
                <el-option
                  label="!="
                  value="NE"
                />
                <el-option
                  label=">"
                  value="GT"
                />
                <el-option
                  label=">="
                  value="GTE"
                />
                <el-option
                  label="<"
                  value="LT"
                />
                <el-option
                  label="<="
                  value="LTE"
                />
                <el-option
                  label="LIKE"
                  value="LIKE"
                />
                <el-option
                  label="BETWEEN"
                  value="BETWEEN"
                />
              </el-select>
            </template>
          </Column>
          <Column
            label="必填"
            min-width="5%"
          >
            <template #default="scope: { row: GenTableColumn }">
              <el-checkbox
                true-value="1"
                false-value="0"
                v-model="scope.row.isRequired"
              ></el-checkbox>
            </template>
          </Column>
          <Column
            label="显示类型"
            min-width="12%"
          >
            <template #default="scope: { row: GenTableColumn }">
              <el-select v-model="scope.row.htmlType">
                <el-option
                  label="文本框"
                  value="input"
                />
                <el-option
                  label="文本域"
                  value="textarea"
                />
                <el-option
                  label="下拉框"
                  value="select"
                />
                <el-option
                  label="单选框"
                  value="radio"
                />
                <el-option
                  label="复选框"
                  value="checkbox"
                />
                <el-option
                  label="日期控件"
                  value="datetime"
                />
                <el-option
                  label="图片上传"
                  value="imageUpload"
                />
                <el-option
                  label="文件上传"
                  value="fileUpload"
                />
                <el-option
                  label="富文本控件"
                  value="editor"
                />
              </el-select>
            </template>
          </Column>
          <Column
            label="字典类型"
            min-width="12%"
          >
            <template #default="scope: { row: GenTableColumn }">
              <el-select
                v-model="scope.row.dictType"
                clearable
                filterable
                placeholder="请选择"
              >
                <el-option
                  v-for="dict in dictOptions"
                  :key="dict.dictType"
                  :label="dict.dictName"
                  :value="dict.dictType"
                >
                  <span style="float: left">{{ dict.dictName }}</span>
                  <span style="float: right; color: #8492a6; font-size: 13px">{{
                    dict.dictType
                  }}</span>
                </el-option>
              </el-select>
            </template>
          </Column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane
        label="生成信息"
        name="genInfo"
      >
        <gen-info-form
          ref="genInfo"
          :info="info"
          :tables="tables"
        />
      </el-tab-pane>
    </el-tabs>
    <el-form label-width="100px">
      <div style="text-align: center; margin-left: -100px; margin-top: 10px">
        <el-button
          type="primary"
          :loading="saving"
          :disabled="!info"
          @click="submitForm()"
          >提交</el-button
        >
        <el-button @click="close()">返回</el-button>
      </div>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { ElTableColumn } from 'element-plus'
const Column = ElTableColumn<GenTableColumn>
import { nextTick, onBeforeUnmount, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { TableInstance } from 'element-plus'
import Sortable from 'sortablejs'
import { getGenTable, updateGenTable } from '@/api/tool/gen'
import { optionselect } from '@/api/system/dict/type'
import type { GenTable, GenTableColumn } from '@/types/base/api/tool/gen'
import type { SysDictType } from '@/types/base/api/system/dict'
import BasicInfoForm from './basicInfoForm.vue'
import GenInfoForm from './genInfoForm.vue'
import { useTabs } from '@/plugins/effects/hooks/use-tabs'
defineOptions({ name: 'GenEdit' })
const route = useRoute(),
  router = useRouter()
const { closeCurrentTab } = useTabs()
const activeName = ref('columnInfo')
const tableHeight = 'calc(100vh - 310px)'
const loading = ref(false),
  saving = ref(false)
const tables = ref<GenTable[]>([]),
  columns = ref<GenTableColumn[]>([]),
  dictOptions = ref<SysDictType[]>([])
const info = ref<GenTable | null>(null)
const basicInfo = ref<InstanceType<typeof BasicInfoForm>>(),
  genInfo = ref<InstanceType<typeof GenInfoForm>>()
const dragTable = ref<TableInstance>()
let sortable: Sortable | undefined
let requestId = 0
function bindSort(): void {
  sortable?.destroy()
  const body = dragTable.value?.$el.querySelector('tbody')
  if (!(body instanceof HTMLElement)) return
  sortable = Sortable.create(body, {
    handle: '.allowDrag',
    onEnd: ({ oldIndex, newIndex }) => {
      if (
        oldIndex === undefined ||
        newIndex === undefined ||
        oldIndex === newIndex
      )
        return
      const [item] = columns.value.splice(oldIndex, 1)
      if (!item) return
      columns.value.splice(newIndex, 0, item)
      columns.value.forEach((column, index) => {
        column.sort = index + 1
      })
    },
  })
}
async function submitForm(): Promise<void> {
  if (!info.value) return
  const valid = await Promise.all([
    basicInfo.value?.validate(),
    genInfo.value?.validate(),
  ])
  if (!valid.every(Boolean)) {
    ElMessage.warning('请检查基本信息和生成信息')
    return
  }
  saving.value = true
  try {
    const value = info.value
    await updateGenTable({
      ...value,
      columns: columns.value,
      params: {
        genView: value.view,
        treeCode: value.treeCode,
        treeName: value.treeName,
        treeParentCode: value.treeParentCode,
        parentMenuId: value.parentMenuId,
      },
    })
    ElMessage.success('保存成功')
    await close()
  } finally {
    saving.value = false
  }
}
async function close(): Promise<void> {
  await closeCurrentTab()
  await router.push({ path: '/tool/gen', query: { t: Date.now() } })
}
watch(
  () => route.params.id,
  async (value) => {
    const id = Number(value)
    if (!Number.isSafeInteger(id) || id <= 0) return
    const current = ++requestId
    loading.value = true
    info.value = null
    sortable?.destroy()
    try {
      const [result, dicts] = await Promise.all([
        getGenTable(id),
        optionselect(),
      ])
      if (current !== requestId) return
      columns.value = result.rows
      info.value = { ...result.info, columns: columns.value }
      tables.value = result.tables
      dictOptions.value = dicts
      await nextTick()
      bindSort()
    } finally {
      if (current === requestId) loading.value = false
    }
  },
  { immediate: true },
)
watch(activeName, async () => {
  await nextTick()
  bindSort()
})
onBeforeUnmount(() => {
  requestId++
  sortable?.destroy()
})
</script>
