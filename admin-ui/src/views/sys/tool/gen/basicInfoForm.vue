<template>
  <el-form
    ref="basicInfoForm"
    :model="info"
    :rules="rules"
    label-width="150px"
  >
    <el-row>
      <el-col :span="12">
        <el-form-item
          label="表名称"
          prop="tableName"
        >
          <el-input
            placeholder="请输入仓库名称"
            v-model="info.tableName"
          />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item
          label="表描述"
          prop="tableComment"
        >
          <el-input
            placeholder="请输入"
            v-model="info.tableComment"
          />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item
          label="实体类名称"
          prop="className"
        >
          <el-input
            placeholder="请输入"
            v-model="info.className"
          />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item
          label="作者"
          prop="functionAuthor"
        >
          <el-input
            placeholder="请输入"
            v-model="info.functionAuthor"
          />
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item
          label="备注"
          prop="remark"
        >
          <el-input
            type="textarea"
            :rows="3"
            v-model="info.remark"
          ></el-input>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { GenTable } from '@/types/base/api/tool/gen'
const props = defineProps<{ info: GenTable }>()
const info = computed(() => props.info)
const basicInfoForm = ref<FormInstance>()
const rules: FormRules<GenTable> = {
  tableName: [{ required: true, message: '请输入表名称', trigger: 'blur' }],
  tableComment: [{ required: true, message: '请输入表描述', trigger: 'blur' }],
  className: [{ required: true, message: '请输入实体类名称', trigger: 'blur' }],
  functionAuthor: [{ required: true, message: '请输入作者', trigger: 'blur' }],
}
async function validate(): Promise<boolean> {
  return (await basicInfoForm.value?.validate().catch(() => false)) ?? false
}
defineExpose({ validate })
</script>
