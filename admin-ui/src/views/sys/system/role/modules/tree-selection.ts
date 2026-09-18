import type { TreeSelect } from '@/types/base/api/common';

/** 后台关联模式仅返回末级选中项；补齐完全选中的父节点用于回显。 */
export function restoreChecked(nodes: TreeSelect[], ids: number[]): number[] {
  const checked = new Set(ids);
  function visit(node: TreeSelect): boolean {
    const children = (node.children ?? []).map(visit);
    if (children.length && children.every(Boolean)) checked.add(node.id);
    return checked.has(node.id);
  }
  nodes.forEach(visit);
  return [...checked];
}

/** 保存关联选择时包含半选祖先，否则按钮所属菜单目录会丢失。 */
export function withParents(nodes: TreeSelect[], ids: number[]): number[] {
  const checked = new Set(ids);
  function visit(node: TreeSelect): boolean {
    const children = (node.children ?? []).map(visit);
    if (children.some(Boolean)) checked.add(node.id);
    return checked.has(node.id);
  }
  nodes.forEach(visit);
  return [...checked];
}
