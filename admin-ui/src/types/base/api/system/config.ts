import type { BaseEntity, PageParam } from '../common';

/** 参数配置分页查询参数 */
export interface ConfigQueryParams extends PageParam {
  /** 参数名称 */
  configName?: string;
  /** 参数键名 */
  configKey?: string;
  /** 系统内置 */
  configType?: '1' | '0';
  /** 创建日期起始值，格式 yyyy-MM-dd */
  beginDate?: string;
  /** 创建日期结束值，格式 yyyy-MM-dd */
  endDate?: string;
}

/** 参数配置信息 */
export interface SysConfig extends BaseEntity {
  /** 参数编号 */
  id: number;
  /** 参数名称 */
  configName: string;
  /** 参数键名 */
  configKey: string;
  /** 参数键值 */
  configValue: string;
  /** 系统内置（1是 0否） */
  configType: '1' | '0';
}

/** 对应 ConfigSaveDTO；修改时携带 id。 */
export interface ConfigSaveParams {
  id?: number;
  configName: string;
  configKey: string;
  configValue: string;
  /** 系统内置（1是 0否），保存时必填 */
  configType: '1' | '0';
  remark?: string;
}
