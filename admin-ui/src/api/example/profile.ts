/** 个人中心演示接口：仅修改当前页面会话中的数据，刷新后恢复。 */
export interface ProfileUpdateParams {
  realName: string
  username: string
  roles: string[]
  introduction: string
}
export interface UserProfile extends ProfileUpdateParams {
  userId: number
  avatar: string
}
export interface PasswordChangeParams {
  oldPassword: string
  newPassword: string
}
export interface SecuritySettings {
  accountPassword: boolean
  securityPhone: boolean
  securityQuestion: boolean
  securityEmail: boolean
  securityMfa: boolean
}
export interface NotificationSettings {
  accountPassword: boolean
  systemMessage: boolean
  todoTask: boolean
}

let profile: UserProfile = {
  userId: 1,
  username: 'admin',
  realName: '演示管理员',
  roles: ['super'],
  introduction: '这是个人中心的演示资料，可以修改后查看效果。',
  avatar: '',
}
let password = 'Admin123'
const security: SecuritySettings = {
  accountPassword: true,
  securityPhone: true,
  securityQuestion: false,
  securityEmail: true,
  securityMfa: false,
}
const notifications: NotificationSettings = {
  accountPassword: true,
  systemMessage: true,
  todoTask: true,
}

export async function getUserProfileApi(): Promise<UserProfile> {
  return { ...profile, roles: [...profile.roles] }
}
export async function updateUserProfileApi(
  values: ProfileUpdateParams,
): Promise<UserProfile> {
  profile = { ...profile, ...values, roles: [...values.roles] }
  return getUserProfileApi()
}
export async function updateUserPasswordApi(
  values: PasswordChangeParams,
): Promise<void> {
  if (values.oldPassword !== password) throw new Error('旧密码不正确')
  if (values.newPassword === password) throw new Error('新密码不能与旧密码相同')
  if (values.newPassword.length < 5 || values.newPassword.length > 20)
    throw new Error('新密码需为 5–20 位')
  password = values.newPassword
}
export async function getSecuritySettingsApi(): Promise<SecuritySettings> {
  return { ...security }
}
export async function updateSecuritySettingsApi(
  values: Partial<SecuritySettings>,
): Promise<SecuritySettings> {
  Object.assign(security, values)
  return { ...security }
}
export async function getNotificationSettingsApi(): Promise<NotificationSettings> {
  return { ...notifications }
}
export async function updateNotificationSettingsApi(
  values: Partial<NotificationSettings>,
): Promise<NotificationSettings> {
  Object.assign(notifications, values)
  return { ...notifications }
}
