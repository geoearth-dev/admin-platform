import type {
  BaseFormComponentType,
  FormCommonConfig,
  FormDependenciesResolveContext,
  FormFieldProps,
  FormItemDependencies,
  FormItemDependenciesLegacy,
  FormSchema,
  FormSchemaContext,
} from '../types'

import { get, isFunction, isObject } from '@/utils/inference'
import { mergeWithArrayOverride } from '@/utils/merge'

import { resolveChildUpdateFieldName } from '../field-name'

type AnyFormSchema = FormSchema<BaseFormComponentType, Record<string, unknown>>

export type NormalizedFormFieldSchema = FormFieldProps & {
  commonComponentProps: NonNullable<FormCommonConfig['componentProps']>
  formFieldProps: Record<string, unknown>
  formItemClass: string
}

interface CreateFormFieldSchemaOptions {
  commonConfig?: FormCommonConfig
  disabled?: boolean
  forceHideLabel?: boolean
  globalCommonConfig?: FormCommonConfig
  hidden?: boolean
}

interface CreateArrayChildSchemaOptions extends CreateFormFieldSchemaOptions {
  arrayField: string
  index: number
}

function createSchemaContext(
  baseContext: FormSchemaContext,
  values?: Partial<Record<string, unknown>>,
): FormSchemaContext {
  const rootValues = values
  const row =
    baseContext.rowPath && rootValues
      ? get(rootValues, baseContext.rowPath)
      : undefined
  return {
    ...baseContext,
    rootValues,
    // 动态路径的结果可能是空值或其他类型，只有对象才能作为行数据。
    row: isObject(row) ? row : undefined,
  }
}

function scopeRowFieldName(rowPath: string, fieldName: string) {
  if (!fieldName) {
    return fieldName
  }

  if (fieldName.startsWith('$root.')) {
    return fieldName.slice('$root.'.length)
  }

  if (fieldName.startsWith('$row.')) {
    return `${rowPath}.${fieldName.slice('$row.'.length)}`
  }

  if (fieldName === rowPath || fieldName.startsWith(`${rowPath}.`)) {
    return fieldName
  }

  return `${rowPath}.${fieldName}`
}

function wrapComponentProps(
  componentProps: AnyFormSchema['componentProps'],
  baseContext: FormSchemaContext,
) {
  if (!isFunction(componentProps)) {
    return componentProps
  }

  return () => componentProps(baseContext)
}

function wrapCustomParamsRender(
  render: AnyFormSchema['help'],
  baseContext: FormSchemaContext,
) {
  if (!isFunction(render)) {
    return render
  }

  return () => render(baseContext)
}

function wrapRenderComponentContent(
  render: AnyFormSchema['renderComponentContent'],
  baseContext: FormSchemaContext,
) {
  if (!isFunction(render)) {
    return render
  }

  return () => render(baseContext)
}

type DependencyHandler<TResult> = (
  ...args: Parameters<NonNullable<FormItemDependenciesLegacy['trigger']>>
) => TResult

function wrapDependencyFn<TResult, TStatic extends boolean | undefined = never>(
  handler: DependencyHandler<TResult> | TStatic,
  baseContext: FormSchemaContext,
): DependencyHandler<TResult> | TStatic {
  if (typeof handler !== 'function') {
    return handler
  }

  // 复用依赖回调的参数类型，并保留各回调的返回值及静态配置类型。
  return (values, actions, controller) =>
    handler(
      values,
      actions,
      controller,
      createSchemaContext(baseContext, values),
    )
}

function scopeDependencies(
  dependencies: FormItemDependencies | undefined,
  baseContext: FormSchemaContext,
): FormItemDependencies | undefined {
  if (!dependencies) {
    return dependencies
  }

  const rowPath = baseContext.rowPath
  if (!rowPath) {
    return dependencies
  }

  const triggerFields =
    dependencies.triggerFields?.map((fieldName) =>
      scopeRowFieldName(rowPath, fieldName),
    ) ?? []
  if (isFunction(dependencies.resolve)) {
    const resolve = dependencies.resolve
    return {
      resolve(context: FormDependenciesResolveContext) {
        return resolve({
          ...context,
          schema: createSchemaContext(baseContext, context.values),
        })
      },
      triggerFields,
    }
  }

  const legacyDependencies = dependencies as FormItemDependenciesLegacy

  return {
    ...legacyDependencies,
    componentProps: wrapDependencyFn(
      legacyDependencies.componentProps,
      baseContext,
    ),
    disabled: wrapDependencyFn(legacyDependencies.disabled, baseContext),
    if: wrapDependencyFn(legacyDependencies.if, baseContext),
    required: wrapDependencyFn(legacyDependencies.required, baseContext),
    rules: wrapDependencyFn(legacyDependencies.rules, baseContext),
    show: wrapDependencyFn(legacyDependencies.show, baseContext),
    trigger: wrapDependencyFn(legacyDependencies.trigger, baseContext),
    triggerFields,
  }
}

function createArrayComponentProps(
  schema: AnyFormSchema,
  options: CreateFormFieldSchemaOptions,
) {
  const componentProps = schema.componentProps
  const arrayProps = 'arrayProps' in schema ? schema.arrayProps : undefined
  const children = getFormArraySchemaChildren(schema)
  const commonConfig = options.commonConfig
  const globalCommonConfig = options.globalCommonConfig
  const schemaProps = children.length > 0 ? { schema: children } : {}

  if (isFunction(componentProps)) {
    return () => ({
      ...arrayProps,
      ...componentProps({ fieldName: schema.fieldName }),
      commonConfig,
      globalCommonConfig,
      ...schemaProps,
    })
  }

  return {
    ...arrayProps,
    ...componentProps,
    commonConfig,
    globalCommonConfig,
    ...schemaProps,
  }
}

function createArrayFieldSchema(
  schema: AnyFormSchema,
  options: CreateFormFieldSchemaOptions,
) {
  const restSchema = { ...(schema as AnyFormSchema & Record<string, unknown>) }
  Reflect.deleteProperty(restSchema, 'arrayProps')
  Reflect.deleteProperty(restSchema, 'children')
  Reflect.deleteProperty(restSchema, 'type')

  return {
    ...restSchema,
    component: 'VbenFormFieldArray',
    componentProps: createArrayComponentProps(schema, options),
  }
}

interface FormArraySchemaLike {
  children?: unknown
  componentProps?: unknown
}

interface UpdatableFormSchemaLike extends FormArraySchemaLike {
  fieldName: string
}

function setSchemaChildren<TSchema extends UpdatableFormSchemaLike>(
  schema: TSchema,
  children: TSchema[],
) {
  if ('children' in schema && Array.isArray(schema.children)) {
    return {
      ...schema,
      children,
    } as TSchema
  }

  if (
    !isFunction(schema.componentProps) &&
    schema.componentProps &&
    Array.isArray((schema.componentProps as Record<string, unknown>).schema)
  ) {
    return {
      ...schema,
      componentProps: {
        ...(schema.componentProps as Record<string, unknown>),
        schema: children,
      },
    } as TSchema
  }
  return schema
}

export function getFormArraySchemaChildren<TSchema = FormSchema>(
  schema: FormArraySchemaLike,
): TSchema[] {
  if ('children' in schema && Array.isArray(schema.children)) {
    return schema.children as TSchema[]
  }

  const componentProps = schema.componentProps
  if (
    !isFunction(componentProps) &&
    componentProps &&
    Array.isArray((componentProps as Record<string, unknown>).schema)
  ) {
    return (componentProps as Record<string, unknown>).schema as TSchema[]
  }

  return []
}

export function isFormArraySchema(schema: Partial<AnyFormSchema>) {
  return (
    ('type' in schema && schema.type === 'array') ||
    schema.component === 'VbenFormFieldArray' ||
    getFormArraySchemaChildren(schema).length > 0
  )
}

export function resolveArrayChildFieldName(rowPath: string, fieldName: string) {
  return scopeRowFieldName(rowPath, fieldName)
}

export function updateFormSchemaList<TSchema extends UpdatableFormSchemaLike>(
  currentSchema: TSchema[],
  updated: Partial<TSchema>[],
): TSchema[] {
  return currentSchema.map((schema) => {
    const exactUpdatedData = updated.find(
      (item) => item.fieldName === schema.fieldName,
    )
    if (exactUpdatedData) {
      return mergeWithArrayOverride(exactUpdatedData, schema) as TSchema
    }

    const children = getFormArraySchemaChildren<TSchema>(schema)
    if (children.length === 0) {
      return schema
    }
    const childUpdates = updated.flatMap((item) => {
      const fieldName = item.fieldName
        ? resolveChildUpdateFieldName(schema.fieldName, item.fieldName)
        : undefined
      return fieldName ? [{ ...item, fieldName } as Partial<TSchema>] : []
    })
    if (childUpdates.length === 0) {
      return schema
    }
    return setSchemaChildren(
      schema,
      updateFormSchemaList(children, childUpdates),
    )
  })
}

export function createFormFieldSchema(
  schema: AnyFormSchema,
  options: CreateFormFieldSchemaOptions = {},
): NormalizedFormFieldSchema {
  const commonConfig = mergeWithArrayOverride(
    options.commonConfig ?? {},
    options.globalCommonConfig ?? {},
  )
  const {
    changeEventFallback = false,
    collapsible,
    colon = false,
    componentProps = {},
    controlClass = '',
    defaultCollapsed,
    disabled,
    formFieldProps = {},
    formItemClass = '',
    hideLabel = false,
    hideRequiredMark = false,
    labelClass = '',
    labelWidth = 100,
    modelPropName = '',
    wrapperClass = '',
  } = commonConfig
  // defu 会把 null 当作缺省值；空状态的 null 是显式组件协议，必须保留。
  const emptyStateValue =
    options.commonConfig?.emptyStateValue !== undefined
      ? options.commonConfig.emptyStateValue
      : options.globalCommonConfig?.emptyStateValue

  const normalizedSchema = isFormArraySchema(schema)
    ? createArrayFieldSchema(schema, options)
    : schema

  let resolvedSchemaFormItemClass = normalizedSchema.formItemClass
  if (isFunction(normalizedSchema.formItemClass)) {
    try {
      resolvedSchemaFormItemClass = normalizedSchema.formItemClass()
    } catch (error) {
      console.error('Error calling formItemClass function:', error)
      resolvedSchemaFormItemClass = ''
    }
  }

  const resolvedCommonFormItemClass = isFunction(formItemClass)
    ? formItemClass()
    : formItemClass

  return {
    changeEventFallback,
    collapsible,
    colon,
    emptyStateValue,
    defaultCollapsed,
    hideRequiredMark,
    labelWidth,
    modelPropName,
    wrapperClass,
    ...normalizedSchema,
    commonComponentProps: componentProps,
    componentProps: normalizedSchema.componentProps,
    controlClass: [controlClass, normalizedSchema.controlClass]
      .filter(Boolean)
      .join(' '),
    formFieldProps: {
      ...formFieldProps,
      ...normalizedSchema.formFieldProps,
    },
    formItemClass: [
      'shrink-0',
      options.hidden ? 'hidden' : '',
      resolvedCommonFormItemClass,
      resolvedSchemaFormItemClass,
    ]
      .filter(Boolean)
      .join(' '),
    labelClass: [labelClass, normalizedSchema.labelClass]
      .filter(Boolean)
      .join(' '),
    disabled: options.disabled ?? normalizedSchema.disabled ?? disabled,
    hideLabel:
      options.forceHideLabel ?? normalizedSchema.hideLabel ?? hideLabel,
  } as NormalizedFormFieldSchema
}

export function createArrayChildSchema(
  schema: AnyFormSchema,
  options: CreateArrayChildSchemaOptions,
): NormalizedFormFieldSchema {
  const rowPath = `${options.arrayField}[${options.index}]`
  const fieldName = resolveArrayChildFieldName(rowPath, schema.fieldName)
  const baseContext: FormSchemaContext = {
    arrayField: options.arrayField,
    fieldName,
    originalFieldName: schema.fieldName,
    rowIndex: options.index,
    rowPath,
  }

  return createFormFieldSchema(
    {
      ...schema,
      componentProps: wrapComponentProps(schema.componentProps, baseContext),
      dependencies: scopeDependencies(schema.dependencies, baseContext),
      fieldName,
      help: wrapCustomParamsRender(schema.help, baseContext),
      renderComponentContent: wrapRenderComponentContent(
        schema.renderComponentContent,
        baseContext,
      ),
    },
    {
      commonConfig: options.commonConfig,
      disabled: options.disabled || schema.disabled,
      forceHideLabel: true,
      globalCommonConfig: options.globalCommonConfig,
    },
  )
}
