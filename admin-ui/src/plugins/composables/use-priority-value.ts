import { getFirstNonNullOrUndefined } from '@/utils/inference';
import { kebabToCamelCase } from '@/utils/letter';
import type { ComputedRef, Ref } from 'vue';

import { computed, getCurrentInstance, useAttrs, useSlots } from 'vue';

/**
 * 依次从插槽、attrs、props、state 中获取值
 * @param key
 * @param props
 * @param state
 */
export function usePriorityValue<T extends object, S extends object, K extends keyof T = keyof T>(
  key: K,
  props: T,
  state: Readonly<Ref<NoInfer<S>>> | undefined,
) {
  const instance = getCurrentInstance();
  const slots = useSlots();
  const attrs = useAttrs();

  const value = computed((): T[K] => {
    // props不管有没有传，都会有默认值，会影响这里的顺序，
    // 通过判断原始props是否有值来判断是否传入
    const rawProps = instance?.vnode?.props || {};

    const standardRawProps: Record<string, unknown> = {};

    for (const [key, value] of Object.entries(rawProps)) {
      standardRawProps[kebabToCamelCase(key)] = value;
    }
    const propsKey = standardRawProps[key as string] === undefined ? undefined : props[key];
    // state 与 props 的字段不必完全相同，按动态键读取时保留 unknown 类型。
    const stateValues = state?.value as Record<PropertyKey, unknown> | undefined;

    // slot可以关闭
    return getFirstNonNullOrUndefined<unknown>(
      slots[key as string],
      attrs[key as string],
      propsKey,
      stateValues?.[key],
    ) as T[K];
  });

  return value;
}

/**
 * 批量获取state中的值（每个值都是ref）
 * @param props
 * @param state
 */
export function usePriorityValues<
  T extends object,
  S extends Readonly<Ref<object>> = Readonly<Ref<NoInfer<T>>>,
>(props: T, state: S | undefined) {
  const result = {} as {
    [K in keyof T]: ComputedRef<T[K]>;
  };

  const keys = Object.keys(props) as Array<keyof T>;

  keys.forEach(<K extends keyof T>(key: K) => {
    result[key] = usePriorityValue(key, props, state);
  });

  return result;
}
/**
 * 批量获取优先级处理后的值，用于透传
 */
export function useForwardPriorityValues<
  T extends object,
  S extends Readonly<Ref<object>> = Readonly<Ref<NoInfer<T>>>,
>(props: T, state: S | undefined) {
  const computedResult = {} as {
    [K in keyof T]: ComputedRef<T[K]>;
  };

  const keys = Object.keys(props) as Array<keyof T>;

  // 用泛型保留每个 key 与对应属性类型的关系
  keys.forEach(<K extends keyof T>(key: K) => {
    computedResult[key] = usePriorityValue(key, props, state);
  });

  return computed(() => {
    const unwrapResult: Record<string, unknown> = {};

    for (const key of keys) {
      unwrapResult[key as string] = computedResult[key].value;
    }

    return unwrapResult as T;
  });
}
