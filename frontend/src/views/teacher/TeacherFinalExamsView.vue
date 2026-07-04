<script setup lang="ts">
import { onMounted, ref } from 'vue'

import { teacherGet } from '@/api/modules/teacher'
import PageContainer from '@/components/PageContainer.vue'
import { formatDateTime, formatSemester, type Row } from '@/utils/formatters'

const loading = ref(false)
const rows = ref<Row[]>([])

async function load() {
  loading.value = true
  try {
    rows.value = await teacherGet('/final-exams')
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <PageContainer title="期末考试" description="查看本人任课课程的期末考试时间和默认考试地点。">
    <el-button class="toolbar-action" @click="load">刷新</el-button>
    <el-table v-loading="loading" :data="rows" border class="data-table">
      <el-table-column prop="courseCode" label="课程编号" />
      <el-table-column prop="courseName" label="课程" />
      <el-table-column prop="className" label="班级" />
      <el-table-column prop="academicYear" label="学年" />
      <el-table-column label="学期">
        <template #default="{ row }">{{ formatSemester(row.semester) }}</template>
      </el-table-column>
      <el-table-column label="考试时间" min-width="170">
        <template #default="{ row }">{{ formatDateTime(row.examTime) }}</template>
      </el-table-column>
      <el-table-column prop="classroomLabel" label="考试地点" />
    </el-table>
  </PageContainer>
</template>
