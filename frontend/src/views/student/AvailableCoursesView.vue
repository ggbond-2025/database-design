<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'

import { studentGet, studentPost } from '@/api/modules/student'
import PageContainer from '@/components/PageContainer.vue'
import { formatAssessmentType, formatCourseType, formatSemester, type Row } from '@/utils/formatters'

const loading = ref(false)
const rows = ref<Row[]>([])

async function load() {
  loading.value = true
  try {
    rows.value = await studentGet('/enrollments/available')
  } finally {
    loading.value = false
  }
}

async function enroll(row: Row) {
  await studentPost(`/enrollments/${row.djx_assignmentid13}`)
  ElMessage.success('选课成功')
  await load()
}

function remaining(row: Row) {
  return Number(row.djx_capacity13 ?? 0) - Number(row.djx_selectedcount13 ?? 0)
}

onMounted(load)
</script>

<template>
  <PageContainer title="可选课程" description="查看已开放选课的课程并完成选课。">
    <el-table v-loading="loading" :data="rows" border class="data-table">
      <el-table-column prop="djx_assignmentid13" label="开课ID" />
      <el-table-column prop="djx_coursecode13" label="课程编号" />
      <el-table-column prop="djx_coursename13" label="课程" />
      <el-table-column prop="djx_tname13" label="教师" />
      <el-table-column prop="djx_academicyear13" label="学年" />
      <el-table-column label="学期">
        <template #default="{ row }">{{ formatSemester(row.djx_semester13) }}</template>
      </el-table-column>
      <el-table-column label="课程类型">
        <template #default="{ row }">{{ formatCourseType(row.djx_coursetype13) }}</template>
      </el-table-column>
      <el-table-column label="考核方式">
        <template #default="{ row }">{{ formatAssessmentType(row.djx_assessmenttype13) }}</template>
      </el-table-column>
      <el-table-column prop="djx_capacity13" label="容量" />
      <el-table-column prop="djx_selectedcount13" label="已选" />
      <el-table-column label="剩余名额">
        <template #default="{ row }">{{ remaining(row) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button type="primary" link @click="enroll(row)">选课</el-button>
        </template>
      </el-table-column>
    </el-table>
  </PageContainer>
</template>
