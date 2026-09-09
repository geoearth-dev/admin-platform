import type { Component, SlotsType } from 'vue';

import type {
  BaseFormComponentType,
  ExtendedFormApi,
  FormValues,
  VbenFormComponent,
  VbenFormProps,
  VbenFormSlots,
} from './types';

import { defineComponent, h, isReactive, onBeforeUnmount, watch } from 'vue';

import { useSelector } from '@tanstack/vue-store';

import { FormApi } from './form-api';
import VbenUseForm from './vben-use-form.vue';

type UseVbenFormReturn<
  TValues extends FormValues,
  T extends BaseFormComponentType,
  P extends object,
  TSubmitValues extends FormValues = TValues,
> = readonly [
  VbenFormComponent<TValues, T, P, TSubmitValues>,
  ExtendedFormApi<TValues, T, P, TSubmitValues>,
];

export function useVbenForm<
  T extends BaseFormComponentType = BaseFormComponentType,
  P extends object = Record<never, never>,
>(options: VbenFormProps<T, P>): UseVbenFormReturn<FormValues, T, P>;

export function useVbenForm<
  TValues extends FormValues,
  T extends BaseFormComponentType = BaseFormComponentType,
  P extends object = Record<never, never>,
  TSubmitValues extends FormValues = TValues,
>(
  options: VbenFormProps<T, P, TValues, TSubmitValues>,
): UseVbenFormReturn<TValues, T, P, TSubmitValues>;

export function useVbenForm<
  TValues extends FormValues,
  T extends BaseFormComponentType = BaseFormComponentType,
  P extends object = Record<never, never>,
  TSubmitValues extends FormValues = TValues,
>(
  options: VbenFormProps<T, P, TValues, TSubmitValues>,
): UseVbenFormReturn<TValues, T, P, TSubmitValues> {
  const IS_REACTIVE = isReactive(options);
  const api = new FormApi<TValues, T, P, TSubmitValues>(options);
  const extendedApi: ExtendedFormApi<TValues, T, P, TSubmitValues> = Object.assign(api, {
    useStore<TResult = NoInfer<VbenFormProps<T, P, TValues, TSubmitValues>>>(
      selector?: (state: NoInfer<VbenFormProps<T, P, TValues, TSubmitValues>>) => TResult,
    ) {
      return useSelector(api.store, selector);
    },
  });

  const Form = defineComponent(
    (props: VbenFormProps<T, P, TValues, TSubmitValues>, { attrs, slots }) => {
      onBeforeUnmount(() => {
        api.unmount();
      });
      api.setState({ ...props, ...attrs });
      return () =>
        // 内部 SFC 处理动态 schema；外层组件保留调用方的值与插槽泛型。
        h(VbenUseForm as Component, { ...props, ...attrs, formApi: extendedApi }, slots);
    },
    {
      name: 'VbenUseForm',
      inheritAttrs: false,
      slots: Object as SlotsType<VbenFormSlots<TValues, T, P, TSubmitValues>>,
    },
  );
  // Add reactivity support
  if (IS_REACTIVE) {
    watch(
      () => options.schema,
      () => {
        api.setState({ schema: options.schema });
      },
      { immediate: true },
    );
  }

  // Vue 的组件构造签名要求 props 参数；公开类型只描述模板实例的 props/slots。
  return [Form as unknown as VbenFormComponent<TValues, T, P, TSubmitValues>, extendedApi] as const;
}
