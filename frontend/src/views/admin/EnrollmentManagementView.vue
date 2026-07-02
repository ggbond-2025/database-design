<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'

import { deleteAdmin, getAdminList, postAdmin } from '@/api/modules/admin'
import ConfirmDeleteButton from '@/components/ConfirmDeleteButton.vue'
import PageContainer from '@/components/PageContainer.vue'
import type { PageResult } from '@/api/modules/crud'
import { enrollmentStatusOptions } from './adminConfigs'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const rows = ref<Record<string, unknown>[]>([])
const formRef = ref<FormInstance>()
const form = reactive({
  studentId: undefined as number | undefined,
  assignmentId: undefined as number | undefined,
  status: 'SELECTED'
})

const rules: FormRules = {
  studentId: [{ required: true, message: '请填写学生ID', trigger: 'blur' }],
  assignmentId: [{ required: true, message: '请填写开课安排ID', trigger: 'blur' }],
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

onMounted(load)
</script>

<template>
  <PageContainer title="选课记录管理" description="查看学生选课状态，新增或退选异常记录。">
    <div class="data-toolbar">
      <el-button type="primary" @click="openCreate">新增/恢复选课</el-button>
      <el-button @click="load">刷新</el-button>
    </div>

    <el-table v-loading="loading" :data="rows" border class="data-table">
      <el-table-column prop="enrollmentId" label="ID" width="90" />
      <el-table-column prop="studentId" label="学生ID" />
      <el-table-column prop="assignmentId" label="开课ID" />
      <el-table-column prop="status" label="状态" />
      <el-table-column prop="selectedAt" label="选课时间" min-width="170" />
      <el-table-column prop="droppedAt" label="退课时间" min-width="170" />
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
        <el-form-item label="学生ID" prop="studentId">
          <el-input-number v-model="form.studentId" :min="1" class="full-control" controls-position="right" />
        </el-form-item>
        <el-form-item label="开课安排ID" prop="assignmentId">
          <el-input-number v-model="form.assignmentId" :min="1" class="full-control" controls-position="right" />
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
