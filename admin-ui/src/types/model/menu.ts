/** 后台返回的路由信息，component 由前端转换为 Vue 组件。 */
export interface RouterVo {
  /** 唯一的路由名称 */
  name: string;
  /** 内部路由地址，顶层以 / 开头，子路由可以使用相对路径 */
  path: string;
  /** 真实的重定向地址 */
  redirect?: string;
  /** 页面组件路径或 IFrameView 等标识；纯目录、外链可以省略 */
  component?: string;
  /** 菜单与页面配置 */
  meta: MetaVo;
  /** 子路由 */
  children?: RouterVo[];
}

/** 后台返回的路由元信息。 */
export interface MetaVo {
  /** 菜单、面包屑标题 */
  title: string;
  /** 图标标识 */
  icon?: string;
  /** 是否缓存页面 */
  keepAlive: boolean;
  /** 是否隐藏菜单入口，不影响路由注册 */
  hideInMenu: boolean;
  /** 同级菜单排序号 */
  order?: number;
  /** 点击菜单时携带的固定查询参数 */
  query?: Record<string, unknown>;
  /** 外部跳转地址，与 iframeSrc 互斥 */
  link?: string;
  /** iframe 内嵌地址，与 link 互斥 */
  iframeSrc?: string;
}

// | 功能 | 返回位置 | 什么时候再加 |
// |---|---|---|
// | 详情页指定菜单高亮 | `meta.activePath` | 开始做隐藏详情、编辑页时优先考虑 |
// | 固定标签页 | `meta.affixTab` | 需要后台逐菜单配置时；只有首页固定可以先用固定配置 |
// | 固定标签排序 | `meta.affixTabOrder` | 有多个固定标签且需要配置顺序时 |
// | 隐藏子菜单 | `meta.hideChildrenInMenu` | 确实需要父菜单直达、子菜单不展示时 |
// | 隐藏标签页 | `meta.hideInTab` | 有特殊页面不应出现在标签栏时 |
// | 隐藏面包屑项 | `meta.hideInBreadcrumb` | 需要对目录或页面单独控制时 |
// | 自定义默认页面 | 顶层 `redirect` | 默认跳第一个合适的子页面不满足需求时 |
// | 内部页面新窗口打开 | `meta.openInNewWindow` | 需要独立打开报表等页面时 |
// | 徽标、激活图标 | `meta.badge`、`meta.activeIcon` 等 | 菜单确实需要这些展示效果时 |
