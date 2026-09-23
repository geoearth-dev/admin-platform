<template>
  <div class="icon-dialog">
    <el-dialog
      v-model="value"
      width="980px"
      :close-on-click-modal="false"
      @open="onOpen"
    >
      <template #header>
        选择图标
        <el-input
          v-model="key"
          size="small"
          :style="{ width: '260px' }"
          placeholder="请输入图标名称"
          :prefix-icon="ElementPlusIconsVue.Search"
          clearable
        />
      </template>
      <ul class="icon-ul">
        <li
          v-for="[icon, component] in iconList"
          :key="icon"
          :class="active === icon ? 'active-item' : ''"
          @click="onSelect(icon)"
        >
          <div>
            <el-icon :size="30">
              <component :is="component" />
            </el-icon>
            <div>{{ icon }}</div>
          </div>
        </li>
      </ul>
    </el-dialog>
  </div>
</template>
<script setup lang="ts">
import { computed, ref } from 'vue'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const props = defineProps<{ current?: string }>()
const emit = defineEmits<{ select: [icon: string] }>()
const value = defineModel<boolean>({ default: false })
const key = ref('')
const active = ref('')
const originList = Object.entries(ElementPlusIconsVue)
const iconList = computed(() =>
  originList.filter(([name]) =>
    name.toLowerCase().includes(key.value.toLowerCase()),
  ),
)

function onOpen(): void {
  key.value = ''
  active.value = props.current ?? ''
}
function onSelect(icon: string): void {
  active.value = icon
  emit('select', icon)
  value.value = false
}
</script>
<style scoped>
.icon-ul {
  margin: 0;
  padding: 0;
  font-size: 0;
}
.icon-ul li {
  list-style-type: none;
  text-align: center;
  font-size: 14px;
  display: inline-flex;
  width: 16.66%;
  box-sizing: border-box;
  height: 108px;
  padding: 6px 6px 6px 6px;
  cursor: pointer;
  overflow: hidden;
  align-items: center;
  justify-content: center;
}
.icon-ul li:hover {
  background: var(--el-fill-color-light);
}
.icon-ul li.active-item {
  background: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
}
.icon-ul li i {
  font-size: 30px;
  line-height: 50px;
  margin-bottom: 10px;
}

.icon-dialog :deep(.el-dialog) {
  border-radius: 8px;
  margin-bottom: 0;
  margin-top: 4vh !important;
  display: flex;
  flex-direction: column;
  max-height: 92vh;
  overflow: hidden;
  box-sizing: border-box;
}
.icon-dialog :deep(.el-dialog .el-dialog__header) {
  padding-top: 14px;
}
.icon-dialog :deep(.el-dialog .el-dialog__body) {
  margin: 0 20px 20px 20px;
  padding: 0;
  overflow: auto;
}
</style>
