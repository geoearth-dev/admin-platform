import type { Editor as CoreEditor } from '@tiptap/core'
import type { Node as ProseMirrorNode } from '@tiptap/pm/model'
import type { EditorView } from '@tiptap/pm/view'
import type { Extensions } from '@tiptap/vue-3'

import type { ImageUploadOptions, VbenTiptapExtensionOptions } from './types'

import { $t } from '@/plugins/locale'

import { alert } from '@/plugins/vben-ui/popup-ui'

import Highlight from '@tiptap/extension-highlight'
import Image from '@tiptap/extension-image'
import Link from '@tiptap/extension-link'
import Placeholder from '@tiptap/extension-placeholder'
import TextAlign from '@tiptap/extension-text-align'
import { Color, TextStyle } from '@tiptap/extension-text-style'
import { Plugin, PluginKey } from '@tiptap/pm/state'
import StarterKit from '@tiptap/starter-kit'

const DEFAULT_ACCEPT = 'image/*'

function validateFile(
  file: File,
  options: ImageUploadOptions,
): string | undefined {
  if (file.size > (options.maxSize ?? 5 * 1024 * 1024)) {
    return $t('ui.tiptap.upload.fileTooLarge')
  }
  const accepted = (options.accept ?? DEFAULT_ACCEPT)
    .split(',')
    .some((value) => {
      const type = value.trim().toLowerCase()
      if (type === '*/*') return true
      if (type.startsWith('.')) return file.name.toLowerCase().endsWith(type)
      return type.endsWith('/*')
        ? file.type.startsWith(type.slice(0, -1))
        : file.type === type
    })
  return accepted ? undefined : $t('ui.tiptap.upload.fileTypeNotAllowed')
}

function handleUploadError(error: unknown, options: ImageUploadOptions): void {
  if (options.onUploadError) {
    options.onUploadError(error)
  } else {
    const message = error instanceof Error ? error.message : String(error)
    alert(message, $t('ui.tiptap.upload.uploadFailed')).catch(() => {})
  }
}

function findPlaceholderPos(doc: ProseMirrorNode, blobUrl: string): number {
  let found = -1
  doc.descendants((node: ProseMirrorNode, offset: number) => {
    if (found !== -1) return false
    if (
      node.type.name === 'image' &&
      node.attrs.src === blobUrl &&
      node.attrs['data-uploading'] === 'true'
    ) {
      found = offset
      return false
    }
    return true
  })
  return found
}

async function createUploadProcess(
  editor: CoreEditor,
  file: File,
  options: ImageUploadOptions,
  blobUrlTracker?: Set<string>,
  pos?: number,
): Promise<void> {
  const blobUrl = URL.createObjectURL(file)
  blobUrlTracker?.add(blobUrl)
  try {
    editor
      .chain()
      .insertContentAt(pos ?? editor.state.selection.from, {
        type: 'image',
        attrs: {
          src: blobUrl,
          'data-uploading': 'true',
          'data-upload-progress': 0,
        },
      })
      .run()
    const url = await options.upload(file, (percent) => {
      if (editor.isDestroyed) return
      const currentPos = findPlaceholderPos(editor.state.doc, blobUrl)
      if (currentPos === -1) return
      const node = editor.state.doc.nodeAt(currentPos)!
      editor.view.dispatch(
        editor.state.tr.setNodeMarkup(currentPos, undefined, {
          ...node.attrs,
          'data-upload-progress': percent,
        }),
      )
    })
    if (editor.isDestroyed) return
    const currentPos = findPlaceholderPos(editor.state.doc, blobUrl)
    if (currentPos === -1) return
    const node = editor.state.doc.nodeAt(currentPos)!
    editor.view.dispatch(
      editor.state.tr.setNodeMarkup(currentPos, undefined, {
        ...node.attrs,
        src: url,
        'data-uploading': null,
        'data-upload-progress': null,
      }),
    )
  } catch (error) {
    if (editor.isDestroyed) return
    const currentPos = findPlaceholderPos(editor.state.doc, blobUrl)
    if (currentPos !== -1) {
      const node = editor.state.doc.nodeAt(currentPos)!
      editor.view.dispatch(
        editor.state.tr.delete(currentPos, currentPos + node.nodeSize),
      )
    }
    handleUploadError(error, options)
  } finally {
    blobUrlTracker?.delete(blobUrl)
    URL.revokeObjectURL(blobUrl)
  }
}

function createCustomImage(
  getImageUpload: () => ImageUploadOptions | undefined,
  blobUrlTracker?: Set<string>,
) {
  return Image.extend({
    addAttributes() {
      return {
        ...this.parent?.(),
        'data-upload-progress': {
          default: null,
          parseHTML: (element) => element.dataset.uploadProgress,
          renderHTML: () => {
            return {}
          },
        },
        'data-uploading': {
          default: null,
          parseHTML: (element) => element.dataset.uploading,
          renderHTML: () => {
            return {}
          },
        },
      }
    },

    addNodeView() {
      return ({ node, HTMLAttributes }) => {
        const isUploading = node.attrs['data-uploading'] === 'true'

        if (!isUploading) {
          const img = document.createElement('img')
          for (const [key, value] of Object.entries(HTMLAttributes)) {
            if (value !== null && value !== undefined)
              img.setAttribute(key, String(value))
          }
          return { dom: img }
        }

        const wrapper = document.createElement('div')
        wrapper.className = 'vben-tiptap-upload-wrapper'

        const img = document.createElement('img')
        img.src = node.attrs.src
        img.className = 'vben-tiptap__image'
        wrapper.append(img)

        const spinner = document.createElement('div')
        spinner.className = 'vben-tiptap-upload-spinner'
        wrapper.append(spinner)

        const progressBar = document.createElement('div')
        progressBar.className = 'vben-tiptap-upload-progress'
        const progressFill = document.createElement('div')
        progressFill.className = 'vben-tiptap-upload-progress-fill'
        progressBar.append(progressFill)
        wrapper.append(progressBar)

        const progress = node.attrs['data-upload-progress']
        if (progress !== null && progress !== undefined && progress > 0) {
          spinner.style.display = 'none'
          progressBar.style.display = ''
          progressFill.style.width = `${progress}%`
        } else {
          spinner.style.display = ''
          progressBar.style.display = 'none'
        }

        return {
          dom: wrapper,
          update(updatedNode: ProseMirrorNode) {
            if (
              updatedNode.type !== node.type ||
              updatedNode.attrs['data-uploading'] !== 'true'
            ) {
              return false
            }

            if (updatedNode.attrs.src !== img.src) {
              img.src = updatedNode.attrs.src
            }

            const newProgress = updatedNode.attrs['data-upload-progress']
            if (
              newProgress !== null &&
              newProgress !== undefined &&
              newProgress > 0
            ) {
              spinner.style.display = 'none'
              progressBar.style.display = ''
              progressFill.style.width = `${newProgress}%`
            } else {
              spinner.style.display = ''
              progressBar.style.display = 'none'
            }

            return true
          },
        }
      }
    },

    addCommands() {
      return {
        ...this.parent?.(),
        uploadImage:
          () =>
          ({ editor: cmdEditor, dispatch }) => {
            const imageUpload = getImageUpload()
            if (!imageUpload || !cmdEditor.isEditable) return false
            if (!dispatch) return true
            const input = document.createElement('input')
            input.type = 'file'
            input.accept = imageUpload.accept ?? DEFAULT_ACCEPT
            input.style.display = 'none'

            input.addEventListener(
              'change',
              () => {
                const file = input.files?.[0]
                input.remove()
                if (!file || cmdEditor.isDestroyed) return

                const error = validateFile(file, imageUpload)
                if (error) {
                  handleUploadError(new Error(error), imageUpload)
                  return
                }

                createUploadProcess(
                  cmdEditor,
                  file,
                  imageUpload,
                  blobUrlTracker,
                )
              },
              { once: true },
            )
            input.addEventListener('cancel', () => input.remove(), {
              once: true,
            })

            document.body.append(input)
            input.click()
            return true
          },
      }
    },

    addProseMirrorPlugins() {
      const editor = this.editor

      return [
        new Plugin({
          key: new PluginKey('imageUploadDrop'),
          props: {
            handleDrop: (view: EditorView, event: DragEvent) => {
              const imageUpload = getImageUpload()
              if (!imageUpload || !editor.isEditable) return false
              if (!event.dataTransfer?.files.length) return false

              const imageFiles = [...event.dataTransfer.files].filter((f) =>
                f.type.startsWith('image/'),
              )
              if (imageFiles.length === 0) return false

              event.preventDefault()

              // Only support single image upload
              const file = imageFiles[0]
              if (!file) return false
              if (imageFiles.length > 1) {
                handleUploadError(
                  new Error($t('ui.tiptap.upload.onlySingleImage')),
                  imageUpload,
                )
              }

              const error = validateFile(file, imageUpload)
              if (error) {
                handleUploadError(new Error(error), imageUpload)
                return true
              }

              const coordinates = view.posAtCoords({
                left: event.clientX,
                top: event.clientY,
              })

              const pos = coordinates?.pos ?? view.state.selection.from

              createUploadProcess(
                editor,
                file,
                imageUpload,
                blobUrlTracker,
                pos,
              )
              return true
            },
          },
        }),
        new Plugin({
          key: new PluginKey('imageUploadPaste'),
          props: {
            handlePaste: (_view: EditorView, event: ClipboardEvent) => {
              const imageUpload = getImageUpload()
              if (!imageUpload || !editor.isEditable) return false
              const items = event.clipboardData?.items
              if (!items) return false

              const imageFiles: File[] = []
              for (const item of items) {
                if (item.type.startsWith('image/')) {
                  const file = item.getAsFile()
                  if (file) imageFiles.push(file)
                }
              }

              if (imageFiles.length === 0) return false

              event.preventDefault()

              const imageFile = imageFiles[0]
              if (!imageFile) return false
              if (imageFiles.length > 1) {
                handleUploadError(
                  new Error($t('ui.tiptap.upload.onlySingleImage')),
                  imageUpload,
                )
              }

              const error = validateFile(imageFile, imageUpload)
              if (error) {
                handleUploadError(new Error(error), imageUpload)
                return true
              }

              createUploadProcess(
                editor,
                imageFile,
                imageUpload,
                blobUrlTracker,
              )
              return true
            },
          },
        }),
      ]
    },
  })
}

export function createDefaultTiptapExtensions(
  options: VbenTiptapExtensionOptions = {},
): Extensions {
  return [
    StarterKit.configure({
      heading: {
        levels: [1, 2, 3, 4],
      },
      link: false,
    }),
    TextAlign.configure({
      types: ['heading', 'paragraph'],
    }),
    TextStyle,
    Color.configure({
      types: ['textStyle'],
    }),
    Highlight.configure({
      multicolor: true,
    }),
    Link.configure({
      autolink: true,
      defaultProtocol: 'https',
      enableClickSelection: true,
      openOnClick: false,
      protocols: ['mailto', { optionalSlashes: true, scheme: 'tel' }],
    }),
    createCustomImage(
      () =>
        typeof options.imageUpload === 'function'
          ? options.imageUpload()
          : options.imageUpload,
      options._blobUrlTracker,
    ).configure({
      allowBase64: true,
      HTMLAttributes: { class: 'vben-tiptap__image' },
    }),
    Placeholder.configure({
      placeholder: options.placeholder ?? $t('ui.tiptap.placeholder'),
    }),
  ]
}
