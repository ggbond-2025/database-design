export interface FieldOption {
  label: string
  value: string | number | boolean
}

export interface FieldConfig {
  prop: string
  label: string
  type?: 'text' | 'number' | 'boolean' | 'select' | 'date'
  required?: boolean
  table?: boolean
  form?: boolean
  options?: FieldOption[]
  lookup?: string
}

export interface CrudPageConfig {
  title: string
  description: string
  resource: string
  rowKey: string
  fields: FieldConfig[]
}
