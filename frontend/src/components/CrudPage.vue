<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'

import { getLookupOptions } from '@/api/modules/admin'
import { crudApi } from '@/api/modules/crud'
import ConfirmDeleteButton from '@/components/ConfirmDeleteButton.vue'
import DataToolbar from '@/components/DataToolbar.vue'
import PageContainer from '@/components/PageContainer.vue'
import type { CrudPageConfig, FieldConfig, FieldOption } from '@/components/crudTypes'

const props = defineProps<{
  config: CrudPageConfig
}>()

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const keyword = ref('')
const page = ref(1)
const size = ref(10)
const total = ref(0)
const rows = ref<Record<string, unknown>[]>([])
const lookupOptions = reactive<Record<string, FieldOption[]>>({})
const formRef = ref<FormInstance>()
const form = reactive<Record<string, unknown>>({})
const editingId = ref<number | string | null>(null)

const tableFields = computed(() => props.config.fields.filter((field) => field.table !== false))
const formFields = computed(() => props.config.fields.filter((field) => field.form !== false))
const visibleFormFields = computed(() => formFields.value.filter((field) => isFieldVisible(field)))
const formGroups = computed(() => {
  const groups: { title: string; fields: FieldConfig[] }[] = []
  for (const field of visibleFormFields.value) {
    const title = field.group ?? ''
    let group = groups.find((item) => item.title === title)
    if (!group) {
      group = { title, fields: [] }
      groups.push(group)
    }
    group.fields.push(field)
  }
  return groups
})
const rules = computed<FormRules>(() => {
  return Object.fromEntries(
    visibleFormFields.value
      .filter((field) => field.required)
      .map((field) => [field.prop, [{ required: true, message: `请填写${field.label}`, trigger: 'blur' }]])
  )
})

async function load() {
  loading.value = true
  try {
    const result = await crudApi.list<Record<string, unknown>>(props.config.resource, {
      keyword: keyword.value,
      page: page.value,
      size: size.value
    })
    rows.value = result.records
    total.value = result.total
  } finally {
    loading.value = false
  }
}

async function ensureLookups() {
  const lookupNames = [...new Set(formFields.value.map((field) => field.lookup).filter(Boolean))]
  await Promise.all(
    lookupNames.map(async (name) => {
      if (!name || lookupOptions[name]) {
        return
      }
      lookupOptions[name] = await getLookupOptions(name)
    })
  )
}

async function openCreate() {
  await ensureLookups()
  editingId.value = null
  resetForm()
  dialogVisible.value = true
}

async function openEdit(row: Record<string, unknown>) {
  await ensureLookups()
  editingId.value = row[props.config.rowKey] as number | string
  resetForm(row)
  dialogVisible.value = true
}

function resetForm(row?: Record<string, unknown>) {
  for (const field of formFields.value) {
    form[field.prop] = row ? row[field.prop] : defaultValue(field)
  }
}

function defaultValue(field: FieldConfig) {
  if (field.type === 'boolean') {
    return false
  }
  if (field.type === 'number') {
    return undefined
  }
  return ''
}

async function save() {
  const valid = await formRef.value?.validate()
  if (!valid) {
    return
  }
  saving.value = true
  try {
    const payload = buildPayload()
    if (editingId.value) {
      await crudApi.update(props.config.resource, editingId.value, payload)
      ElMessage.success('修改成功')
    } else {
      await crudApi.create(props.config.resource, payload)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    await load()
  } finally {
    saving.value = false
  }
}

function buildPayload() {
  const payload: Record<string, unknown> = {}
  for (const field of formFields.value) {
    if (!isFieldVisible(field)) {
      if (field.hiddenPayload) {
        Object.assign(payload, field.hiddenPayload)
      }
      continue
    }
    if (field.payload === false) {
      continue
    }
    const value = form[field.prop]
    payload[field.prop] = value
    const relatedValues = field.relatedFields?.[String(value ?? '')]
    if (relatedValues) {
      Object.assign(payload, relatedValues)
    }
  }
  return payload
}

async function remove(row: Record<string, unknown>) {
  await crudApi.remove(props.config.resource, row[props.config.rowKey] as number | string)
  ElMessage.success('删除成功')
  await load()
}

function renderValue(row: Record<string, unknown>, field: FieldConfig) {
  const value = row[field.prop]
  if (field.type === 'boolean') {
    return value ? '是' : '否'
  }
  const option = fieldOptions(field).find((item) => item.value === value)
  return option?.label ?? String(value ?? '')
}

function fieldOptions(field: FieldConfig) {
  const options = field.lookup ? lookupOptions[field.lookup] ?? field.options ?? [] : field.options ?? []
  if (!field.optionFilter) {
    return options
  }
  return options.filter((option) => field.optionFilter?.(option, { form, lookupOptions }) ?? true)
}

function selectedOption(field: FieldConfig) {
  return fieldOptions(field).find((option) => option.value === form[field.prop])
}

function isFieldVisible(field: FieldConfig) {
  return field.visibleWhen?.({ form, option: selectedOption(field), lookupOptions }) ?? true
}

onMounted(async () => {
  await Promise.all([load(), ensureLookups()])
})
</script>

<template>
  <PageContainer :title="config.title" :description="config.description">
    <DataToolbar v-model="keyword" create-text="新增记录" @search="load" @create="openCreate">
      <slot name="toolbar-extra" :reload="load" />
    </DataToolbar>

    <el-table v-loading="loading" :data="rows" border class="data-table">
      <el-table-column
        v-for="field in tableFields"
        :key="field.prop"
        :prop="field.prop"
        :label="field.label"
        min-width="120"
      >
        <template #default="{ row }">
          <el-tag v-if="field.type === 'boolean'" :type="row[field.prop] ? 'success' : 'info'">
            {{ renderValue(row, field) }}
          </el-tag>
          <span v-else>{{ renderValue(row, field) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
          <ConfirmDeleteButton @confirm="remove(row)" />
        </template>
      </el-table-column>
    </el-table>

    <div class="pager-row">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        background
        layout="total, sizes, prev, pager, next"
        :total="total"
        @current-change="load"
        @size-change="load"
      />
    </div>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑记录' : '新增记录'" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="108px">
        <template v-for="group in formGroups" :key="group.title || 'default'">
          <el-divider v-if="group.title" content-position="left">{{ group.title }}</el-divider>
          <el-form-item v-for="field in group.fields" :key="field.prop" :label="field.label" :prop="field.prop">
            <el-input-number
              v-if="field.type === 'number'"
              v-model="form[field.prop] as number"
              :min="0"
              controls-position="right"
              class="full-control"
            />
            <el-switch v-else-if="field.type === 'boolean'" v-model="form[field.prop] as boolean" />
            <el-date-picker
              v-else-if="field.type === 'date'"
              v-model="form[field.prop] as string"
              type="date"
              value-format="YYYY-MM-DD"
              class="full-control"
            />
            <el-time-picker
              v-else-if="field.type === 'time'"
              v-model="form[field.prop] as string"
              value-format="HH:mm:ss"
              format="HH:mm"
              class="full-control"
            />
            <el-date-picker
              v-else-if="field.type === 'datetime'"
              v-model="form[field.prop] as string"
              type="datetime"
              value-format="YYYY-MM-DDTHH:mm:ss"
              format="YYYY-MM-DD HH:mm"
              class="full-control"
            />
            <el-select
              v-else-if="field.type === 'select'"
              v-model="form[field.prop]"
              class="full-control"
              filterable
              clearable
            >
              <el-option
                v-for="option in fieldOptions(field)"
                :key="String(option.value)"
                :label="option.label"
                :value="option.value"
              />
            </el-select>
            <el-input v-else v-model="form[field.prop] as string" />
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </PageContainer>
</template>
