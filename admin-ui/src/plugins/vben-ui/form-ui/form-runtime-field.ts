import type { Component } from 'vue';

import { defineComponent, h, markRaw, onUnmounted } from 'vue';

import { isObject } from '@/utils/inference';

export type FieldValidationInvalidator = () => void;

const asyncValidatorKeys = [
  'onBlurAsync',
  'onChangeAsync',
  'onDynamicAsync',
  'onSubmitAsync',
] as const;

export function createRuntimeFieldComponent(
  fieldComponent: Component,
  registerInvalidator: (
    fieldName: string,
    invalidator: FieldValidationInvalidator,
  ) => () => void,
) {
  return markRaw(
    defineComponent({
      inheritAttrs: false,
      setup(_, { attrs, slots }) {
        const fieldName = String(attrs.name ?? '');
        let validationRunId = 0;
        let cachedValidators: Record<string, unknown> | undefined;
        let cachedWrappedValidators: Record<string, unknown> | undefined;
        const unregisterInvalidator = registerInvalidator(fieldName, () => {
          validationRunId += 1;
        });
        onUnmounted(unregisterInvalidator);

        function wrapValidators(validators: Record<string, unknown>) {
          if (validators === cachedValidators && cachedWrappedValidators) {
            return cachedWrappedValidators;
          }
          const wrappedValidators = { ...validators };
          for (const key of asyncValidatorKeys) {
            const validator = validators[key];
            // Standard Schema 校验器是对象，交给 TanStack 自行处理。
            if (typeof validator !== 'function') {
              continue;
            }
            wrappedValidators[key] = async (...args: unknown[]) => {
              const currentValidationRunId = ++validationRunId;
              const result: unknown = await validator(...args);
              return currentValidationRunId === validationRunId
                ? result
                : undefined;
            };
          }
          cachedValidators = validators;
          cachedWrappedValidators = wrappedValidators;
          return wrappedValidators;
        }

        return () => {
          const validators = attrs.validators;
          return h(
            fieldComponent,
            {
              ...attrs,
              ...(isObject(validators) ? { validators: wrapValidators(validators) } : {}),
            },
            slots,
          );
        };
      },
    }),
  );
}
