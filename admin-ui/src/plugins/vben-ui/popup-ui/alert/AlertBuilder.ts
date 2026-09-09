import type { ComponentPublicInstance, VNode } from 'vue';

import type { Recordable } from '@/types';
import type { AlertProps, BeforeCloseScope, PromptProps } from './alert.ts';

import { h, nextTick, render, shallowRef } from 'vue';

import { Input, VbenRenderContent } from '@/plugins/vben-ui/shadcn-ui';

import Alert from './alert.vue';

import { useSimpleLocale } from '@/plugins/locale/use-simple-locale/index.ts';
import { isFunction, isString } from '@/utils/inference.ts';

interface AlertEntry {
  container: HTMLElement;
  instance: ComponentPublicInstance | null;
}
const alerts = shallowRef<AlertEntry[]>([]);

const { $t } = useSimpleLocale();

export function vbenAlert(options: AlertProps): Promise<void>;
export function vbenAlert(message: string, options?: Partial<AlertProps>): Promise<void>;
export function vbenAlert(
  message: string,
  title?: string,
  options?: Partial<AlertProps>,
): Promise<void>;

export function vbenAlert(
  arg0: AlertProps | string,
  arg1?: Partial<AlertProps> | string,
  arg2?: Partial<AlertProps>,
): Promise<void> {
  return new Promise((resolve, reject) => {
    const options: AlertProps = isString(arg0)
      ? {
          content: arg0,
        }
      : { ...arg0 };
    if (arg1) {
      if (isString(arg1)) {
        options.title = arg1;
      } else if (!isString(arg1)) {
        // 如果第二个参数是对象，则合并到选项中
        Object.assign(options, arg1);
      }
    }

    if (arg2 && !isString(arg2)) {
      Object.assign(options, arg2);
    }
    // 创建容器元素
    const container = document.createElement('div');
    document.body.append(container);

    // 创建一个引用，用于在回调中访问实例
    const alertRef: AlertEntry = { container, instance: null };

    const props: AlertProps & Recordable<unknown> = {
      onClosed: (isConfirm: boolean) => {
        // 移除组件实例以及创建的所有dom（恢复页面到打开前的状态）
        // 从alerts数组中移除该实例
        alerts.value = alerts.value.filter((item) => item !== alertRef);

        // 从DOM中移除容器
        render(null, container);
        if (container.parentNode) {
          container.remove();
        }

        // 解析 Promise，传递用户操作结果
        if (isConfirm) {
          resolve();
        } else {
          reject(new Error('dialog cancelled'));
        }
      },
      ...options,
      open: true,
      title: options.title ?? $t.value('prompt'),
    };

    // 创建Alert组件的VNode
    const vnode = h(Alert, props);

    // 渲染组件到容器
    render(vnode, container);

    // 保存组件实例引用
    alertRef.instance = vnode.component?.proxy ?? null;

    // 将实例和容器添加到alerts数组中
    alerts.value.push(alertRef);
  });
}

export function vbenConfirm(options: AlertProps): Promise<void>;
export function vbenConfirm(message: string, options?: Partial<AlertProps>): Promise<void>;
export function vbenConfirm(
  message: string,
  title?: string,
  options?: Partial<AlertProps>,
): Promise<void>;

export function vbenConfirm(
  arg0: AlertProps | string,
  arg1?: Partial<AlertProps> | string,
  arg2?: Partial<AlertProps>,
): Promise<void> {
  const defaultProps: Partial<AlertProps> = {
    showCancel: true,
  };
  if (!arg1) {
    return isString(arg0) ? vbenAlert(arg0, defaultProps) : vbenAlert({ ...defaultProps, ...arg0 });
  } else if (!arg2) {
    return isString(arg1)
      ? vbenAlert(arg0 as string, arg1, defaultProps)
      : vbenAlert(arg0 as string, { ...defaultProps, ...arg1 });
  }
  return vbenAlert(arg0 as string, arg1 as string, {
    ...defaultProps,
    ...arg2,
  });
}

export async function vbenPrompt<T = unknown>(options: PromptProps<T>): Promise<T | undefined> {
  const {
    component: _component,
    componentProps: _componentProps,
    componentSlots,
    content,
    defaultValue,
    modelPropName: _modelPropName,
    ...delegated
  } = options;

  const modelValue = shallowRef<T | undefined>(defaultValue);
  let inputVNode: VNode | null = null;
  const staticContents: VNode[] = [h(VbenRenderContent, { content, renderBr: true })];

  const modelPropName = _modelPropName || 'modelValue';
  const componentProps = { ..._componentProps };

  // 每次渲染时都会重新计算的内容函数
  const contentRenderer = () => {
    const currentProps = {
      ...componentProps,
      [modelPropName]: modelValue.value,
      [`onUpdate:${modelPropName}`]: (val: T) => {
        modelValue.value = val;
      },
    };

    // 设置当前值

    // 设置更新处理函数

    // 创建输入组件
    inputVNode = h(_component || Input, currentProps, componentSlots);

    // 返回包含静态内容和输入组件的数组
    return h('div', { class: 'flex flex-col gap-2' }, [...staticContents, inputVNode]);
  };

  const props: AlertProps & Recordable<any> = {
    ...delegated,
    async beforeClose(scope: BeforeCloseScope) {
      if (delegated.beforeClose) {
        return await delegated.beforeClose({
          ...scope,
          value: modelValue.value,
        });
      }
    },
    // 使用函数形式，每次渲染都会重新计算内容
    content: contentRenderer,
    contentMasking: true,
    async onOpened() {
      await nextTick();
      // 优先调用组件主动暴露的 focus 方法
      const exposed = inputVNode?.component?.exposed;

      if (exposed && isFunction(exposed.focus)) {
        exposed.focus();
        return;
      }

      const element: unknown = inputVNode?.el;

      if (element instanceof HTMLElement) {
        const focusTarget = element.matches('input, select, textarea, button')
          ? element
          : element.querySelector<HTMLElement>('input, select, textarea, button');

        focusTarget?.focus();
      } else if (element instanceof Text || element instanceof Comment) {
        // 处理根节点为文本或注释锚点的情况
        const sibling = element.nextElementSibling;

        if (sibling instanceof HTMLElement) {
          sibling.focus();
        }
      }
    },
  };

  await vbenConfirm(props);
  return modelValue.value;
}

export function clearAllAlerts() {
  alerts.value.forEach((alert) => {
    // 从DOM中移除容器
    render(null, alert.container);
    if (alert.container.parentNode) {
      alert.container.remove();
    }
  });
  alerts.value = [];
}
