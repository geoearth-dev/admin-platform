import type { UploadRequestOptions } from 'element-plus'

import { uploadFile } from '@/api/common/file'
import type { FileUploadResult } from '@/api/common/file'
export type { FileUploadResult } from '@/api/common/file'
function uploadFileRequest(
  file: File,
  signal?: AbortSignal,
  onProgress?: (progress: { percent: number }) => void,
): Promise<FileUploadResult> {
  return uploadFile(file, {
    signal,
    onProgress: (percent) => onProgress?.({ percent }),
  })
}

/** Element Plus httpRequest：由组件处理 Promise 的成功与失败，避免重复回调。 */
export function upload_file(
  options: UploadRequestOptions,
  signal?: AbortSignal,
): Promise<FileUploadResult> {
  return uploadFileRequest(options.file, signal, ({ percent }) => {
    options.onProgress(
      Object.assign(new ProgressEvent('progress'), { percent }),
    )
  })
}

interface CustomUploadOptions {
  file: File
  signal?: AbortSignal
  onProgress?: (event: { percent: number }) => void
  onSuccess?: (data: FileUploadResult, file: File) => void
  onError?: (error: Error) => void
}

/** 适配通过回调通知结果的 customRequest */
export async function uploadCustomRequest({
  file,
  signal,
  onProgress,
  onSuccess,
  onError,
}: CustomUploadOptions): Promise<void> {
  try {
    const data = await uploadFileRequest(file, signal, onProgress)

    onSuccess?.(data, file)
  } catch (error) {
    onError?.(error instanceof Error ? error : new Error(String(error)))
  }
}
