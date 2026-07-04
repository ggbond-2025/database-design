export interface FieldOption {
  label: string
  value: string | number | boolean
  meta?: Record<string, unknown>
}

export interface FieldVisibilityContext {
  form: Record<string, unknown>
  option?: FieldOption
  lookupOptions: Record<string, FieldOption[]>
}

export interface FieldConfig {
  prop: string
  label: string
  type?: 'text' | 'number' | 'boolean' | 'select' | 'date' | 'time' | 'datetime'
  required?: boolean
  table?: boolean
  form?: boolean
  options?: FieldOption[]
  lookup?: string
  relatedFields?: Record<string, Record<string, string | number | boolean | null>>
  group?: string
  visibleWhen?: (context: FieldVisibilityContext) => boolean
  optionFilter?: (option: FieldOption, context: FieldVisibilityContext) => boolean
  hiddenPayload?: Record<string, string | number | boolean | null>
  payload?: boolean
}

export interface CrudPageConfig {
  title: string
  description: string
  resource: string
  rowKey: string
  fields: FieldConfig[]
}
