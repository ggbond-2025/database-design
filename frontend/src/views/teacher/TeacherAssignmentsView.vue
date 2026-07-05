<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'

import { teacherGet } from '@/api/modules/teacher'
import PageContainer from '@/components/PageContainer.vue'
import { formatDateTime, formatSemester, type Row } from '@/utils/formatters'
import { filterTeacherAssignments, getAcademicYearOptions } from './teacherAssignmentFilters'

const loading = ref(false)
const gradeLoading = ref(false)
const gradeDialogVisible = ref(false)
const rows = ref<Row[]>([])
const gradeRows = ref<Row[]>([])
const selectedAssignment = ref<Row>()
const router = useRouter()
const filters = reactive({
  academicYear: '',
  semester: undefined as number | undefined
})
const academicYearOptions = computed(() => getAcademicYearOptions(rows.value))
const filteredRows = computed(() => filterTeacherAssignments(rows.value, filters))
const gradeDialogTitle = computed(() => {
  if (!selectedAssignment.value) {
    return '学生成绩'
  }
  return `${selectedAssignment.value.djx_coursename13} / ${selectedAssignment.value.djx_classname13} 学生成绩`
})

async function load() {
  loading.value = true
  try {
    rows.value = await teacherGet('/statistics/course-averages')
  } finally {
    loading.value = false
  }
}

onMounted(load)

function resetFilters() {
  filters.academicYear = ''
  filters.semester = undefined
}

function openStudents(row: Row) {
  router.push({ path: '/teacher/enrollments', query: { assignmentId: String(row.djx_assignmentid13) } })
}

async function openGradeDetail(row: Row) {
  selectedAssignment.value = row
  gradeDialogVisible.value = true
  gradeLoading.value = true
  try {
    gradeRows.value = await teacherGet(`/grades/assignments/${row.djx_assignmentid13}`)
  } finally {
    gradeLoading.value = false
  }
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
    <div class="inline-form">
      <el-select v-model="filters.academicYear" class="query-select" clearable placeholder="全部学年">
        <el-option
          v-for="academicYear in academicYearOptions"
          :key="academicYear"
          :label="academicYear"
          :value="academicYear"
        />
      </el-select>
      <el-select v-model="filters.semester" class="query-select" clearable placeholder="全部学期">
        <el-option label="第1学期" :value="1" />
        <el-option label="第2学期" :value="2" />
      </el-select>
      <el-button @click="resetFilters">重置</el-button>
      <el-button type="primary" @click="load">刷新</el-button>
    </div>

    <el-table v-loading="loading" :data="filteredRows" border class="data-table">
      <el-table-column prop="djx_assignmentid13" label="开课ID" width="100" />
      <el-table-column prop="djx_coursecode13" label="课程编号" width="120" />
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
      <el-table-column prop="djx_studentcount13" label="已评分人数" width="110" />
      <el-table-column prop="djx_averagescore13" label="平均分" width="100" />
      <el-table-column label="操作" width="300" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="openStudents(row)">名单</el-button>
          <el-button type="primary" link @click="openGradeDetail(row)">看成绩</el-button>
          <el-button type="primary" link @click="openGrades(row)">录成绩</el-button>
          <el-button type="primary" link @click="openRank(row)">排名</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="gradeDialogVisible" :title="gradeDialogTitle" width="760px">
      <el-table v-loading="gradeLoading" :data="gradeRows" border>
        <el-table-column prop="djx_enrollmentid13" label="选课ID" width="100" />
        <el-table-column prop="djx_sno13" label="学号" />
        <el-table-column prop="djx_sname13" label="姓名" />
        <el-table-column prop="djx_score13" label="成绩" width="100" />
        <el-table-column label="录入时间" min-width="170">
          <template #default="{ row }">{{ formatDateTime(row.djx_gradedat13) }}</template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </PageContainer>
</template>
