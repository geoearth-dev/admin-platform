import { requestClient } from '@/utils/request'
export interface FileUploadResult {
  url: string
  fileName: string
  newFileName: string
  originalFilename: string
}
export interface UploadFileOptions {
  signal?: AbortSignal
  onProgress?: (percent: number) => void
}
/** Uses the shared auth, response unwrapping and error handling. */
export function uploadFile(file: File, options: UploadFileOptions = {}) {
  return requestClient.upload<FileUploadResult>('/common/upload', file, {
    timeout: 120_000,
    signal: options.signal,
    onUploadProgress: (event) => {
      if (event.total)
        options.onProgress?.(Math.round((event.loaded / event.total) * 100))
    },
  })
}
/** Stored values use server paths; resolve through the configured API gateway. */
export function resolveFileUrl(path: string): string {
  if (/^https?:\/\//i.test(path)) return path
  if (!path.startsWith('/profile/')) return ''
  return `${(import.meta.env.VITE_API_BASE || '/api').replace(/\/$/, '')}${path}`
}
