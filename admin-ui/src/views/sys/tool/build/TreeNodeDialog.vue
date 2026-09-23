<template>
  <div>
    <el-dialog
      title="添加选项"
      v-model="open"
      width="800px"
      :close-on-click-modal="false"
      @open="onOpen"
      @close="onClose"
    >
      <el-form
        ref="treeNodeForm"
        :model="formData"
        :rules="rules"
        label-width="100px"
      >
        <el-col :span="24">
          <el-form-item
            label="选项名"
            prop="label"
          >
            <el-input
              v-model="formData.label"
              placeholder="请输入选项名"
              clearable
            />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item
            label="选项值"
            prop="value"
          >
            <el-input
              v-model="formData.value"
              placeholder="请输入选项值"
              clearable
            >
              <template #append>
                <el-select
                  v-model="dataType"
                  :style="{ width: '100px' }"
                >
                  <el-option
                    v-for="(item, index) in dataTypeOptions"
                    :key="index"
                    :label="item.label"
                    :value="item.value"
                  />
                </el-select>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button
            type="primary"
            @click="handelConfirm"
            >确 定</el-button
          >
          <el-button @click="onClose">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { nextTick, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { FieldOption } from '@/utils/generator/types'

const open = defineModel<boolean>({ default: false })
const emit = defineEmits<{ commit: [data: FieldOption] }>()
const formData = ref({ label: '', value: '' })
const dataType = ref<'string' | 'number'>('string')
const dataTypeOptions = [
  { label: '字符串', value: 'string' },
  { label: '数字', value: 'number' },
]
const treeNodeForm = ref<FormInstance>()
const rules: FormRules<typeof formData.value> = {
  label: [
    {
      required: true,
      whitespace: true,
      message: '请输入选项名',
      trigger: 'blur',
    },
  ],
  value: [
    {
      required: true,
      whitespace: true,
      message: '请输入选项值',
      trigger: 'blur',
    },
    {
      validator: (_rule, _value, callback) => {
        const invalid =
          dataType.value === 'number' &&
          !Number.isFinite(Number(formData.value.value))
        callback(invalid ? new Error('请输入有效数字') : undefined)
      },
      trigger: 'blur',
    },
  ],
}
function onOpen(): void {
  formData.value = { label: '', value: '' }
  dataType.value = 'string'
  void nextTick(() => treeNodeForm.value?.clearValidate())
}
function onClose(): void {
  open.value = false
}
async function handelConfirm(): Promise<void> {
  const valid = await treeNodeForm.value?.validate().catch(() => false)
  if (!valid) return
  emit('commit', {
    label: formData.value.label,
    value:
      dataType.value === 'number'
        ? Number(formData.value.value)
        : formData.value.value,
  })
  onClose()
}
</script>
