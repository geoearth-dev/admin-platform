/** Entity基类 */
export interface BaseEntity {
  /** 搜索值 */
  searchValue?: string;
  /** 创建者 */
  createBy?: string;
  /** 创建时间 */
  createTime?: string;
  /** 更新者 */
  updateBy?: string;
  /** 更新时间 */
  updateTime?: string;
  /** 备注 */
  remark?: string;
  /** 请求参数 */
  params?: Record<string, unknown>;
}
