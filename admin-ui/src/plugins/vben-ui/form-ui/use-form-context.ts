import type { ZodType } from 'zod';

import type { ComputedRef } from 'vue';

import type { ExtendedFormApi, FormActions, FormValues, VbenFormProps } from './types';

import { computed, toRaw, unref, useSlots } from 'vue';

import { createContext } from '@/plugins/vben-ui/shadcn-ui';
import { cloneDeep, isString } from '@/utils/inference';

import { object, ZodNumber, ZodObject, ZodString } from 'zod';
import { getDefaultsForSchema } from 'zod-defaults';

import { useFormRuntime } from './form-runtime';
import { setValueByFieldName } from './field-name';

type ExtendFormProps = VbenFormProps & {
  formApi?: ExtendedFormApi;
};

export const [injectFormProps, provideFormProps] =
  createContext<[ComputedRef<ExtendFormProps> | ExtendFormProps, FormActions]>(
    'VbenFormProps',
  );

export const [injectComponentRefMap, provideComponentRefMap] =
  createContext<Map<string, unknown>>('ComponentRefMap');

export function useFormInitial(
  props: ComputedRef<VbenFormProps> | VbenFormProps,
) {
  const slots = useSlots();
  const initialValues = generateInitialValues();

  const form = useFormRuntime(initialValues);

  const delegatedSlots = computed(() => {
    const resultSlots: string[] = [];

    for (const key of Object.keys(slots)) {
      if (key !== 'default') {
        resultSlots.push(key);
      }
    }
    return resultSlots;
  });

  function generateInitialValues() {
    const initialValues: FormValues = {};

    (unref(props).schema || []).forEach((item) => {
      if (Reflect.has(item, 'defaultValue')) {
        // 显式默认值优先，包括 null/undefined；避免与 schema 共享可变对象。
        setValueByFieldName(initialValues, item.fieldName, cloneDeep(item.defaultValue));
      } else if (item.rules && !isString(item.rules)) {
        const defaultValue = getCustomDefaultValue(item.rules);
        if (defaultValue !== undefined) {
          setValueByFieldName(initialValues, item.fieldName, cloneDeep(defaultValue));
        }
      }
    });

    return initialValues;
  }
  // 自定义默认值提取逻辑
  function getCustomDefaultValue(rule: ZodType): unknown {
    rule = toRaw(rule);
    if (rule instanceof ZodString) {
      return ''; // 默认为空字符串
    } else if (rule instanceof ZodNumber) {
      return null; // 默认为 null（避免显示 0）
    } else if (rule instanceof ZodObject) {
      // 递归提取嵌套对象的默认值
      const defaultValues: FormValues = {};
      const shape: Record<string, ZodType> = rule.shape;
      for (const [key, valueSchema] of Object.entries(shape)) {
        const value = getCustomDefaultValue(valueSchema);
        if (value !== undefined) {
          defaultValues[key] = value;
        }
      }
      return defaultValues;
    } else {
      // 保留 Zod 默认值、数组、可选值、交集等类型原有的提取规则。
      return getDefaultsForSchema(object({ value: rule })).value;
    }
  }

  return {
    delegatedSlots,
    form,
  };
}
