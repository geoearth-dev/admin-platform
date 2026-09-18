<script setup lang="ts">
import { computed } from 'vue';
import type { DictOption } from '@/store/system/admin/dict';

type DictValue = string | number;

interface Props {
  options?: readonly DictOption[] | null;
  value?: DictValue | DictValue[] | null;
  /** 未匹配到字典时，是否显示原值。 */
  showValue?: boolean;
  /** 多个值以字符串传入时的分隔符。 */
  separator?: string;
}

const props = withDefaults(defineProps<Props>(), {
  options: () => [],
  showValue: true,
  separator: ',',
});

/** 统一为字符串并去重，数字 0 与字符串 '0' 可以匹配。 */
const valueSet = computed(() => {
  const value = props.value;

  if (value === null || value === undefined || value === '') {
    return new Set<string>();
  }

  const list = Array.isArray(value)
    ? value
    : typeof value === 'string' && props.separator
      ? value.split(props.separator)
      : [value];

  return new Set(list.map(String));
});

/** 保持字典选项原有的排列顺序。 */
const matchedOptions = computed(() =>
  (props.options ?? []).filter((item) => valueSet.value.has(String(item.value))),
);

/** 未匹配的值直接作为文字显示。 */
const unmatchedText = computed(() => {
  const matchedValues = new Set(matchedOptions.value.map((item) => String(item.value)));

  return [...valueSet.value].filter((value) => !matchedValues.has(value)).join(' ');
});
</script>

<template>
  <span class="inline-flex flex-wrap items-center gap-1 align-middle">
    <template v-for="item in matchedOptions" :key="item.value">
      <!-- 没有配置标签样式时，显示普通文字。 -->
      <span v-if="!item.elTagType && !item.elTagClass">
        {{ item.label }}
      </span>

      <el-tag v-else :type="item.elTagType" :class="item.elTagClass" disable-transitions>
        {{ item.label }}
      </el-tag>
    </template>

    <span v-if="showValue && unmatchedText">
      {{ unmatchedText }}
    </span>
  </span>
</template>
