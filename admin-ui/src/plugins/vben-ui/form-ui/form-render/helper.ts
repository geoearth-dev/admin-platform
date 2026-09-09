import type { ZodType } from 'zod'

import { toRaw } from 'vue'

import { isString } from '@/utils/inference'

import { ZodArray, ZodPipe } from 'zod'

type UnwrappableZodType = ZodType & {
  unwrap?: () => ZodType
}

/**
 * Get the lowest level Zod type.
 * This will unpack optionals, refinements, etc.
 */
export function getBaseRules(schema?: null | string | ZodType): null | ZodType {
  if (!schema || isString(schema)) return null
  const rawSchema = toRaw(schema)

  // 数组的 unwrap 返回元素规则；管道还包含转换后的校验，两者都必须保留。
  if (rawSchema instanceof ZodArray || rawSchema instanceof ZodPipe) {
    return rawSchema
  }

  const unwrappedSchema = (rawSchema as UnwrappableZodType).unwrap?.()
  if (unwrappedSchema && unwrappedSchema !== rawSchema) {
    return getBaseRules(unwrappedSchema)
  }

  return rawSchema
}

/**
 * Search for a "ZodDefault" in the Zod stack and return its value.
 */
export function getDefaultValueInZodStack(
  schema?: null | string | ZodType,
): unknown {
  if (!schema || isString(schema)) {
    return
  }

  try {
    const result = toRaw(schema).safeParse(undefined)
    return result.success ? result.data : undefined
  } catch {
    return undefined
  }
}

export function isEventObjectLike(obj: unknown): obj is {
  target: Record<string, unknown>
  stopPropagation: () => void
} {
  return (
    typeof obj === 'object' &&
    obj !== null &&
    'target' in obj &&
    typeof obj.target === 'object' &&
    obj.target !== null &&
    'stopPropagation' in obj &&
    typeof obj.stopPropagation === 'function'
  )
}
