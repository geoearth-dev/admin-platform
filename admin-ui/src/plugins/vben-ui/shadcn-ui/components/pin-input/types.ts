import type { ClassType } from '@/types';
import type { PinInputRootProps } from 'reka-ui';

interface PinInputProps extends Pick<PinInputRootProps<'text'>, 'id' | 'name' | 'required'> {
  class?: ClassType;
  /**
   * 验证码长度
   */
  codeLength?: number;
  /**
   * 发送验证码按钮文本
   */
  createText?: (countdown: number) => string;
  /**
   * 是否禁用
   */
  disabled?: boolean;
  /**
   * 自定义验证码发送逻辑
   * @returns
   */
  handleSendCode?: () => Promise<void>;
  /**
   * 发送验证码按钮loading
   */
  loading?: boolean;
  /**
   * 最大重试时间
   */
  maxTime?: number;
}

export type { PinInputProps };
