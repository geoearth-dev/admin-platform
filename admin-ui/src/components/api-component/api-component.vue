<script lang="ts" setup>
import type { ComponentPublicInstance } from 'vue';
import type {
  ApiComponentDataItem,
  ApiComponentExpose,
  ApiComponentParams,
  ApiComponentProps,
  ApiComponentOptionsItem as OptionsItem,
} from './types';

import { computed, nextTick, ref, shallowRef, unref, useAttrs, watch } from 'vue';

import { LoaderCircle } from '@/assets/icons';

import { objectOmit } from '@vueuse/core';
import { cloneDeep, get, isEqual } from '@/utils/inference';

defineOptions({ name: 'ApiComponent', inheritAttrs: false });

const props = withDefaults(defineProps<ApiComponentProps>(), {
  labelField: 'label',
  valueField: 'value',
  labelFn: undefined,
  disabledField: 'disabled',
  childrenField: '',
  optionsPropName: 'options',
  resultField: '',
  visibleEvent: '',
  numberToString: false,
  params: () => ({}),
  immediate: true,
  alwaysLoad: false,
  loadingSlot: '',
  beforeFetch: undefined,
  shouldFetch: undefined,
  afterFetch: undefined,
  modelPropName: 'modelValue',
  api: undefined,
  autoSelect: false,
  options: () => [],
});

const emit = defineEmits<{
  optionsChange: [OptionsItem[]];
}>();

// 通用包装层原样透传控件值，兼容单选、多选及对象形式的值。
const modelValue = defineModel<unknown>();

const attrs = useAttrs();
const innerParams = ref<ApiComponentParams>({});
const refOptions = ref<ApiComponentDataItem[]>([]);
const loading = ref(false);
// 首次是否加载过了
const isFirstLoaded = ref(false);
// 标记是否有待处理的请求
const hasPendingRequest = ref(false);

function isDataItem(value: unknown): value is ApiComponentDataItem {
  return typeof value === 'object' && value !== null && !Array.isArray(value);
}

/** 接口及嵌套字段的结果只有经过检查，才能作为选项数组使用。 */
function readOptions(value: unknown): ApiComponentDataItem[] {
  if (value === undefined || value === null) {
    return [];
  }
  if (!Array.isArray(value) || !value.every(isDataItem)) {
    throw new TypeError('[ApiComponent] 选项数据必须是对象数组，请检查接口返回值和字段映射。');
  }
  return value;
}

const getOptions = computed(() => {
  const { labelField, labelFn, valueField, disabledField, childrenField, numberToString } = props;

  function transformData(data: ApiComponentDataItem[] = []): OptionsItem[] {
    return data.map((item) => {
      const rawValue: unknown = get(item, valueField);
      const value =
        typeof rawValue === 'string' || typeof rawValue === 'number' ? rawValue : undefined;
      const label: unknown = labelFn ? labelFn(item) : get(item, labelField);
      const disabled: unknown = get(item, disabledField);
      const children: unknown = childrenField ? get(item, childrenField) : item.children;
      // 原始 children 不能作为已转换的子选项直接透传。
      const remainingProps = objectOmit(item, [
        labelField,
        valueField,
        disabledField,
        'children',
        ...(childrenField ? [childrenField] : []),
      ]);
      return {
        ...remainingProps,
        label: typeof label === 'string' || typeof label === 'number' ? String(label) : undefined,
        value: numberToString && value !== undefined ? String(value) : value,
        disabled: typeof disabled === 'boolean' ? disabled : undefined,
        ...(Array.isArray(children) && children.length > 0
          ? { children: transformData(readOptions(children)) }
          : {}),
      };
    });
  }

  const data = transformData(unref(refOptions));

  return data.length > 0 ? data : transformData(readOptions(props.options));
});

const bindProps = computed(() => {
  return {
    [props.modelPropName]: unref(modelValue),
    [props.optionsPropName]: unref(getOptions),
    [`onUpdate:${props.modelPropName}`]: (val: unknown) => {
      modelValue.value = val;
    },
    ...objectOmit(attrs, [`onUpdate:${props.modelPropName}`]),
    ...(props.visibleEvent
      ? {
          [props.visibleEvent]: handleFetchForVisible,
        }
      : {}),
  };
});

async function fetchApi() {
  const { api, beforeFetch, shouldFetch, afterFetch, resultField } = props;

  if (!api) {
    return;
  }

  // 如果正在加载，标记有待处理的请求并返回
  if (loading.value) {
    hasPendingRequest.value = true;
    return;
  }

  refOptions.value = [];
  try {
    loading.value = true;
    let finalParams = unref(mergedParams);
    if (beforeFetch) {
      finalParams = (await beforeFetch(cloneDeep(finalParams))) || finalParams;
    }
    // 判断是否需要控制执行中断
    if (shouldFetch && !(await shouldFetch(finalParams))) {
      return;
    }
    let res = await api(finalParams);
    if (afterFetch) {
      res = (await afterFetch(res)) || res;
    }
    isFirstLoaded.value = true;
    if (Array.isArray(res)) {
      refOptions.value = readOptions(res);
      emitChange();
      return;
    }
    if (resultField) {
      const options: unknown = isDataItem(res) ? get(res, resultField) : undefined;
      refOptions.value = readOptions(options);
    }
    emitChange();
  } catch (error) {
    console.warn(error);
    // reset status
    isFirstLoaded.value = false;
  } finally {
    loading.value = false;
    // 如果有待处理的请求，立即触发新的请求
    if (hasPendingRequest.value) {
      hasPendingRequest.value = false;
      // 使用 nextTick 确保状态更新完成后再触发新请求
      await nextTick();
      fetchApi();
    }
  }
}

async function handleFetchForVisible(visible: boolean) {
  if (visible) {
    if (props.alwaysLoad) {
      await fetchApi();
    } else if (!props.immediate && !unref(isFirstLoaded)) {
      await fetchApi();
    }
  }
}

const mergedParams = computed(() => {
  return {
    ...props.params,
    ...unref(innerParams),
  };
});

watch(
  mergedParams,
  (value, oldValue) => {
    if (isEqual(value, oldValue)) {
      return;
    }
    fetchApi();
  },
  { deep: true, immediate: props.immediate },
);

function emitChange() {
  if (modelValue.value === undefined && props.autoSelect && unref(getOptions).length > 0) {
    let firstOption;
    if (typeof props.autoSelect === 'function') {
      firstOption = props.autoSelect(unref(getOptions));
    } else {
      switch (props.autoSelect) {
        case 'first': {
          firstOption = unref(getOptions)[0];
          break;
        }
        case 'last': {
          firstOption = unref(getOptions)[unref(getOptions).length - 1];
          break;
        }
        case 'one': {
          if (unref(getOptions).length === 1) {
            firstOption = unref(getOptions)[0];
          }
          break;
        }
      }
    }

    if (firstOption) modelValue.value = firstOption.value;
  }
  emit('optionsChange', unref(getOptions));
}
const componentRef = shallowRef<ComponentPublicInstance | null>(null);
defineExpose<ApiComponentExpose>({
  /** 获取options数据 */
  getOptions: () => unref(getOptions),
  /** 获取当前值 */
  getValue: () => unref(modelValue),
  /** 获取被包装的组件实例 */
  getComponentRef: () => componentRef.value,
  /** 更新Api参数 */
  updateParam(newParams: ApiComponentParams) {
    innerParams.value = newParams;
  },
});
</script>
<template>
  <component
    :is="component"
    v-bind="bindProps"
    :placeholder="$attrs.placeholder"
    ref="componentRef"
  >
    <template v-for="item in Object.keys($slots)" #[item]="data">
      <slot :name="item" v-bind="data || {}"></slot>
    </template>
    <template v-if="loadingSlot && loading" #[loadingSlot]>
      <LoaderCircle class="animate-spin" />
    </template>
  </component>
</template>
