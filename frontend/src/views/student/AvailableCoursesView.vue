<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

import { studentGet, studentPost } from '@/api/modules/student'
import PageContainer from '@/components/PageContainer.vue'
import { formatAssessmentType, formatCourseType, formatScheduleSlots, formatSemester, type Row } from '@/utils/formatters'

const loading = ref(false)
const rows = ref<Row[]>([])
const router = useRouter()
const filters = reactive({
  keyword: '',
  academicYear: '',
  semester: undefined as number | undefined
})
const filteredRows = computed(() => {
  return rows.value.filter((row) => {
    const keyword = filters.keyword.trim()
    const matchesKeyword =
      !keyword ||
      String(row.djx_coursecode13 ?? '').includes(keyword) ||
      String(row.djx_coursename13 ?? '').includes(keyword) ||
      String(row.djx_tname13 ?? '').includes(keyword)
    const matchesYear = !filters.academicYear || row.djx_academicyear13 === filters.academicYear
    const matchesSemester = !filters.semester || Number(row.djx_semester13) === filters.semester
    return matchesKeyword && matchesYear && matchesSemester
  })
})

async function load() {
  loading.value = true
  try {
    rows.value = await studentGet<Row[]>('/enrollments/available')
  } finally {
    loading.value = false
  }
}

async function enroll(row: Row) {
  await studentPost(`/enrollments/${row.djx_assignmentid13}`)
  ElMessage.success('选课成功')
  await router.push('/student/enrollments')
}

function remaining(row: Row) {
  return Number(row.djx_capacity13 ?? 0) - Number(row.djx_selectedcount13 ?? 0)
}

onMounted(load)
</script>

<template>
  <PageContainer title="可选课程" description="仅展示当前年级学期开放的专业选修课。">
    <div class="inline-form">
      <el-input v-model="filters.keyword" class="toolbar-search" clearable placeholder="课程、编号或教师" />
      <el-input v-model="filters.academicYear" class="toolbar-search" clearable placeholder="学年，例如 2023-2024" />
      <el-select v-model="filters.semester" class="query-select" clearable placeholder="学期">
        <el-option label="第1学期" :value="1" />
        <el-option label="第2学期" :value="2" />
      </el-select>
      <el-button @click="load">刷新</el-button>
    </div>
    <el-table v-loading="loading" :data="filteredRows" border class="data-table">
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
      <el-table-column label="上课时间" min-width="220">
        <template #default="{ row }">{{ formatScheduleSlots(row) }}</template>
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
