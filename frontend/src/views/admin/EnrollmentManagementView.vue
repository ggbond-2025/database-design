<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'

import { deleteAdmin, getAdminList, getLookupOptions, postAdmin, putAdmin } from '@/api/modules/admin'
import ConfirmDeleteButton from '@/components/ConfirmDeleteButton.vue'
import PageContainer from '@/components/PageContainer.vue'
import type { PageResult } from '@/api/modules/crud'
import type { FieldOption } from '@/components/crudTypes'
import { enrollmentStatusOptions } from './adminConfigs'
import { formatCourseType, formatDateTime, formatEnrollmentStatus, formatSemester } from '@/utils/formatters'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const rows = ref<Record<string, unknown>[]>([])
const studentOptions = ref<FieldOption[]>([])
const assignmentOptions = ref<FieldOption[]>([])
const enrollmentEnabled = ref(true)
const formRef = ref<FormInstance>()
const form = reactive({
  studentId: undefined as number | undefined,
  assignmentId: undefined as number | undefined,
  status: 'SELECTED'
})

const rules: FormRules = {
  studentId: [{ required: true, message: '请选择学生', trigger: 'change' }],
  assignmentId: [{ required: true, message: '请选择开课安排', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

async function load() {
  loading.value = true
  try {
    const result = await getAdminList<PageResult<Record<string, unknown>>>('/admin/enrollments', {
      page: page.value,
      size: size.value
    })
    rows.value = result.records
    total.value = result.total
  } finally {
    loading.value = false
  }
}

async function loadEnrollmentSetting() {
  const setting = await getAdminList<Record<string, unknown>>('/admin/enrollment-setting')
  enrollmentEnabled.value = Boolean(setting.enabled)
}

async function toggleEnrollmentSetting(value: string | number | boolean) {
  await putAdmin('/admin/enrollment-setting', { enabled: Boolean(value) })
  ElMessage.success(Boolean(value) ? '已开启学生选课' : '已关闭学生选课')
  await loadEnrollmentSetting()
}

function openCreate() {
  form.studentId = undefined
  form.assignmentId = undefined
  form.status = 'SELECTED'
  dialogVisible.value = true
}

async function save() {
  const valid = await formRef.value?.validate()
  if (!valid) return
  saving.value = true
  try {
    await postAdmin('/admin/enrollments', form)
    ElMessage.success('选课记录已保存')
    dialogVisible.value = false
    await load()
  } finally {
    saving.value = false
  }
}

async function drop(row: Record<string, unknown>) {
  await deleteAdmin(`/admin/enrollments/${row.enrollmentId}`)
  ElMessage.success('已退选该记录')
  await load()
}

onMounted(async () => {
  await Promise.all([
    load(),
    loadEnrollmentSetting(),
    getLookupOptions('students').then((options) => {
      studentOptions.value = options
    }),
    getLookupOptions('assignments').then((options) => {
      assignmentOptions.value = options
    })
  ])
})
</script>

<template>
  <PageContainer title="选课记录管理" description="查看学生选课状态，新增或退选异常记录。">
    <div class="data-toolbar">
      <el-button type="primary" @click="openCreate">新增/恢复选课</el-button>
      <el-button @click="load">刷新</el-button>
      <el-switch
        v-model="enrollmentEnabled"
        active-text="学生选课已开启"
        inactive-text="学生选课已关闭"
        @change="toggleEnrollmentSetting"
      />
    </div>

    <el-table v-loading="loading" :data="rows" border class="data-table">
      <el-table-column prop="sno" label="学号" />
      <el-table-column prop="sname" label="学生姓名" />
      <el-table-column prop="className" label="班级" />
      <el-table-column prop="courseName" label="课程" />
      <el-table-column prop="teacherName" label="任课教师" />
      <el-table-column label="课程类型">
        <template #default="{ row }">{{ formatCourseType(row.courseType) }}</template>
      </el-table-column>
      <el-table-column prop="academicYear" label="学年" />
      <el-table-column label="学期">
        <template #default="{ row }">{{ formatSemester(row.semester) }}</template>
      </el-table-column>
      <el-table-column label="状态">
        <template #default="{ row }">{{ formatEnrollmentStatus(row.status) }}</template>
      </el-table-column>
      <el-table-column label="选课时间" min-width="170">
        <template #default="{ row }">{{ formatDateTime(row.selectedAt) }}</template>
      </el-table-column>
      <el-table-column label="退课时间" min-width="170">
        <template #default="{ row }">{{ formatDateTime(row.droppedAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <ConfirmDeleteButton message="确认将该记录标记为退选？" @confirm="drop(row)" />
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

    <el-dialog v-model="dialogVisible" title="新增/恢复选课" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="学生" prop="studentId">
          <el-select v-model="form.studentId" class="full-control" filterable>
            <el-option
              v-for="option in studentOptions"
              :key="String(option.value)"
              :label="option.label"
              :value="Number(option.value)"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="开课安排" prop="assignmentId">
          <el-select v-model="form.assignmentId" class="full-control" filterable>
            <el-option
              v-for="option in assignmentOptions"
              :key="String(option.value)"
              :label="option.label"
              :value="Number(option.value)"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" class="full-control">
            <el-option
              v-for="option in enrollmentStatusOptions"
              :key="String(option.value)"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </PageContainer>
</template>
