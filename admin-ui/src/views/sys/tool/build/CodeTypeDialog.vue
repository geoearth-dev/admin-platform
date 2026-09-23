<template>
  <el-dialog
    v-model="open"
    width="500px"
    title="选择生成类型"
    @open="onOpen"
    @close="onClose"
  >
    <el-form
      ref="codeTypeForm"
      :model="formData"
      :rules="rules"
      label-width="100px"
    >
      <el-form-item
        label="生成类型"
        prop="type"
      >
        <el-radio-group v-model="formData.type">
          <el-radio-button
            v-for="(item, index) in typeOptions"
            :key="index"
            :value="item.value"
          >
            {{ item.label }}
          </el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item
        v-if="showFileName"
        label="文件名"
        prop="fileName"
      >
        <el-input
          v-model="formData.fileName"
          placeholder="请输入文件名"
          clearable
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="onClose">取消</el-button>
      <el-button
        type="primary"
        @click="handelConfirm"
        >确定</el-button
      >
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { nextTick, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { GenerateOptions, GenerateType } from '@/utils/generator/types'

const open = defineModel<boolean>({ default: false })
const props = defineProps({
  showFileName: Boolean,
})
const emit = defineEmits<{ confirm: [data: GenerateOptions] }>()
const formData = ref<GenerateOptions>({
  fileName: '',
  type: 'file',
})
const codeTypeForm = ref<FormInstance>()
const rules: FormRules<GenerateOptions> = {
  fileName: [
    {
      required: true,
      message: '请输入文件名',
      trigger: 'blur',
    },
  ],
  type: [
    {
      required: true,
      message: '生成类型不能为空',
      trigger: 'change',
    },
  ],
}
const typeOptions: Array<{ label: string; value: GenerateType }> = [
  {
    label: '页面',
    value: 'file',
  },
  {
    label: '弹窗',
    value: 'dialog',
  },
]
function onOpen(): void {
  void nextTick(() => codeTypeForm.value?.clearValidate())
  if (props.showFileName) {
    formData.value.fileName = `${+new Date()}.vue`
  }
}
function onClose(): void {
  open.value = false
}
function handelConfirm(): void {
  codeTypeForm.value?.validate((valid: boolean) => {
    if (!valid) return
    emit('confirm', { ...formData.value })
    onClose()
  })
}
</script>
