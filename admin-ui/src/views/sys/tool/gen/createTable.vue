<template>
  <!-- 创建表 -->
  <el-dialog
    title="创建表"
    v-model="visible"
    width="800px"
    top="5vh"
    append-to-body
  >
    <span>创建表语句(支持多个建表语句)：</span>
    <el-input
      type="textarea"
      :rows="10"
      placeholder="请输入文本"
      v-model="content"
    ></el-input>
    <template #footer>
      <div class="dialog-footer">
        <el-button
          type="primary"
          :loading="saving"
          @click="handleImportTable"
          >确 定</el-button
        >
        <el-button @click="visible = false">取 消</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { createTable } from '@/api/tool/gen'
const visible = ref(false),
  saving = ref(false),
  content = ref('')
const emit = defineEmits<{ ok: [] }>()
function show(): void {
  content.value = ''
  visible.value = true
}
async function handleImportTable(): Promise<void> {
  if (!content.value.trim()) {
    ElMessage.warning('请输入建表语句')
    return
  }
  saving.value = true
  try {
    await createTable(content.value)
    ElMessage.success('创建并导入成功')
    visible.value = false
    emit('ok')
  } finally {
    saving.value = false
  }
}
defineExpose({ show })
</script>
