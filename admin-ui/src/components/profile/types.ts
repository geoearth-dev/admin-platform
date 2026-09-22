import type { UserProfileResult } from '@/types/base/api/system/user'

export interface Props {
  title?: string
  userInfo: Pick<UserProfileResult, 'avatar' | 'nickName' | 'userName'> | null
  tabs: {
    label: string
    value: string
  }[]
}

export interface FormSchemaItem {
  description: string
  fieldName: string
  label: string
  value: boolean
}

export interface SettingProps {
  disabled?: boolean
  formSchema: FormSchemaItem[]
}

export interface SettingChange {
  fieldName: string
  value: boolean
}
