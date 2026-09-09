import type { BasicUserInfo } from '@/types'

export interface Props {
  title?: string
  userInfo: Pick<BasicUserInfo, 'avatar' | 'realName' | 'username'> | null
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
