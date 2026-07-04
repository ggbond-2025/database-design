<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

import { teacherGet } from '@/api/modules/teacher'
import PageContainer from '@/components/PageContainer.vue'
import { formatSemester, type Row } from '@/utils/formatters'

const loading = ref(false)
const rows = ref<Row[]>([])
const router = useRouter()

async function load() {
  loading.value = true
  try {
    rows.value = await teacherGet('/statistics/course-averages')
  } finally {
    loading.value = false
  }
}

onMounted(load)

function openStudents(row: Row) {
  router.push({ path: '/teacher/enrollments', query: { assignmentId: String(row.djx_assignmentid13) } })
}

function openGrades(row: Row) {
  router.push({ path: '/teacher/grades', query: { assignmentId: String(row.djx_assignmentid13) } })
}

function openRank(row: Row) {
  router.push({ path: '/teacher/statistics', query: { assignmentId: String(row.djx_assignmentid13) } })
}
</script>

<template>
  <PageContainer title="我的任课" description="查看本人课程、班级、学年学期和成绩概况。">
    <el-button class="toolbar-action" @click="load">刷新</el-button>
    <el-table v-loading="loading" :data="rows" border class="data-table">
      <el-table-column prop="djx_assignmentid13" label="开课ID" width="100" />
      <el-table-column prop="djx_coursename13" label="课程" />
      <el-table-column prop="djx_classname13" label="班级" />
      <el-table-column label="上课地点">
        <template #default="{ row }">
          {{ [row.djx_buildingname13, row.djx_classroomname13].filter(Boolean).join(' ') || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="djx_academicyear13" label="学年" />
      <el-table-column label="学期" width="100">
        <template #default="{ row }">{{ formatSemester(row.djx_semester13) }}</template>
      </el-table-column>
      <el-table-column prop="djx_averagescore13" label="平均分" />
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="openStudents(row)">名单</el-button>
          <el-button type="primary" link @click="openGrades(row)">录成绩</el-button>
          <el-button type="primary" link @click="openRank(row)">排名</el-button>
        </template>
      </el-table-column>
    </el-table>
  </PageContainer>
</template>
