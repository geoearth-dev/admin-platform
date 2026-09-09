<script lang="ts" setup>
import dayjs from 'dayjs'
import type { UploadFile, UploadProps, UploadRawFile } from 'element-plus'
import { ElButton, ElDialog } from 'element-plus'
import { h, onBeforeUnmount, ref } from 'vue'

import { Page } from '@/components/page'

import { useDebounceFn } from '@vueuse/core'

import { ElMessage, ElTag } from 'element-plus'
import {
  Button,
  Card,
  CardHeader,
  CardTitle,
  CardContent,
} from '@/plugins/vben-ui/shadcn-ui'

import {
  useVbenForm,
  z,
  type BaseFormComponentType,
  type ElementPlusComponentProps,
} from '@/plugins/vben-ui/form-ui'
import type { ApiComponentParams } from '@/components/api-component'
import { getExampleMenuApi } from '@/api/example/menu'
import { uploadExampleFileApi } from '@/api/example/mock-upload'
import { $t } from '@/plugins/locale'
import { VCropper } from '@/components/cropper'

import DocButton from '../doc-button.vue'

const keyword = ref('')
const fetching = ref(false)
const cropperRef = ref<InstanceType<typeof VCropper>>()
const cropSource = ref('')
const cropVisible = ref(false)
const cropBusy = ref(false)
let cropFile: UploadRawFile | undefined
let finishCrop: ((result: File | false) => void) | undefined

function settleCrop(result: File | false = false) {
  const resolve = finishCrop
  finishCrop = undefined
  cropFile = undefined
  cropVisible.value = false
  if (cropSource.value) URL.revokeObjectURL(cropSource.value)
  cropSource.value = ''
  resolve?.(result)
}

async function confirmCrop() {
  const file = cropFile
  const cropper = cropperRef.value
  if (!file || !cropper || cropBusy.value) return
  cropBusy.value = true
  try {
    const format = /\.png$/i.test(file.name) ? 'image/png' : 'image/jpeg'
    const blob = await cropper.getCropImage(format, 0.92, 'blob')
    // 异步导出期间取消或离开页面时，不再提交旧文件。
    if (cropFile !== file) return
    if (!(blob instanceof Blob) || blob.size === 0) {
      throw new Error('图片裁剪失败，请重新选择图片')
    }
    if (blob.size > 2 * 1024 * 1024) {
      ElMessage.error('裁剪后的图片超过 2MB，请缩小裁剪范围')
      return
    }
    settleCrop(new File([blob], file.name, { type: format }))
  } catch (error) {
    if (cropFile === file) {
      ElMessage.error(error instanceof Error ? error.message : '图片裁剪失败')
    }
  } finally {
    cropBusy.value = false
  }
}

// 页面离开时取消仍在进行的上传；每次请求使用独立信号。
const uploadControllers = new Map<number, AbortController>()
const requestUpload: NonNullable<UploadProps['httpRequest']> = (options) => {
  const controller = new AbortController()
  uploadControllers.set(options.file.uid, controller)
  return uploadExampleFileApi(options.file, controller.signal, (percent) => {
    options.onProgress(
      Object.assign(new ProgressEvent('progress'), { percent }),
    )
  }).finally(() => {
    if (uploadControllers.get(options.file.uid) === controller) {
      uploadControllers.delete(options.file.uid)
    }
  })
}

onBeforeUnmount(() => {
  settleCrop()
  uploadControllers.forEach((controller) => controller.abort())
  uploadControllers.clear()
})

const beforeUpload: NonNullable<UploadProps['beforeUpload']> = (file) => {
  if (!/\.(png|jpe?g)$/i.test(file.name)) {
    ElMessage.error('请选择 PNG 或 JPG 图片')
    return false
  }
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过 2MB')
    return false
  }
  return true
}

const beforeCropUpload: NonNullable<UploadProps['beforeUpload']> = async (
  file,
) => {
  if ((await beforeUpload(file)) === false) return false
  settleCrop()
  cropFile = file
  cropSource.value = URL.createObjectURL(file)
  cropVisible.value = true
  return new Promise<File | false>((resolve) => {
    finishCrop = resolve
  })
}

const handleUploadChange: NonNullable<UploadProps['onChange']> = (file) => {
  if (file.status === 'success') {
    // 裁剪上传后展示服务器上的结果，避免继续显示原图缩略图。
    const url = getUploadUrl(file)
    if (url && url !== file.url) {
      if (file.url?.startsWith('blob:')) URL.revokeObjectURL(file.url)
      file.url = url
    }
    ElMessage.success(`${file.name} ${$t('examples.form.upload-success')}`)
  } else if (file.status === 'fail') {
    ElMessage.error(`${file.name} ${$t('examples.form.upload-fail')}`)
  }
}

const handleUploadRemove: NonNullable<UploadProps['onRemove']> = (file) => {
  if (cropFile?.uid === file.uid) settleCrop()
  uploadControllers.get(file.uid)?.abort()
  uploadControllers.delete(file.uid)
}

const uploadProps = {
  accept: '.png,.jpg,.jpeg',
  httpRequest: requestUpload,
  beforeUpload,
  listType: 'picture-card',
  showFileList: true,
  onChange: handleUploadChange,
  onRemove: handleUploadRemove,
  onExceed: () => ElMessage.warning('已达到图片数量上限，请先移除已有图片'),
} satisfies UploadProps

const uploadResponseSchema = z.object({ url: z.string().min(1) })

function getUploadUrl(file: UploadFile): string | undefined {
  const result = uploadResponseSchema.safeParse(file.response)
  return result.success ? result.data.url : file.url
}

interface BasicFormValues extends Record<string, unknown> {
  cropImage?: UploadFile[]
  files?: UploadFile[]
  rangePicker?: [Date, Date] | null
}

function encodeBasicFormValues(values: Readonly<BasicFormValues>) {
  const { rangePicker, ...formValues } = values
  return {
    ...formValues,
    endTime: rangePicker?.[1]
      ? dayjs(rangePicker[1]).format('YYYY-MM-DD')
      : undefined,
    startTime: rangePicker?.[0]
      ? dayjs(rangePicker[0]).format('YYYY-MM-DD')
      : undefined,
  }
}

type BasicSubmitValues = ReturnType<typeof encodeBasicFormValues>

function decodeBasicFormValues(
  values: Readonly<BasicSubmitValues>,
): BasicFormValues {
  const { endTime, startTime, ...formValues } = values
  return {
    ...formValues,
    rangePicker:
      startTime && endTime
        ? [dayjs(startTime).toDate(), dayjs(endTime).toDate()]
        : null,
  }
}

// 模拟远程获取数据
function fetchRemoteOptions(
  params: ApiComponentParams = {},
): Promise<{ label: string; value: string }[]> {
  const search = typeof params.keyword === 'string' ? params.keyword : '选项'
  fetching.value = true
  return new Promise((resolve) => {
    setTimeout(() => {
      const options = Array.from({ length: 10 }).map((_, index) => ({
        label: `${search}-${index}`,
        value: `${search}-${index}`,
      }))
      resolve(options)
      fetching.value = false
    }, 1000)
  })
}

const handleSearch = useDebounceFn((value: string) => {
  keyword.value = value
}, 300)

// 后端返回平铺菜单，树选择器需要按 parentId 建立层级。
const menuSchema = z.object({
  id: z.union([z.string(), z.number()]),
  parentId: z.union([z.string(), z.number()]).nullish(),
  menuName: z.string(),
})
type MenuNode = z.infer<typeof menuSchema> & { children?: MenuNode[] }
async function fetchMenuTree(
  params: ApiComponentParams = {},
): Promise<MenuNode[]> {
  const menus = z.array(menuSchema).parse(await getExampleMenuApi(params))
  const nodes = new Map<string, MenuNode>(
    menus.map((menu) => [String(menu.id), { ...menu }]),
  )
  const roots: MenuNode[] = []
  for (const node of nodes.values()) {
    const parent =
      node.parentId == null ? undefined : nodes.get(String(node.parentId))
    if (parent && parent !== node) (parent.children ??= []).push(node)
    else roots.push(node)
  }
  return roots
}

const [BaseForm, baseFormApi] = useVbenForm<
  BasicFormValues,
  BaseFormComponentType,
  ElementPlusComponentProps,
  BasicSubmitValues
>({
  codec: {
    decode: decodeBasicFormValues,
    encode: encodeBasicFormValues,
  },
  // 所有表单项共用，可单独在表单内覆盖
  commonConfig: {
    // 在label后显示一个冒号
    colon: true,
    // 所有表单项
    componentProps: {
      class: 'w-full',
    },
  },
  // 提交函数
  handleSubmit: onSubmit,
  handleValuesChange(_values, fieldsChanged) {
    ElMessage.info(`表单以下字段发生变化：${fieldsChanged.join('，')}`)
  },

  // 垂直布局，label和input在不同行，值为vertical
  // 水平布局，label和input在同一行
  layout: 'horizontal',
  schema: [
    {
      // 组件名称由 form-ui 内部的 Element Plus 适配层提供
      component: 'Input',
      // 对应组件的参数
      componentProps: {
        placeholder: '请输入用户名',
      },
      // 字段名
      fieldName: 'username',
      // 界面显示的label
      label: '字符串',
      rules: 'required',
    },
    {
      component: 'Input',
      fieldName: 'desc',
      // 界面显示的description
      description: '这是表单描述',
      label: '字符串(带描述)',
    },
    {
      // 组件名称由 form-ui 内部的 Element Plus 适配层提供
      component: 'ApiSelect',
      // 对应组件的参数
      componentProps: {
        labelField: 'menuName',
        valueField: 'id',
        // 菜单接口
        api: getExampleMenuApi,
        autoSelect: 'first',
      },
      // 字段名
      fieldName: 'api',
      // 界面显示的label
      label: 'ApiSelect',
    },
    {
      component: 'ApiSelect',
      // 对应组件的参数
      componentProps: () => {
        return {
          api: fetchRemoteOptions,
          filterable: true,
          remote: true,
          loading: fetching.value,
          remoteMethod: handleSearch,
          // 远程搜索参数。当搜索词变化时，params也会更新
          params: {
            keyword: keyword.value || undefined,
          },
          // 远程搜索判断。当为true时，才允许调用api
          shouldFetch: (params: ApiComponentParams) => {
            return typeof params.keyword === 'string' && !!params.keyword.trim()
          },
        }
      },
      // 字段名
      fieldName: 'remoteSearch',
      // 界面显示的label
      label: '远程搜索',
      help: '远程查询，仅有输入时方进行查询',
      rules: 'selectRequired',
    },
    {
      component: 'ApiTreeSelect',
      // 对应组件的参数
      componentProps: {
        // 菜单接口
        api: fetchMenuTree,
        // 菜单接口转options格式
        labelField: 'menuName',
        valueField: 'id',
        childrenField: 'children',
      },
      // 字段名
      fieldName: 'apiTree',
      // 界面显示的label
      label: 'ApiTreeSelect',
    },
    {
      component: 'InputPassword',
      componentProps: {
        placeholder: '请输入密码',
      },
      fieldName: 'password',
      label: '密码',
    },
    {
      component: 'InputNumber',
      componentProps: {
        placeholder: '请输入',
      },
      fieldName: 'number',
      label: '数字(带后缀)',
      suffix: () => '¥',
    },
    {
      component: 'IconPicker',
      fieldName: 'icon',
      label: '图标',
    },
    {
      colon: false,
      component: 'Select',
      componentProps: {
        clearable: true,
        options: [
          {
            label: '选项1',
            value: '1',
          },
          {
            label: '选项2',
            value: '2',
          },
        ],
        placeholder: '请选择',
        filterable: true,
      },
      fieldName: 'options',
      label: () => h(ElTag, { type: 'warning' }, () => '😎自定义：'),
    },
    {
      component: 'RadioGroup',
      componentProps: {
        options: [
          {
            label: '选项1',
            value: '1',
          },
          {
            label: '选项2',
            value: '2',
          },
        ],
      },
      fieldName: 'radioGroup',
      label: '单选组',
    },
    {
      component: 'Radio',
      fieldName: 'radio',
      label: '',
      renderComponentContent: () => {
        return {
          default: () => ['Radio'],
        }
      },
    },
    {
      component: 'CheckboxGroup',
      componentProps: {
        name: 'cname',
        options: [
          {
            label: '选项1',
            value: '1',
          },
          {
            label: '选项2',
            value: '2',
          },
        ],
      },
      fieldName: 'checkboxGroup',
      label: '多选组',
    },
    {
      component: 'Checkbox',
      fieldName: 'checkbox',
      label: '',
      renderComponentContent: () => {
        return {
          default: () => ['我已阅读并同意'],
        }
      },
      rules: z
        .boolean()
        .refine((v) => v, { message: '为什么不同意？勾上它！' }),
    },
    {
      component: 'Mentions',
      componentProps: {
        options: [
          {
            label: 'afc163',
            value: 'afc163',
          },
          {
            label: 'zombieJ',
            value: 'zombieJ',
          },
        ],
        placeholder: '请输入',
      },
      fieldName: 'mentions',
      label: '提及',
    },
    {
      component: 'Rate',
      fieldName: 'rate',
      label: '评分',
    },
    {
      component: 'Switch',
      componentProps: {
        class: 'w-auto',
      },
      fieldName: 'switch',
      help: () =>
        ['这是一个多行帮助信息', '第二行', '第三行'].map((v) => h('p', v)),
      label: '开关',
    },
    {
      component: 'DatePicker',
      dependencies: {
        resolve: ({ values }: { values: BasicFormValues }) => ({
          help: () =>
            [`这是一个可输出其他字段值的帮助信息${values.rate}`].map((value) =>
              h('p', value),
            ),
        }),
        triggerFields: ['rate'],
      },
      fieldName: 'datePicker',
      label: '日期选择框',
    },
    {
      component: 'RangePicker',
      fieldName: 'rangePicker',
      label: '范围选择器',
    },
    {
      component: 'TimePicker',
      fieldName: 'timePicker',
      label: '时间选择框',
    },
    {
      component: 'TreeSelect',
      componentProps: {
        clearable: true,
        placeholder: '请选择',
        filterable: true,
        nodeKey: 'value',
        props: { label: 'label', children: 'children' },
        data: [
          {
            label: 'root 1',
            value: 'root 1',
            children: [
              {
                label: 'parent 1',
                value: 'parent 1',
                children: [
                  {
                    label: 'parent 1-0',
                    value: 'parent 1-0',
                    children: [
                      {
                        label: 'my leaf',
                        value: 'leaf1',
                      },
                      {
                        label: 'your leaf',
                        value: 'leaf2',
                      },
                    ],
                  },
                  {
                    label: 'parent 1-1',
                    value: 'parent 1-1',
                  },
                ],
              },
              {
                label: 'parent 2',
                value: 'parent 2',
              },
            ],
          },
        ],
      },
      fieldName: 'treeSelect',
      label: '树选择',
    },
    {
      component: 'Upload',
      componentProps: {
        ...uploadProps,
        disabled: false,
        limit: 3,
        multiple: false,
      } satisfies UploadProps,
      fieldName: 'files',
      label: $t('examples.form.file'),
      renderComponentContent: () => {
        return {
          default: () => $t('examples.form.upload-image'),
        }
      },
      rules: 'selectRequired',
    },
    {
      component: 'Upload',
      componentProps: {
        ...uploadProps,
        limit: 1,
        multiple: false,
        beforeUpload: beforeCropUpload,
      } satisfies UploadProps,
      fieldName: 'cropImage',
      label: $t('examples.form.crop-image'),
      renderComponentContent: () => {
        return {
          default: () => $t('examples.form.upload-image'),
        }
      },
      rules: 'selectRequired',
    },
    {
      component: 'RichEditor',
      fieldName: 'richEditor',
      label: '富文本',
      formItemClass: 'col-span-3 items-baseline',
    },
  ],
  // 大屏一行显示3个，中屏一行显示2个，小屏一行显示1个
  wrapperClass: 'grid-cols-1 md:grid-cols-2 lg:grid-cols-3',
})

function onSubmit(values: BasicSubmitValues) {
  const files = values.files ?? []
  const cropImages = values.cropImage ?? []
  const unfinishedFiles = [...files, ...cropImages].filter(
    (file) => file.status !== 'success',
  )
  if (unfinishedFiles.length > 0) {
    ElMessage.error(
      `以下文件尚未上传成功：${unfinishedFiles.map((file) => file.name).join('，')}`,
    )
    return
  }
  const fileUrls = files.map(getUploadUrl)
  const cropUrls = cropImages.map(getUploadUrl)
  if ([...fileUrls, ...cropUrls].some((url) => !url)) {
    ElMessage.error('部分文件缺少上传地址，请重新上传')
    return
  }
  // 提交 URL 副本，表单继续保留文件列表供回显和删除。
  const payload = { ...values, files: fileUrls, cropImage: cropUrls }
  ElMessage.success({
    message: `form values: ${JSON.stringify(payload)}`,
  })
}

function handleSetFormValue() {
  /**
   * 设置表单值(多个)
   */
  baseFormApi.setValues({
    checkboxGroup: ['1'],
    datePicker: dayjs('2022-01-01').toDate(),
    files: [
      {
        name: 'example.png',
        status: 'success',
        uid: -1,
        url: 'https://unpkg.com/@vbenjs/static-source@0.1.7/source/logo-v1.webp',
      },
    ],
    mentions: '@afc163',
    number: 3,
    options: '1',
    password: '2',
    radioGroup: '1',
    rangePicker: [dayjs('2022-01-01').toDate(), dayjs('2022-01-02').toDate()],
    rate: 3,
    switch: true,
    timePicker: dayjs('2022-01-01 12:00:00').toDate(),
    treeSelect: 'leaf1',
    username: '1',
    richEditor: `
      <h1>Vben Tiptap</h1>
      <p>这个编辑器已经被封装在 <code>src/components/tiptap</code> 中。</p>
      <p>你可以直接在各个 app 里通过 <code>@/components/tiptap</code> 引入。</p>
      <blockquote>默认内置 StarterKit、Underline、TextAlign、Placeholder。</blockquote>
    `,
  })

  // 设置单个表单值
  baseFormApi.setFieldValue('checkbox', true)
}
</script>

<template>
  <Page
    content-class="flex flex-col gap-4"
    description="表单组件基础示例，请注意，该页面用到的参数代码会添加一些简单注释，方便理解，请仔细查看。"
    title="表单组件"
  >
    <template #description>
      <div class="text-muted-foreground">
        <p>
          表单组件基础示例，请注意，该页面用到的参数代码会添加一些简单注释，方便理解，请仔细查看。
        </p>
      </div>
    </template>
    <template #extra>
      <DocButton
        class="mb-2"
        path="/components/common-ui/vben-form"
      />
    </template>
    <Card>
      <CardHeader class="flex flex-row items-center justify-between">
        <CardTitle>基础示例</CardTitle>
        <Button @click="handleSetFormValue">设置表单值</Button>
      </CardHeader>
      <CardContent><BaseForm /></CardContent>
    </Card>
    <ElDialog
      v-model="cropVisible"
      title="裁剪图片（1:1）"
      width="560px"
      append-to-body
      destroy-on-close
      :close-on-click-modal="false"
      @close="settleCrop()"
    >
      <div class="overflow-auto">
        <VCropper
          v-if="cropSource"
          ref="cropperRef"
          :img="cropSource"
          aspect-ratio="1:1"
          :width="500"
          :height="400"
        />
      </div>
      <template #footer>
        <ElButton @click="settleCrop()">取消</ElButton>
        <ElButton
          type="primary"
          :loading="cropBusy"
          @click="confirmCrop"
        >
          确认并上传
        </ElButton>
      </template>
    </ElDialog>
  </Page>
</template>
