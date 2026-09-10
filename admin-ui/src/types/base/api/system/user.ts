import type { SysRole } from './role';
import type { SysDept } from './dept';
import type { SysPost } from './post';
import type { PageParam, BaseEntity } from '../common';

/** 用户分页查询参数 */
export interface UserQueryParams extends PageParam {
  /** 创建开始日期，格式 yyyy-MM-dd */
  startTime?: string;
  /** 创建结束日期，格式 yyyy-MM-dd */
  endTime?: string;
  /** 用户名称 */
  userName?: string;
  /** 手机号码 */
  phonenumber?: string;
  /** 状态（0正常 1停用） */
  status?: string;
  /** 部门编号 */
  deptId?: number;
}

/** 角色授权用户分页查询参数 */
export interface AuthUserQueryParams extends UserQueryParams {
  /** 角色编号 */
  roleId?: number;
}

/** 用户信息 */
export interface SysUser extends BaseEntity {
  /** 用户ID */
  id: number;
  /** 部门ID */
  deptId?: number;
  /** 用户账号 */
  userName?: string;
  /** 用户昵称 */
  nickName?: string;
  /** 用户邮箱 */
  email?: string;
  /** 手机号码 */
  phonenumber?: string;
  /** 用户性别（0男 1女 2未知） */
  sex?: string;
  /** 用户头像 */
  avatar?: string;
  /** 密码 */
  password?: string;
  /** 账号状态（0正常 1停用） */
  status?: string;
  /** 部门对象 */
  dept?: SysDept;
  /** 角色对象 */
  roles?: SysRole[];
  /** 角色组 */
  roleIds?: number[];
  /** 岗位组 */
  postIds?: number[];
}

/** 注册信息 */
export interface SysRegister {
  /** 用户账号 */
  userName?: string;
  /** 密码 */
  password?: string;
  /** 验证码 */
  code?: string;
  /** 唯一标识 */
  uuid?: string;
}

/** 用户详情查询响应 */
export interface UserFormDataResult {
  /** 用户信息 */
  data?: SysUser;
  /** 用户的岗位ID列表 */
  postIds?: number[];
  /** 用户的角色ID列表 */
  roleIds?: number[];
  /** 所有角色列表 */
  roles: SysRole[];
  /** 所有岗位列表 */
  posts: SysPost[];
}

/** 用户个人资料响应 */
export interface UserProfileResult {
  /** 角色分组 */
  roleGroup: string;
  /** 岗位分组 */
  postGroup: string;
}

/** 用户头像上传响应 */
export interface UserProfileAvatarResult {
  /** 头像地址 */
  imgUrl: string;
}

/** 用户授权角色响应 */
export interface UserAuthRoleResult {
  /** 用户信息 */
  user: SysUser;
  /** 角色列表 */
  roles: SysRole[];
}
