import type { FileUploadResult } from './upload'

/** 本地上传示例：返回所选文件的 data URL，刷新页面后不保存到服务器。 */
export function uploadExampleFileApi(
  file: File,
  signal?: AbortSignal,
  onProgress?: (percent: number) => void,
): Promise<FileUploadResult> {
  return new Promise((resolve, reject) => {
    signal?.throwIfAborted()
    const reader = new FileReader()
    const abort = () => reader.abort()
    signal?.addEventListener('abort', abort, { once: true })
    reader.onloadend = () => signal?.removeEventListener('abort', abort)
    reader.onabort = () => reject(new DOMException('上传已取消', 'AbortError'))
    reader.onerror = () => reject(reader.error ?? new Error('读取文件失败'))
    reader.onprogress = (event) => {
      if (event.lengthComputable)
        onProgress?.(Math.round((event.loaded / event.total) * 100))
    }
    reader.onload = () => {
      onProgress?.(100)
      resolve({
        url: String(reader.result),
        fileName: file.name,
        newFileName: file.name,
        originalFilename: file.name,
      })
    }
    reader.readAsDataURL(file)
  })
}
