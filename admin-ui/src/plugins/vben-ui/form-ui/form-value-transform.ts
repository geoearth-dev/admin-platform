import type {
  ArrayToStringFields,
  BaseFormComponentType,
  FieldMappingTime,
  FormSchema,
  FormSchemaContext,
  FormValues,
} from './types'

import { cloneDeep, isObject } from '@/utils/inference'
import { formatDate, isDate, isDayjsObject } from '@/utils/date'

import {
  deleteValueByFieldName,
  getValueByFieldName,
  resolveValueFormatFieldName,
  setValueByFieldName,
} from './field-name'
import {
  getFormArraySchemaChildren,
  resolveArrayChildFieldName,
} from './form-render/schema'

type AnyFormSchema<TValues extends FormValues> = FormSchema<
  BaseFormComponentType,
  Record<string, unknown>,
  TValues
>

function processFields(
  fields: string[],
  separator: string,
  values: FormValues,
) {
  for (const field of fields) {
    const value = getValueByFieldName(values, field)
    if (value === undefined || value === null) {
      continue
    }
    if (Array.isArray(value)) {
      setValueByFieldName(values, field, value.join(separator))
      continue
    }
    if (typeof value !== 'string') {
      continue
    }
    if (value === '') {
      setValueByFieldName(values, field, [])
      continue
    }
    const escapedSeparator = separator.replaceAll(
      /[.*+?^${}()|[\]\\]/g,
      String.raw`\$&`,
    )
    setValueByFieldName(
      values,
      field,
      value.split(new RegExp(escapedSeparator)),
    )
  }
}

function applyArrayToStringFields(
  values: FormValues,
  arrayToStringFields?: ArrayToStringFields,
) {
  if (!arrayToStringFields || !Array.isArray(arrayToStringFields)) {
    return
  }

  if (arrayToStringFields.every((item) => typeof item === 'string')) {
    const fieldsConfig = arrayToStringFields
    const lastItem = fieldsConfig.at(-1) ?? ''
    // 保留末尾标点分隔符的兼容写法；单字符字段 x/y 仍是字段名。
    const hasSeparator =
      fieldsConfig.length > 1 && /^[^\p{L}\p{N}_$]$/u.test(lastItem)
    const fields = hasSeparator ? fieldsConfig.slice(0, -1) : fieldsConfig
    processFields(fields, hasSeparator ? lastItem : ',', values)
    return
  }

  for (const fieldConfig of arrayToStringFields) {
    if (typeof fieldConfig === 'string') {
      processFields([fieldConfig], ',', values)
      continue
    }
    if (!Array.isArray(fieldConfig)) {
      continue
    }
    const [fields, separator = ','] = fieldConfig
    if (!Array.isArray(fields)) {
      applyArrayToStringFields(values, fieldConfig as string[])
      continue
    }
    processFields(
      fields,
      typeof separator === 'string' ? separator : ',',
      values,
    )
  }
}

function applyRangeTimeFields(
  values: FormValues,
  fieldMappingTime?: FieldMappingTime,
) {
  if (!fieldMappingTime || !Array.isArray(fieldMappingTime)) {
    return
  }

  for (const [
    field,
    [startTimeKey, endTimeKey],
    format = 'YYYY-MM-DD',
  ] of fieldMappingTime) {
    const range = getValueByFieldName(values, field)
    if (startTimeKey && endTimeKey && range === null) {
      deleteValueByFieldName(values, startTimeKey)
      deleteValueByFieldName(values, endTimeKey)
    }
    if (range === null || range === undefined) {
      deleteValueByFieldName(values, field)
      continue
    }

    // 日期范围必须是数组；未知类型不应被当作可迭代范围拆分。
    if (!Array.isArray(range)) continue
    const [startTime, endTime]: unknown[] = range
    if (format === null) {
      setValueByFieldName(values, startTimeKey, startTime)
      setValueByFieldName(values, endTimeKey, endTime)
    } else if (typeof format === 'function') {
      setValueByFieldName(values, startTimeKey, format(startTime, startTimeKey))
      setValueByFieldName(values, endTimeKey, format(endTime, endTimeKey))
    } else {
      const [startTimeFormat, endTimeFormat] = Array.isArray(format)
        ? format
        : [format, format]
      setValueByFieldName(
        values,
        startTimeKey,
        formatRangeValue(startTime, startTimeFormat),
      )
      setValueByFieldName(
        values,
        endTimeKey,
        formatRangeValue(endTime, endTimeFormat),
      )
    }
    deleteValueByFieldName(values, field)
  }
}

function formatRangeValue(value: unknown, format: string) {
  if (value === null || value === undefined || value === '') return undefined
  if (
    typeof value === 'string' ||
    typeof value === 'number' ||
    isDate(value) ||
    isDayjsObject(value)
  ) {
    return formatDate(value, format)
  }
  return undefined
}

function applyValueFormatBySchemas<TValues extends FormValues>(
  schemas: AnyFormSchema<TValues>[],
  values: FormValues,
  parentPath?: string,
  parentContext?: FormSchemaContext<TValues>,
) {
  for (const schema of schemas) {
    const fieldName = parentPath
      ? resolveArrayChildFieldName(parentPath, schema.fieldName)
      : schema.fieldName
    const row =
      parentPath && parentContext?.rowPath
        ? getValueByFieldName(values, parentContext.rowPath)
        : parentContext?.row
    const schemaContext: FormSchemaContext<TValues> = {
      ...parentContext,
      fieldName,
      originalFieldName: schema.fieldName,
      rootValues: values as TValues,
      row: isObject(row) ? row : undefined,
    }

    const children = getFormArraySchemaChildren<AnyFormSchema<TValues>>(schema)
    if (children.length > 0) {
      const arrayValue = getValueByFieldName(values, fieldName)
      if (Array.isArray(arrayValue)) {
        arrayValue.forEach((rowValue, index) => {
          const rowPath = `${fieldName}[${index}]`
          applyValueFormatBySchemas(children, values, rowPath, {
            arrayField: fieldName,
            row: isObject(rowValue) ? rowValue : undefined,
            rowIndex: index,
            rowPath,
          })
        })
      }
    }

    if (!schema.valueFormat) {
      continue
    }
    const value = getValueByFieldName(values, fieldName)
    deleteValueByFieldName(values, fieldName)
    const formattedValue = schema.valueFormat(
      value,
      (key, nextValue) => {
        setValueByFieldName(
          values,
          resolveValueFormatFieldName(key, parentPath),
          nextValue,
        )
      },
      values as TValues,
      schemaContext,
    )
    if (formattedValue !== undefined) {
      setValueByFieldName(values, fieldName, formattedValue)
    }
  }
}

export function applyFormValueFormats<TValues extends FormValues>(
  originValues: FormValues,
  schemas: AnyFormSchema<TValues>[],
) {
  const values = cloneDeep(originValues)
  applyValueFormatBySchemas(schemas, values)
  return values
}

export function formatFormValues<TValues extends FormValues>(
  originValues: Readonly<FormValues>,
  schemas: AnyFormSchema<TValues>[],
  fieldMappingTime?: FieldMappingTime,
  arrayToStringFields?: ArrayToStringFields,
) {
  const values = cloneDeep(originValues)
  applyArrayToStringFields(values, arrayToStringFields)
  applyRangeTimeFields(values, fieldMappingTime)
  applyValueFormatBySchemas(schemas, values)
  return values
}

export function transformRangeTimeValues(
  originValues: FormValues,
  fieldMappingTime?: FieldMappingTime,
  arrayToStringFields?: ArrayToStringFields,
) {
  const values = cloneDeep(originValues)
  applyArrayToStringFields(values, arrayToStringFields)
  applyRangeTimeFields(values, fieldMappingTime)
  return values
}
