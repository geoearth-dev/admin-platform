<script lang="ts" setup>
import { ElInput, ElSelectV2 } from 'element-plus'
type TwoFieldsValue = [string | undefined, string | undefined]

const emit = defineEmits<{
  blur: []
  change: [value: TwoFieldsValue]
}>()

const modelValue = defineModel<TwoFieldsValue>({
  default: () => [undefined, undefined],
})

function updateValue(index: 0 | 1, value: unknown) {
  // 替换整个数组才能触发 update:modelValue，避免直接修改父表单中的数组。
  const next: TwoFieldsValue = [...modelValue.value]
  next[index] = typeof value === 'string' ? value : undefined
  modelValue.value = next
  emit('change', next)
}
</script>
<template>
  <div class="flex w-full gap-1">
    <ElSelectV2
      :model-value="modelValue[0]"
      class="w-20"
      placeholder="类型"
      clearable
      :class="{ 'valid-success': !!modelValue[0] }"
      :options="[
        { label: '个人', value: 'personal' },
        { label: '工作', value: 'work' },
        { label: '私密', value: 'private' },
      ]"
      @blur="emit('blur')"
      @update:model-value="updateValue(0, $event)"
    />
    <ElInput
      placeholder="请输入11位手机号码"
      class="flex-1"
      clearable
      :class="{ 'valid-success': /^1[3-9]\d{9}$/.test(modelValue[1] ?? '') }"
      :model-value="modelValue[1]"
      :maxlength="11"
      type="tel"
      @blur="emit('blur')"
      @update:model-value="updateValue(1, $event)"
    />
  </div>
</template>
