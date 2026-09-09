import type { Plugin } from 'vite';

import { resolve } from 'node:path';

import dayjs from 'dayjs';
import { readPackageJSON } from 'pkg-types';

/**
 * 读取项目 package.json，并注入前端需要的项目信息。
 * @param root 项目根目录，默认使用启动命令所在的目录。
 */
export async function viteMetadataPlugin(root = process.cwd()): Promise<Plugin> {
  const {
    author,
    dependencies = {},
    description = '',
    devDependencies = {},
    homepage = '',
    license = '',
    repository,
    version = '0.0.0',
  } = await readPackageJSON(resolve(root));

  // author 支持字符串和对象两种形式。
  const authorInfo = typeof author === 'string' ? { name: author } : author;

  const metadata = {
    authorEmail: authorInfo?.email ?? '',
    authorName: authorInfo?.name ?? '',
    authorUrl: authorInfo?.url ?? '',

    // 在 Vite 启动或构建时生成，使用运行机器的本地时间。
    buildTime: dayjs().format('YYYY-MM-DD HH:mm:ss'),

    dependencies,
    description,
    devDependencies,
    homepage,
    license,
    repositoryUrl: typeof repository === 'string' ? repository : (repository?.url ?? ''),
    version,
  };

  return {
    name: 'vite:inject-metadata',

    config() {
      return {
        define: {
          // 供 About 页面使用。
          __VBEN_ADMIN_METADATA__: JSON.stringify(metadata),

          // 供 main.ts 中的缓存命名空间使用。
          'import.meta.env.VITE_APP_VERSION': JSON.stringify(version),
        },
      };
    },
  };
}
