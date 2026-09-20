import { afterEach, describe, expect, it, vi } from 'vitest'
import { createRenderer, defineComponent, h, nextTick, ref } from 'vue'
import type {
  UploadFile,
  UploadRawFile,
  UploadRequestOptions,
  UploadUserFile,
} from 'element-plus'
import type { FileUploadResult, UploadFileOptions } from '@/api/common/file'
import FileUpload from '../file-upload.vue'

const mocks = vi.hoisted(() => ({
  warning: vi.fn(),
  validate: vi.fn().mockResolvedValue(true),
  id: 1000,
}))
vi.mock('element-plus', () => ({
  ElMessage: { warning: mocks.warning },
  genFileId: () => ++mocks.id,
  useFormItem: () => ({ formItem: { validate: mocks.validate } }),
}))
vi.mock('@/api/common/file', () => ({
  uploadFile: vi.fn(),
  resolveFileUrl: (path: string) => '/api' + path,
}))
type Host = { children: Host[]; parent: Host | null; text?: string }
const renderer = createRenderer<Host, Host>({
  createElement: () => ({ children: [], parent: null }),
  createText: (text) => ({ children: [], parent: null, text }),
  createComment: () => ({ children: [], parent: null }),
  insert: (child, parent) => {
    child.parent = parent
    parent.children.push(child)
  },
  remove: (child) => {
    if (child.parent)
      child.parent.children = child.parent.children.filter(
        (item) => item !== child,
      )
  },
  setText: (node, text) => {
    node.text = text
  },
  setElementText: (node, text) => {
    node.text = text
  },
  parentNode: (node) => node.parent,
  nextSibling: () => null,
  patchProp: () => undefined,
})
interface UploadBindings {
  fileList: UploadUserFile[]
  'onUpdate:fileList': (files: UploadUserFile[]) => void
  httpRequest: (options: UploadRequestOptions) => Promise<unknown>
  beforeUpload: (file: UploadRawFile) => boolean
  onRemove: (file: UploadFile, remaining: UploadFile[]) => void
}
const cleanups: (() => void)[] = []
afterEach(() => {
  cleanups.splice(0).forEach((fn) => fn())
  vi.clearAllMocks()
})
function mount(
  upload: (file: File, options: UploadFileOptions) => Promise<FileUploadResult>,
  initial = '',
) {
  let bindings: UploadBindings
  const model = ref<string | null>(initial)
  const busy: boolean[] = []
  const UploadStub = defineComponent({
    inheritAttrs: false,
    props: ['fileList', 'httpRequest', 'beforeUpload', 'onRemove'],
    setup(props, { attrs, expose }) {
      expose({ abort: vi.fn(), handleStart: vi.fn(), submit: vi.fn() })
      return () => {
        bindings = { ...props, ...attrs } as unknown as UploadBindings
        return h('div')
      }
    },
  })
  const Stub = defineComponent({
    setup(_, { slots }) {
      return () => h('div', slots.default?.())
    },
  })
  const app = renderer.createApp(
    defineComponent({
      setup() {
        return () =>
          h(FileUpload, {
            modelValue: model.value,
            'onUpdate:modelValue': (value) => {
              model.value = value ?? ''
            },
            upload,
            onUploading: (value) => busy.push(value),
          })
      },
    }),
  )
  app.component('ElUpload', UploadStub)
  app.component('ElButton', Stub)
  app.component('ElDialog', Stub)
  app.mount({ children: [], parent: null })
  cleanups.push(() => app.unmount())
  return { model, busy, bindings: () => bindings, unmount: () => app.unmount() }
}
function raw(uid: number, name = 'a.png'): UploadRawFile {
  return Object.assign(new File(['data'], name, { type: 'image/png' }), { uid })
}
function result(name: string): FileUploadResult {
  return {
    fileName: '/profile/upload/' + name,
    url: '',
    newFileName: name,
    originalFilename: name,
  }
}
function deferred<T>() {
  let resolve!: (value: T) => void
  let reject!: (reason: Error) => void
  const promise = new Promise<T>((yes, no) => {
    resolve = yes
    reject = no
  })
  return { promise, resolve, reject }
}
async function queue(view: ReturnType<typeof mount>, file: UploadRawFile) {
  view
    .bindings()
    ['onUpdate:fileList']([
      ...view.bindings().fileList,
      { uid: file.uid, name: file.name, status: 'ready', raw: file },
    ])
  await nextTick()
  return {
    run: () =>
      view
        .bindings()
        .httpRequest({
          file,
          onProgress: vi.fn(),
        } as unknown as UploadRequestOptions),
  }
}
describe('FileUpload', () => {
  it('回显已有路径，删除使用 Element Plus 回调传入的最新列表', async () => {
    const view = mount(vi.fn(), '/profile/upload/a.png,/profile/upload/b.png')
    expect(view.bindings().fileList).toHaveLength(2)
    const [first, second] = view.bindings().fileList
    expect(first?.url).toBe('/api/profile/upload/a.png')
    view.bindings().onRemove(first as UploadFile, [second as UploadFile])
    await nextTick()
    expect(view.model.value).toBe('/profile/upload/b.png')
    expect(mocks.validate).toHaveBeenCalledWith('change')
  })
  it('拒绝空文件和不允许的扩展名', () => {
    const view = mount(vi.fn())
    expect(view.bindings().beforeUpload(raw(1, 'bad.exe'))).toBe(false)
    expect(
      view
        .bindings()
        .beforeUpload(Object.assign(new File([], 'empty.png'), { uid: 2 })),
    ).toBe(false)
    expect(view.bindings().beforeUpload(raw(3, 'GOOD.PNG'))).toBe(true)
  })
  it('并发上传按选择顺序保存路径，全部结束才解除 busy', async () => {
    const first = deferred<FileUploadResult>(),
      second = deferred<FileUploadResult>()
    const upload = vi
      .fn()
      .mockReturnValueOnce(first.promise)
      .mockReturnValueOnce(second.promise)
    const view = mount(upload)
    const a = await queue(view, raw(1, 'a.png'))
    const b = await queue(view, raw(2, 'b.png'))
    const pa = a.run(),
      pb = b.run()
    second.resolve(result('b.png'))
    await pb
    await nextTick()
    expect(view.busy.at(-1)).toBe(true)
    expect(view.model.value).toBe('/profile/upload/b.png')
    first.resolve(result('a.png'))
    await pa
    await nextTick()
    expect(view.model.value).toBe('/profile/upload/a.png,/profile/upload/b.png')
    expect(view.busy.at(-1)).toBe(false)
  })
  it('失败不写入表单，其他上传仍可完成', async () => {
    const pending = deferred<FileUploadResult>()
    const upload = vi
      .fn()
      .mockRejectedValueOnce(new Error('failed'))
      .mockReturnValueOnce(pending.promise)
    const view = mount(upload)
    const a = await queue(view, raw(1))
    const b = await queue(view, raw(2))
    const pa = a.run(),
      pb = b.run()
    await expect(pa).rejects.toThrow('failed')
    expect(view.busy.at(-1)).toBe(true)
    pending.resolve(result('b.png'))
    await pb
    await nextTick()
    expect(view.model.value).toBe('/profile/upload/b.png')
    expect(view.busy.at(-1)).toBe(false)
  })
  it('外部重置取消旧请求，迟到的响应不能覆盖新表单', async () => {
    const pending = deferred<FileUploadResult>()
    let signal: AbortSignal | undefined
    const view = mount((_file, options) => {
      signal = options.signal
      return pending.promise
    })
    const queued = await queue(view, raw(1))
    const request = queued.run()
    view.model.value = '/profile/upload/existing.png'
    await nextTick()
    expect(signal?.aborted).toBe(true)
    pending.resolve(result('late.png'))
    await expect(request).rejects.toThrow('取消')
    expect(view.model.value).toBe('/profile/upload/existing.png')
  })
  it('删除上传中的文件立即取消请求', async () => {
    const pending = deferred<FileUploadResult>()
    let signal: AbortSignal | undefined
    const view = mount((_file, options) => {
      signal = options.signal
      return pending.promise
    })
    const queued = await queue(view, raw(1))
    const request = queued.run()
    view.bindings().onRemove(view.bindings().fileList[0] as UploadFile, [])
    expect(signal?.aborted).toBe(true)
    pending.resolve(result('late.png'))
    await expect(request).rejects.toThrow('取消')
    expect(view.model.value).toBe('')
  })
})
