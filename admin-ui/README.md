# Admin Platform Frontend

**English** | [简体中文](./README.zh-CN.md) | [Project README](../README.md)

The Vue 3 frontend for Admin Platform, built with TypeScript, Vite, Element Plus, and Tailwind CSS. Vben components are maintained locally in `src/plugins/vben-ui`; shared application components live in `src/components`.

## Development

Requires Node.js `^22.18.0 || >=24.12.0`. Run from `admin-ui`:

```sh
npm install
npm run dev
```

[vite.config.ts](./vite.config.ts) proxies `/api` requests to `http://localhost:8080`, removing the `/api` prefix. See the [project README](../README.md#quick-start) for backend setup.

## Structure

| Path | Purpose |
| --- | --- |
| `src/api` | Backend API calls |
| `src/assets` | Styles, icons, and images |
| `src/components` | Shared application components |
| `src/constants/admin.ts` | Project links and branding |
| `src/layout` | Application layouts |
| `src/plugins/vben-ui` | Local Vben components, forms, and layouts |
| `src/plugins/preference` | Preferences and theme state |
| `src/plugins/locale` | Translations and locale configuration |
| `src/router` | Routing and access control |
| `src/store` | Pinia stores |
| `src/utils` | Requests, caching, and utilities |
| `src/views/sys` | System pages |
| `src/views/examples`, `src/views/demos` | Component and feature examples |

The `@` alias points to `src`. The application entry point is [src/main.ts](./src/main.ts); default preferences are in [src/preference.ts](./src/preference.ts).

## Checks and Build

| Command | Purpose |
| --- | --- |
| `npm run type-check` | Check TypeScript and Vue types |
| `npm run build` | Run type checks and build to `dist` |
| `npm run build-only` | Build without a separate type check |
| `npm run preview` | Preview the build locally |
| `npx eslint src` | Check source code with ESLint |
| `npm run format` | Format files under `src` in place |

Scripts are defined in [package.json](./package.json). Review changes after formatting.

## Component Documentation

These implementation notes are currently in Chinese:

- [Layout](src/layout/basic/README.md)
- [Array forms](src/views/demos/form-array/README.md)
- [Caching](src/utils/cache/README.md)
- [Motion](src/components/motion/README.md)
- [Tables](src/components/vxe-table/README.md)
- [Charts](src/components/echarts/README.md)
