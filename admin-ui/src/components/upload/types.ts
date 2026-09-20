import type { FileUploadResult, UploadFileOptions } from '@/api/common/file'
export interface FileUploadProps {
  mode?: 'file' | 'image'
  limit?: number
  maxSize?: number
  accept?: string
  disabled?: boolean
  drag?: boolean
  /** Optional business-specific API; shared /common/upload is the default. */
  upload?: (file: File, options: UploadFileOptions) => Promise<FileUploadResult>
}
