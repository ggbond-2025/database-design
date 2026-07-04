<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

import { studentDelete, studentGet } from '@/api/modules/student'
import ConfirmDeleteButton from '@/components/ConfirmDeleteButton.vue'
import PageContainer from '@/components/PageContainer.vue'
import { formatCourseType, formatDateTime, formatEnrollmentStatus, formatScheduleSlots, type Row } from '@/utils/formatters'

const loading = ref(false)
const rows = ref<Row[]>([])
const router = useRouter()

async function load() {
  loading.value = true
  try {
    rows.value = await studentGet('/enrollments/mine')
  } finally {
    loading.value = false
  }
}

async function drop(row: Row) {
  await studentDelete(`/enrollments/${row.djx_assignmentid13}`)
  ElMessage.success('退课成功')
  await load()
}

onMounted(load)

function openCourses() {
  router.push('/student/courses')
}

function openGrades() {
  router.push('/student/grades')
}

function openCredits() {
  router.push('/student/credits')
}

function openRank(row: Row) {
  router.push({ path: '/student/rank', query: { assignmentId: String(row.djx_assignmentid13) } })
}
</script>

<template>
  <PageContainer title="我的选课" description="仅查看本学期已选、退选和已完成课程记录。">
    <el-table v-loading="loading" :data="rows" border class="data-table">
      <el-table-column prop="djx_coursename13" label="课程" />
      <el-table-column prop="djx_tname13" label="教师" />
      <el-table-column label="课程类型">
        <template #default="{ row }">{{ formatCourseType(row.djx_coursetype13) }}</template>
      </el-table-column>
      <el-table-column label="上课时间" min-width="220">
        <template #default="{ row }">{{ formatScheduleSlots(row) }}</template>
      </el-table-column>
      <el-table-column prop="djx_classroomlabel13" label="上课地点" />
      <el-table-column label="状态">
        <template #default="{ row }">
          <el-tag :type="row.djx_status13 === 'DROPPED' ? 'info' : 'success'">
            {{ formatEnrollmentStatus(row.djx_status13) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="选课时间" min-width="170">
        <template #default="{ row }">{{ formatDateTime(row.djx_selectedat13) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <ConfirmDeleteButton
            v-if="row.djx_status13 === 'SELECTED' && row.djx_coursetype13 === 'ELECTIVE'"
            message="确认退选该课程？"
            @confirm="drop(row)"
          />
          <el-button v-if="row.djx_status13 !== 'DROPPED'" type="primary" link @click="openRank(row)">排名</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="edit-strip">
      <span>继续查看：</span>
      <el-button @click="openCourses">我的课程</el-button>
      <el-button @click="openGrades">我的成绩</el-button>
      <el-button @click="openCredits">我的学分</el-button>
    </div>
  </PageContainer>
</template>
