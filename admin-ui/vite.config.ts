import { fileURLToPath, URL } from 'node:url';

import { defineConfig, loadEnv } from 'vite';
import vue from '@vitejs/plugin-vue';
import vueDevTools from 'vite-plugin-vue-devtools';
import AutoImport from 'unplugin-auto-import/vite';
import Components from 'unplugin-vue-components/vite';
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers';
import tailwindcss from '@tailwindcss/vite';

import { viteCssLayerPlugin } from './src/plugins/config/css-layer.ts';
import { viteInjectAppLoadingPlugin } from './src/plugins/config/inject-app-loading/index.ts';
import { viteTailwindReferencePlugin } from './src/plugins/config/tailwind-reference.ts';
import { viteMetadataPlugin } from './src/plugins/config/inject-metadata.ts';

// https://vite.dev/config/
export default defineConfig(async ({ command, mode }) => {
  const env = loadEnv(mode, process.cwd());
  const isBuild = command === 'build';
  const appLoadingPlugin =
    env.VITE_INJECT_APP_LOADING === 'true'
      ? await viteInjectAppLoadingPlugin(isBuild, env)
      : undefined;
  return {
    base: env.VITE_BASE,
    plugins: [
      vue(),
      vueDevTools(),
      AutoImport({
        resolvers: [ElementPlusResolver()],
      }),
      Components({
        resolvers: [ElementPlusResolver()],
      }),
      appLoadingPlugin,
      viteCssLayerPlugin({
        layerName: 'el',
        packageName: 'element-plus',
      }),
      viteTailwindReferencePlugin(),
      tailwindcss(),

      await viteMetadataPlugin(),
    ],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('./src', import.meta.url)),
      },
    },
    server: {
      proxy: {
        '/api': {
          changeOrigin: true,
          rewrite: (path: string) => path.replace(/^\/api/, ''),
          // 代理目标地址
          target: 'http://localhost:8080',
          ws: true,
        },
      },
    },
    // 打包配置
    build: {
      // https://vite.dev/config/build-options.html
      sourcemap: false, //防止浏览器开发者工具中直接调试源码
      outDir: 'dist',
      assetsDir: 'assets',
      chunkSizeWarningLimit: 2000, // 控制大文件警告
      rollupOptions: {
        output: {
          chunkFileNames: 'static/js/[name]-[hash].js',
          entryFileNames: 'static/js/[name]-[hash].js',
          assetFileNames: 'static/[ext]/[name]-[hash].[ext]',
        },
      },
    },
  };
});
