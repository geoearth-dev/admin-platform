import { existsSync, readFileSync } from 'node:fs'
import { fileURLToPath, URL } from 'node:url'
import { compileScript, parse } from '@vue/compiler-sfc'
import ts from 'typescript'
import { defineConfig } from 'vitest/config'
// Client render functions with Vue's in-memory renderer: no browser or DOM dependency.
export default defineConfig({
  plugins: [
    {
      name: 'upload-test-vue',
      async transform(source, id) {
        if (!id.endsWith('.vue')) return
        const { descriptor } = parse(source, { filename: id })
        const script = compileScript(descriptor, {
          id,
          inlineTemplate: true,
          fs: {
            fileExists: existsSync,
            readFile: (path) => readFileSync(path, 'utf8'),
          },
        })
        return {
          code: ts.transpileModule(script.content, {
            compilerOptions: {
              target: ts.ScriptTarget.ES2022,
              module: ts.ModuleKind.ESNext,
            },
          }).outputText,
          map: null,
        }
      },
    },
  ],
  resolve: { alias: { '@': fileURLToPath(new URL('./src', import.meta.url)) } },
  test: {
    environment: 'node',
    include: ['src/components/upload/__tests__/*.test.ts'],
  },
})
