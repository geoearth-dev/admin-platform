import { requestClient } from '@/utils/request';
// 返回类型对应后端 FileUploadVO
interface FileUploadResult {
  url: string;
  fileName: string;
  newFileName: string;
  originalFilename: string;
}
interface UploadFileParams {
  file: File;
  onError?: (error: Error) => void;
  onProgress?: (progress: { percent: number }) => void;
  onSuccess?: (data: FileUploadResult, file: File) => void;
}
export async function upload_file({ file, onError, onProgress, onSuccess }: UploadFileParams) {
  try {
    const controller = new AbortController();
    const data = await requestClient.upload<FileUploadResult>('/upload', file, {
      // 全局超时是 12 秒，文件上传按需要单独调整。
      timeout: 120_000,
      signal: controller.signal,
      onUploadProgress(event) {
        if (event.progress !== undefined) {
          onProgress?.({
            percent: Math.round(event.progress * 100),
          });
        }
      },
    });

    onSuccess?.(data, file);
  } catch (error) {
    onError?.(error instanceof Error ? error : new Error(String(error)));
  }
}
