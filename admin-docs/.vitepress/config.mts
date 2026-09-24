import { defineConfig } from "vitepress";

export default defineConfig({
  title: "Admin Platform",
  description:
    "A modern admin platform built with Spring Boot, Vue 3, and TypeScript.",
  head: [["link", { rel: "icon", href: "/favicon.ico" }]],
  locales: {
    root: {
      label: "English",
      lang: "en-US",
      themeConfig: {
        nav: [
          { text: "Home", link: "/" },
          { text: "Docs", link: "/guide/", activeMatch: "/guide/" },
          { text: "Live Demo", link: "https://admin-demo.geoearth.dev" },
        ],
        sidebar: {
          "/guide/": [
            {
              text: "Getting Started",
              items: [
                { text: "Introduction", link: "/guide/" },
                { text: "Quick Start", link: "/guide/quick-start" },
              ],
            },
          ],
        },
        footer: {
          message:
            'Released under the <a href="https://github.com/geoearth-dev/admin-platform/blob/main/LICENSE">MIT License</a>.',
          copyright: "Copyright © 2026 Qiang Liu",
        },
      },
    },
    zh: {
      label: "简体中文",
      lang: "zh-CN",
      description: "基于 Spring Boot、Vue 3 和 TypeScript 的现代化管理平台。",
      themeConfig: {
        nav: [
          { text: "首页", link: "/zh/" },
          { text: "文档", link: "/zh/guide/", activeMatch: "/zh/guide/" },
          { text: "在线体验", link: "https://admin-demo.geoearth.dev" },
        ],
        sidebar: {
          "/zh/guide/": [
            {
              text: "开始使用",
              items: [
                { text: "项目介绍", link: "/zh/guide/" },
                { text: "快速开始", link: "/zh/guide/quick-start" },
              ],
            },
          ],
        },
        outline: { label: "本页目录", level: [2, 3] },
        docFooter: { prev: "上一页", next: "下一页" },
        langMenuLabel: "切换语言",
        darkModeSwitchLabel: "外观",
        darkModeSwitchTitle: "切换到深色主题",
        lightModeSwitchTitle: "切换到浅色主题",
        sidebarMenuLabel: "菜单",
        returnToTopLabel: "返回顶部",
        skipToContentLabel: "跳转到内容",
        footer: {
          message:
            '基于 <a href="https://github.com/geoearth-dev/admin-platform/blob/main/LICENSE">MIT 协议</a> 开源。',
          copyright: "Copyright © 2026 Qiang Liu",
        },
      },
    },
  },
  themeConfig: {
    logo: {
      light: "/images/logo.png",
      dark: "/images/logo-dark.png",
      alt: "Admin Platform",
    },
    socialLinks: [
      {
        icon: "github",
        link: "https://github.com/geoearth-dev/admin-platform",
      },
    ],
    outline: { level: [2, 3] },
    search: {
      provider: "local",
      options: {
        locales: {
          zh: {
            translations: {
              button: { buttonText: "搜索文档", buttonAriaLabel: "搜索文档" },
              modal: {
                noResultsText: "没有找到相关结果",
                resetButtonTitle: "清除搜索",
                backButtonTitle: "返回",
                displayDetails: "显示详细内容",
                footer: {
                  selectText: "选择",
                  selectKeyAriaLabel: "回车键",
                  navigateText: "切换",
                  navigateUpKeyAriaLabel: "向上方向键",
                  navigateDownKeyAriaLabel: "向下方向键",
                  closeText: "关闭",
                  closeKeyAriaLabel: "Esc 键",
                },
              },
            },
          },
        },
      },
    },
  },
});
