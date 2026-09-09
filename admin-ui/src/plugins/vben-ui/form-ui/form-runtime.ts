import type { FieldValidationInvalidator } from './form-runtime-field';
import type {
  FormActions,
  FormFieldName,
  FormFieldValue,
  FormResetOptions,
  FormRuntimeState,
  FormValues,
} from './types';

import { computed, shallowRef } from 'vue';

import { cloneDeep, mergeWith } from 'es-toolkit/compat';

import { batch } from '@tanstack/vue-store';
import { functionalUpdate, useForm, type DeepValue } from '@tanstack/vue-form';

import { createRuntimeFieldComponent } from './form-runtime-field';
import { resolveFieldNamePath } from './field-name';

function normalizeError(error: unknown): string | undefined {
  if (typeof error === 'string') {
    return error;
  }
  if (error && typeof error === 'object' && 'message' in error) {
    const message = Reflect.get(error, 'message');
    return typeof message === 'string' ? message : undefined;
  }
  return error === undefined || error === null ? undefined : String(error);
}

function normalizeFieldMetaError(meta: unknown) {
  if (!meta || typeof meta !== 'object' || !('errors' in meta)) {
    return undefined;
  }
  const errors = Reflect.get(meta, 'errors');
  return normalizeError(Array.isArray(errors) ? errors[0] : undefined);
}

export function useFormRuntime<TValues extends FormValues>(
  defaultValues: TValues,
): FormActions<TValues> {
  const rawForm = useForm({
    defaultValues,
    onSubmit: () => {},
  });

  // Vben 用 [profile.name] 表示字面量键，TanStack 默认会将它拆成嵌套路径。
  // 仅适配这类键的值读写，字段名仍保留原样，避免与 profile.name 的校验状态混淆。
  const getRuntimeFieldValue = rawForm.getFieldValue;
  const setRuntimeFieldValue = rawForm.setFieldValue;
  rawForm.getFieldValue = (field) => {
    const { rawKey } = resolveFieldNamePath(field);
    return rawKey
      ? (rawForm.state.values[rawKey] as DeepValue<TValues, typeof field>)
      : getRuntimeFieldValue(field);
  };
  rawForm.setFieldValue = (field, updater, options) => {
    const { rawKey } = resolveFieldNamePath(field);
    if (!rawKey) {
      setRuntimeFieldValue(field, updater, options);
      return;
    }
    batch(() => {
      if (!options?.dontUpdateMeta) {
        rawForm.setFieldMeta(field, (previous) => ({
          ...previous,
          isTouched: true,
          isDirty: true,
          errorMap: { ...previous?.errorMap, onMount: undefined },
        }));
      }
      rawForm.baseStore.setState((previous) => ({
        ...previous,
        values: {
          ...previous.values,
          [rawKey]: functionalUpdate(updater, rawForm.getFieldValue(field)),
        },
      }));
    });
    if (!options?.dontRunListeners) {
      rawForm.getFieldInfo(field).instance?.triggerOnChangeListener();
    }
    if (!options?.dontValidate) {
      void rawForm.validateField(field, 'change');
    }
  };
  const values = rawForm.useSelector((formState) => formState.values);
  const fieldMeta = rawForm.useSelector((formState) => formState.fieldMeta);
  const isDirty = rawForm.useSelector((formState) => formState.isDirty);
  const isSubmitting = rawForm.useSelector(
    (formState) => formState.isSubmitting,
  );
  const isValid = rawForm.useSelector((formState) => formState.isValid);
  const isValidating = rawForm.useSelector(
    (formState) => formState.isValidating,
  );
  const manualErrors = shallowRef(new Map<string, string>());
  const validationInvalidators = new Map<
    string,
    Set<FieldValidationInvalidator>
  >();

  function registerValidationInvalidator(
    fieldName: string,
    invalidator: FieldValidationInvalidator,
  ) {
    let fieldInvalidators = validationInvalidators.get(fieldName);
    if (!fieldInvalidators) {
      fieldInvalidators = new Set();
      validationInvalidators.set(fieldName, fieldInvalidators);
    }
    fieldInvalidators.add(invalidator);
    return () => {
      invalidator();
      fieldInvalidators.delete(invalidator);
      if (fieldInvalidators.size === 0) {
        validationInvalidators.delete(fieldName);
      }
    };
  }

  function invalidateFieldValidation(fieldName: string) {
    for (const invalidator of validationInvalidators.get(fieldName) ?? []) {
      invalidator();
    }
  }

  const RuntimeField = createRuntimeFieldComponent(
    rawForm.Field,
    registerValidationInvalidator,
  );

  function getErrors() {
    const result: Record<string, string> = {};
    for (const [fieldName, meta] of Object.entries(fieldMeta.value)) {
      const error = normalizeFieldMetaError(meta);
      if (error) {
        result[fieldName] = error;
      }
    }
    for (const [fieldName, error] of manualErrors.value) {
      result[fieldName] = error;
    }
    return result;
  }

  const errors = computed(getErrors);
  const meta = computed(() => ({
    dirty: isDirty.value,
    submitting: isSubmitting.value,
    valid: isValid.value && manualErrors.value.size === 0,
    validating: isValidating.value,
  }));
  const runtimeState = computed<FormRuntimeState<TValues>>(() => ({
    errors: errors.value,
    meta: meta.value,
    values: values.value,
  }));

  function getFieldError(fieldName: string) {
    return (
      manualErrors.value.get(fieldName) ??
      normalizeFieldMetaError(Reflect.get(fieldMeta.value, fieldName))
    );
  }

  function useFieldError(fieldName: string) {
    const schemaError = rawForm.useSelector((formState) =>
      normalizeFieldMetaError(Reflect.get(formState.fieldMeta, fieldName)),
    );
    return computed(
      () => manualErrors.value.get(fieldName) ?? schemaError.value,
    );
  }

  function useFieldValue<TFieldName extends FormFieldName<TValues>>(
    fieldName: TFieldName,
  ) {
    return rawForm.useSelector(
      () =>
        rawForm.getFieldValue(fieldName as never) as FormFieldValue<
          TValues,
          TFieldName
        >,
    );
  }

  function useFieldValues<TFieldName extends FormFieldName<TValues>>(
    fieldNames: readonly TFieldName[],
  ) {
    const selectedValues = fieldNames.map((fieldName) =>
      useFieldValue(fieldName),
    );
    return computed(() => selectedValues.map((value) => value.value));
  }

  async function validateField(fieldName: string) {
    await rawForm.validateField(fieldName as never, 'submit');
    const error = getFieldError(fieldName);
    return {
      errors: error ? { [fieldName]: error } : {},
      valid: !error,
    };
  }

  async function validate() {
    await rawForm.validateAllFields('submit');
    const errors = getErrors();
    return {
      errors,
      valid: Object.keys(errors).length === 0,
    };
  }

  function setFieldError(fieldName: string, error?: string) {
    invalidateFieldValidation(fieldName);

    const nextManualErrors = new Map(manualErrors.value);
    if (error) {
      nextManualErrors.set(fieldName, error);
    } else {
      nextManualErrors.delete(fieldName);
    }
    manualErrors.value = nextManualErrors;

    if (error || !rawForm.getFieldMeta(fieldName as never)) {
      return;
    }
    rawForm.setFieldMeta(fieldName as never, (meta) => ({
      ...meta,
      errorMap: {},
    }));
  }

  function clearValidation(
    fieldNames?: FormFieldName<TValues> | FormFieldName<TValues>[],
  ) {
    let requestedFieldNames: FormFieldName<TValues>[] | undefined;
    if (Array.isArray(fieldNames)) {
      requestedFieldNames = fieldNames;
    } else if (fieldNames) {
      requestedFieldNames = [fieldNames];
    }
    const targetFieldNames = requestedFieldNames ?? [
      ...new Set([
        ...validationInvalidators.keys(),
        ...Object.keys(rawForm.getAllErrors().fields),
        ...manualErrors.value.keys(),
      ]),
    ];

    for (const fieldName of targetFieldNames) {
      setFieldError(fieldName, undefined);
    }
  }

  async function reset(
    resetState?: { values?: Partial<TValues> },
    options?: FormResetOptions,
  ) {
    for (const fieldName of validationInvalidators.keys()) {
      invalidateFieldValidation(fieldName);
    }
    manualErrors.value = new Map();
    const partialValues = resetState?.values;
    let resetValues: TValues | undefined;
    if (partialValues) {
      resetValues = options?.force
        ? (cloneDeep(partialValues) as TValues)
        : mergeWith(
            cloneDeep(rawForm.options.defaultValues ?? defaultValues),
            partialValues,
            (_previousValue: unknown, nextValue: unknown) =>
              Array.isArray(nextValue) ? cloneDeep(nextValue) : undefined,
          );
    }
    rawForm.reset(resetValues, {
      keepDefaultValues: options?.keepDefaultValues,
    });
  }

  async function submit() {
    await rawForm.handleSubmit();
  }

  const actions: FormActions<TValues> = {
    clearValidation,
    get errors() {
      return errors.value;
    },
    fieldComponent: RuntimeField,
    get meta() {
      return meta.value;
    },
    get values() {
      return values.value;
    },
    getFieldError,
    getFieldValue(fieldName) {
      return rawForm.getFieldValue(fieldName as never) as FormFieldValue<
        TValues,
        typeof fieldName
      >;
    },
    handleSubmit(callback?) {
      return async (event?: Event) => {
        event?.preventDefault();
        event?.stopPropagation();
        const result = await validate();
        if (result.valid) {
          await callback?.(values.value as TValues);
        }
      };
    },
    isFieldValid(fieldName) {
      return !getFieldError(fieldName);
    },
    pushFieldValue(fieldName, value) {
      rawForm.pushFieldValue(fieldName as never, value as never);
    },
    async removeFieldValue(fieldName, index) {
      await rawForm.removeFieldValue(fieldName as never, index);
    },
    reset,
    resetForm: reset,
    setFieldError,
    async setFieldValue(fieldName, value, shouldValidate) {
      rawForm.setFieldValue(fieldName as never, value as never, {
        dontValidate: !shouldValidate,
      });
      if (shouldValidate) {
        await validateField(fieldName);
      }
    },
    async setValues(values, shouldValidate) {
      batch(() => {
        for (const [fieldName, value] of Object.entries(values)) {
          const literalFieldName = `[${fieldName}]`;
          const runtimeFieldName = validationInvalidators.has(literalFieldName)
            ? literalFieldName
            : fieldName;
          rawForm.setFieldValue(runtimeFieldName as never, value as never, {
            dontValidate: !shouldValidate,
          });
        }
      });
      if (shouldValidate) {
        await validate();
      }
    },
    submit,
    submitForm: submit,
    useSelector(selector) {
      return computed(() => selector(runtimeState.value));
    },
    useFieldError,
    useFieldValue,
    useFieldValues,
    useValues() {
      return values;
    },
    validate,
    validateField,
  };

  return actions;
}
