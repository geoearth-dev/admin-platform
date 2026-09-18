import { capitalizeFirstLetter } from '@/utils/letter'
import { trigger } from './config'

import type { ComponentConfig, GenerateType } from './types'

export type { GenerateType } from './types'
export type GeneratorField = ComponentConfig

export interface GeneratorFormConfig {
  fields: GeneratorField[]
  formRef: string
  formModel: string
  formRules: string
}

const ruleTriggers: Readonly<Partial<Record<string, string>>> = trigger
const units: Readonly<Partial<Record<string, number>>> = {
  KB: 1024,
  MB: 1024 ** 2,
  GB: 1024 ** 3,
}

/** 序列化为 TS 表达式，同时防止配置中的 </script> 提前结束 SFC 脚本。 */
function literal(value: unknown): string {
  return (JSON.stringify(value) ?? 'undefined')
    .replace(/</g, '\\u003c')
    .replace(/\u2028/g, '\\u2028')
    .replace(/\u2029/g, '\\u2029')
}

function flatten(fields: GeneratorField[]): GeneratorField[] {
  return fields.flatMap((field) => [field, ...flatten(field.children ?? [])])
}

interface ModelMember {
  type: string
  value: unknown
}

/** 按组件契约确定类型；空数组、null、undefined 不能单独用于推断类型。 */
function modelMember(field: GeneratorField): ModelMember {
  const value = field.defaultValue
  switch (field.tag) {
    case 'el-input':
      return { type: 'string', value: value == null ? '' : String(value) }
    case 'el-input-number':
      return {
        type: 'number | undefined',
        value: typeof value === 'number' ? value : undefined,
      }
    case 'el-rate':
      return { type: 'number', value: typeof value === 'number' ? value : 0 }
    case 'el-slider':
      return field.range
        ? {
            type: 'number[]',
            value: Array.isArray(value)
              ? value
              : [field.min ?? 0, field.max ?? 100],
          }
        : {
            type: 'number',
            value: typeof value === 'number' ? value : (field.min ?? 0),
          }
    case 'el-switch': {
      const active = field['active-value'] ?? true
      const inactive = field['inactive-value'] ?? false
      const types = [...new Set([typeof active, typeof inactive])].join(' | ')
      return {
        type: types,
        value: value === active || value === inactive ? value : inactive,
      }
    }
    case 'el-checkbox-group':
      return {
        type: 'Array<string | number | boolean>',
        value: Array.isArray(value) ? value : [],
      }
    case 'el-select':
      return field.multiple
        ? {
            type: 'Array<string | number | boolean>',
            value: Array.isArray(value) ? value : [],
          }
        : {
            type: 'string | number | boolean | undefined',
            value: value ?? undefined,
          }
    case 'el-radio-group':
      return {
        type: 'string | number | boolean | undefined',
        value: value ?? undefined,
      }
    case 'el-cascader':
      return { type: 'CascaderValue | undefined', value: value ?? undefined }
    case 'el-color-picker':
      return { type: 'string', value: typeof value === 'string' ? value : '' }
    case 'el-date-picker':
      return { type: "DatePickerProps['modelValue']", value: value ?? null }
    case 'el-time-picker':
      return {
        type: "TimePickerDefaultProps['modelValue']",
        value: value ?? null,
      }
    default:
      throw new Error(`尚未定义组件 ${field.tag ?? '(无 tag)'} 的 TS 字段类型`)
  }
}

/** 同时支持 /^...$/i 和不带斜杠的正则表达式文本。 */
function regexpExpression(pattern: string): string {
  const match = pattern.match(/^\/([\s\S]*)\/([a-z]*)$/)
  const source = match?.[1] ?? pattern
  const flags = match?.[2] ?? ''
  new RegExp(source, flags) // 配置无效时，在生成阶段给出错误。
  return `new RegExp(${literal(source)}, ${literal(flags)})`
}

function rulesFor(field: GeneratorField): string[] {
  const event = field.tag ? ruleTriggers[field.tag] : undefined
  if (!event) return []
  const rules: string[] = []
  if (field.required) {
    const array =
      field.tag === 'el-checkbox-group' ||
      (field.tag === 'el-select' && field.multiple) ||
      Array.isArray(field.defaultValue)
    const message =
      field.placeholder || `${field.label ?? field.vModel}不能为空`
    rules.push(
      `{ required: true, ${array ? "type: 'array', " : ''}message: ${literal(message)}, trigger: ${literal(event)} }`,
    )
  }
  for (const rule of field.regList ?? []) {
    if (rule.pattern) {
      rules.push(
        `{ pattern: ${regexpExpression(rule.pattern)}, message: ${literal(rule.message)}, trigger: ${literal(event)} }`,
      )
    }
  }
  return rules
}

/** 返回 script setup 内部的 TS 文本；script 标签由 vueScript() 包装。 */
export function generateScript(
  conf: GeneratorFormConfig,
  type: GenerateType,
): string {
  if (type !== 'file' && type !== 'dialog') throw new Error('未知的生成类型')

  const allFields = flatten(conf.fields).filter(
    (field) => field.vModel && field.tag && field.tag !== 'el-button',
  )
  const uploads = allFields.filter((field) => field.tag === 'el-upload')
  const modelFields = allFields.filter((field) => field.tag !== 'el-upload')
  const typeImports = new Set(['FormInstance', 'FormRules'])
  const runtimeImports = new Set<string>()
  const declarations: string[] = []
  const methods: string[] = []
  const members: string[] = []
  const values: string[] = []
  const rules: string[] = []
  const names = new Set(
    (
      'ref reactive FormModel FormInstance FormRules FieldOption CascaderValue CascaderOption CascaderProps DatePickerProps TimePickerDefaultProps UploadInstance UploadProps UploadUserFile ElMessage dialogVisible emit onOpen onClose close handelConfirm submitForm resetForm submitUpload ' +
      'break case catch class const continue debugger default delete do else enum export extends false finally for function if import in instanceof new null return super switch this throw true try typeof var void while with yield let static implements interface package private protected public await arguments eval'
    ).split(/\s+/),
  )
  function identifier(name: string): void {
    if (!/^[$A-Z_a-z][$\w]*$/.test(name))
      throw new Error(`名称 ${name} 必须是合法变量名`)
  }
  function reserve(name: string): void {
    identifier(name)
    if (names.has(name))
      throw new Error(`生成的变量名 ${name} 重复或与保留名称冲突`)
    names.add(name)
  }
  ;[conf.formRef, conf.formModel, conf.formRules].forEach(reserve)
  const fieldNames = new Set<string>()

  for (const field of allFields) {
    const name = field.vModel!
    identifier(name)
    if (fieldNames.has(name)) throw new Error(`表单字段 ${name} 重复`)
    fieldNames.add(name)
  }

  for (const field of modelFields) {
    const name = field.vModel!
    const member = modelMember(field)
    if (field.tag === 'el-cascader') typeImports.add('CascaderValue')
    if (field.tag === 'el-date-picker') typeImports.add('DatePickerProps')
    if (field.tag === 'el-time-picker')
      typeImports.add('TimePickerDefaultProps')
    members.push(`${literal(name)}: ${member.type};`)
    values.push(`${literal(name)}: ${literal(member.value)},`)
    const fieldRules = rulesFor(field)
    if (fieldRules.length)
      rules.push(`${literal(name)}: [${fieldRules.join(', ')}],`)
  }

  const choiceFields = modelFields.filter((field) =>
    [
      'el-select',
      'el-radio-group',
      'el-checkbox-group',
      'el-cascader',
    ].includes(field.tag ?? ''),
  )
  if (choiceFields.some((field) => field.tag !== 'el-cascader')) {
    declarations.push(`interface FieldOption {
      label: string;
      value: string | number | boolean;
      disabled?: boolean;
    }`)
  }
  for (const field of choiceFields) {
    const name = field.vModel!
    const optionName = `${name}Options`
    reserve(optionName)
    const cascader = field.tag === 'el-cascader'
    if (cascader) typeImports.add('CascaderOption')
    const optionType = cascader ? 'CascaderOption[]' : 'FieldOption[]'
    const options = field.dataType === 'dynamic' ? [] : (field.options ?? [])
    declarations.push(
      `const ${optionName} = ref<${optionType}>(${literal(options)});`,
    )
    if (field.dataType === 'dynamic') {
      const method = `get${capitalizeFirstLetter(optionName)}`
      reserve(method)
      methods.push(`async function ${method}(): Promise<void> {
        // TODO 使用项目的 requestClient 请求选项，然后赋值给 ${optionName}.value。
      }`)
    }
    if (cascader && field.props?.props) {
      const props = { ...field.props.props }
      if (field.dataType === 'dynamic') {
        if (field.valueKey) props.value = field.valueKey
        if (field.labelKey) props.label = field.labelKey
        if (field.childrenKey) props.children = field.childrenKey
      }
      typeImports.add('CascaderProps')
      reserve(`${name}Props`)
      declarations.push(
        `const ${name}Props: CascaderProps = ${literal(props)};`,
      )
    }
  }

  for (const field of uploads) {
    const name = field.vModel!
    ;[name, `${name}Action`, `${name}fileList`, `${name}BeforeUpload`].forEach(
      reserve,
    )
    ;['UploadInstance', 'UploadProps', 'UploadUserFile'].forEach((item) =>
      typeImports.add(item),
    )
    declarations.push(`const ${name} = ref<UploadInstance>();
      const ${name}Action = ${literal(field.action ?? '')};
      const ${name}fileList = ref<UploadUserFile[]>([]);`)
    const checks: string[] = []
    if (field.fileSize) {
      const unit = field.sizeUnit ? units[field.sizeUnit] : undefined
      if (!unit) throw new Error(`上传字段 ${name} 的大小单位无效`)
      runtimeImports.add('ElMessage')
      checks.push(`if (file.size / ${unit} >= ${field.fileSize}) {
        ElMessage.error(${literal(`文件大小不能达到或超过 ${field.fileSize}${field.sizeUnit}`)});
        return false;
      }`)
    }
    if (field.accept?.trim()) {
      runtimeImports.add('ElMessage')
      checks.push(`const accepted = ${literal(field.accept)}.split(',').map((item) => item.trim().toLowerCase()).filter(Boolean);
        const mime = file.type.toLowerCase();
        const matches = accepted.some((item) => item.startsWith('.')
          ? file.name.toLowerCase().endsWith(item)
          : item.endsWith('/*') ? mime.startsWith(item.slice(0, -1)) : mime === item);
        if (accepted.length > 0 && !matches) {
          ElMessage.error(${literal(`请选择 ${field.accept} 类型的文件`)});
          return false;
        }`)
    }
    methods.push(`const ${name}BeforeUpload: UploadProps['beforeUpload'] = (${checks.length ? 'file' : '_file'}) => {
      ${checks.join('\n')}
      return true;
    };`)
  }
  const manualUploads = uploads.filter(
    (field) => field['auto-upload'] === false,
  )
  if (manualUploads.length) {
    methods.push(`function submitUpload(): void {
      ${manualUploads.map((field) => `${field.vModel}.value?.submit();`).join('\n')}
    }`)
  }

  const submitBody = `if (!${conf.formRef}.value) return;
    const valid = await ${conf.formRef}.value.validate().catch(() => false);
    if (!valid) return;
    // TODO 使用项目的 requestClient 提交 ${conf.formModel}。`
  if (type === 'dialog') {
    declarations.push(`const dialogVisible = defineModel<boolean>({ default: false });
      const emit = defineEmits<{ confirm: [] }>();`)
    methods.push(`function onOpen(): void {}
      function onClose(): void { ${conf.formRef}.value?.resetFields(); }
      function close(): void { dialogVisible.value = false; }
      async function handelConfirm(): Promise<void> {
        ${submitBody}
        close();
        emit('confirm');
      }`)
  } else {
    methods.push(`async function submitForm(): Promise<void> {
      ${submitBody}
    }
    function resetForm(): void { ${conf.formRef}.value?.resetFields(); }`)
  }

  return `import { reactive, ref } from 'vue';
    ${runtimeImports.size ? `import { ${[...runtimeImports].join(', ')} } from 'element-plus';` : ''}
    import type { ${[...typeImports].join(', ')} } from 'element-plus';

    interface FormModel {
      ${members.join('\n')}
    }
    const ${conf.formRef} = ref<FormInstance>();
    const ${conf.formModel} = reactive<FormModel>({
      ${values.join('\n')}
    });
    const ${conf.formRules}: FormRules<FormModel> = {
      ${rules.join('\n')}
    };

    ${declarations.join('\n\n')}
    ${methods.join('\n\n')}
  `
}
