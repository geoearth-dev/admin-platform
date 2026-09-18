import { ElMessage } from 'element-plus';
import { $t } from '@/plugins/locale';

export async function saveExcel(blob: Blob, filename: string) {
  if (blob.type.includes('json') || blob.type.includes('text/html')) {
    let message = $t('system.user.downloadFailed');

    if (blob.type.includes('json')) {
      const result: unknown = JSON.parse(await blob.text());
      if (
        typeof result === 'object' &&
        result !== null &&
        'message' in result &&
        typeof result.message === 'string'
      ) {
        message = result.message;
      }
    }

    ElMessage.error(message);
    return;
  }

  const url = URL.createObjectURL(blob);
  const anchor = document.createElement('a');

  anchor.href = url;
  anchor.download = filename;
  document.body.append(anchor);
  anchor.click();
  anchor.remove();

  setTimeout(() => URL.revokeObjectURL(url), 1000);
}
