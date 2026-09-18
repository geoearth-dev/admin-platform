import type { Component } from 'vue';
import type { LocationQueryRaw } from 'vue-router';

export type MenuBadgeType = 'dot' | 'normal';
/** 菜单管理允许保存的徽标样式。 */
export type MenuBadgeVariant = 'default' | 'destructive' | 'primary' | 'success' | 'warning';

export interface MenuBadgeOptions {
  badge?: string;
  badgeType?: MenuBadgeType;
  /** 支持预设样式；本地路由还可以传 CSS 颜色或类名。 */
  badgeVariants?: string;
}

/** 从路由生成的导航菜单，供侧栏、搜索等组件使用，不是菜单管理 DTO。 */
export interface NavigationMenu extends MenuBadgeOptions {
  activeIcon?: string;
  children?: NavigationMenu[];
  disabled?: boolean;
  icon?: Component | string;
  /** 展示标题，支持国际化 key；不是路由 name。 */
  name: string;
  order?: number;
  parent?: string;
  parents?: string[];
  /** 最终导航地址，可以是内部完整路径或外部 URL。 */
  path: string;
  query?: LocationQueryRaw;
  show?: boolean;
}
