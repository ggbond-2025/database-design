<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'

import { getAdminList, getLookupOptions } from '@/api/modules/admin'
import PageContainer from '@/components/PageContainer.vue'
import type { FieldOption } from '@/components/crudTypes'
import { formatSemester } from '@/utils/formatters'

const loading = ref(false)
const regionRows = ref<Record<string, unknown>[]>([])
const averageRows = ref<Record<string, unknown>[]>([])
const creditRows = ref<Record<string, unknown>[]>([])
const teacherAssignmentRows = ref<Record<string, unknown>[]>([])
const classCourseRows = ref<Record<string, unknown>[]>([])
const yearScoreRows = ref<Record<string, unknown>[]>([])
const rankRows = ref<Record<string, unknown>[]>([])
const studentOptions = ref<FieldOption[]>([])
const assignmentOptions = ref<FieldOption[]>([])
const query = reactive({
  studentId: undefined as number | undefined,
  academicYear: '2023-2024',
  assignmentId: undefined as number | undefined
})

async function loadBaseStats() {
  loading.value = true
  try {
    const [regions, averages, credits, teacherAssignments, classCourses, students, assignments] = await Promise.all([
      getAdminList<Record<string, unknown>[]>('/admin/statistics/regions'),
      getAdminList<Record<string, unknown>[]>('/admin/statistics/course-averages'),
      getAdminList<Record<string, unknown>[]>('/admin/statistics/student-credits'),
      getAdminList<Record<string, unknown>[]>('/admin/statistics/teacher-assignments'),
      getAdminList<Record<string, unknown>[]>('/admin/statistics/class-courses'),
      getLookupOptions('students'),
      getLookupOptions('assignments')
    ])
    regionRows.value = regions
    averageRows.value = averages
    creditRows.value = credits
    teacherAssignmentRows.value = teacherAssignments
    classCourseRows.value = classCourses
    studentOptions.value = students
    assignmentOptions.value = assignments
  } finally {
    loading.value = false
  }
}

async function loadYearScores() {
  if (!query.studentId) return
  yearScoreRows.value = await getAdminList<Record<string, unknown>[]>('/admin/statistics/student-year-scores', {
    studentId: query.studentId,
    academicYear: query.academicYear
  })
}

async function loadRank() {
  if (!query.assignmentId) return
  rankRows.value = await getAdminList<Record<string, unknown>[]>('/admin/statistics/course-rank', {
    assignmentId: query.assignmentId
  })
}

onMounted(loadBaseStats)
</script>

<template>
  <PageContainer title="统计查询" description="基于数据库视图和存储过程查看验收所需统计结果。">
    <el-skeleton v-if="loading" :rows="6" animated />
    <template v-else>
      <div class="metric-grid">
        <div class="metric-card">
          <span>地区数量</span>
          <strong>{{ regionRows.length }}</strong>
        </div>
        <div class="metric-card">
          <span>课程统计项</span>
          <strong>{{ averageRows.length }}</strong>
        </div>
        <div class="metric-card">
          <span>学分汇总学生</span>
          <strong>{{ creditRows.length }}</strong>
        </div>
        <div class="metric-card">
          <span>教师任课记录</span>
          <strong>{{ teacherAssignmentRows.length }}</strong>
        </div>
      </div>

      <el-tabs class="stats-tabs" type="border-card">
        <el-tab-pane label="地区学生数">
          <el-table :data="regionRows" border class="data-table">
            <el-table-column prop="djx_regionname13" label="地区" />
            <el-table-column prop="djx_studentcount13" label="学生数" />
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="课程平均分">
          <el-table :data="averageRows" border class="data-table">
            <el-table-column prop="djx_coursename13" label="课程" />
            <el-table-column prop="djx_academicyear13" label="学年" />
            <el-table-column label="学期">
              <template #default="{ row }">{{ formatSemester(row.djx_semester13) }}</template>
            </el-table-column>
            <el-table-column prop="djx_averagescore13" label="平均分" />
            <el-table-column prop="djx_maxscore13" label="最高分" />
            <el-table-column prop="djx_minscore13" label="最低分" />
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="学生学分">
          <el-table :data="creditRows" border class="data-table">
            <el-table-column prop="djx_sno13" label="学号" />
            <el-table-column prop="djx_sname13" label="姓名" />
            <el-table-column prop="djx_majorname13" label="专业" />
            <el-table-column prop="djx_totalcredits13" label="已修学分" />
            <el-table-column prop="djx_graduationcredits13" label="毕业所需学分" />
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="教师任课">
          <el-table :data="teacherAssignmentRows" border class="data-table">
            <el-table-column prop="djx_tname13" label="教师" />
            <el-table-column prop="djx_coursename13" label="课程" />
            <el-table-column prop="djx_classname13" label="班级" />
            <el-table-column prop="djx_selectedcount13" label="已选人数" />
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="班级开课">
          <el-table :data="classCourseRows" border class="data-table">
            <el-table-column prop="djx_classname13" label="班级" />
            <el-table-column prop="djx_majorname13" label="专业" />
            <el-table-column prop="djx_coursename13" label="课程" />
            <el-table-column prop="djx_tname13" label="教师" />
          </el-table>
        </el-tab-pane>
      </el-tabs>

      <section class="stat-section query-section">
          <h2>学生学年成绩</h2>
          <div class="inline-form">
          <el-select v-model="query.studentId" class="query-select" filterable placeholder="请选择学生">
            <el-option
              v-for="option in studentOptions"
              :key="String(option.value)"
              :label="option.label"
              :value="Number(option.value)"
            />
          </el-select>
          <el-input v-model="query.academicYear" placeholder="学年，例如 2023-2024" />
          <el-button type="primary" @click="loadYearScores">查询</el-button>
        </div>
        <el-table :data="yearScoreRows" border>
          <el-table-column prop="djx_coursename13" label="课程" />
          <el-table-column label="学期">
            <template #default="{ row }">{{ formatSemester(row.djx_semester13) }}</template>
          </el-table-column>
          <el-table-column prop="djx_score13" label="成绩" />
          <el-table-column prop="djx_credit13" label="学分" />
          <el-table-column prop="djx_tname13" label="教师" />
        </el-table>
      </section>

      <section class="stat-section query-section">
        <h2>课程排名</h2>
        <div class="inline-form">
          <el-select v-model="query.assignmentId" class="query-select" filterable placeholder="请选择开课安排">
            <el-option
              v-for="option in assignmentOptions"
              :key="String(option.value)"
              :label="option.label"
              :value="Number(option.value)"
            />
          </el-select>
          <el-button type="primary" @click="loadRank">查询</el-button>
        </div>
        <el-table :data="rankRows" border>
          <el-table-column prop="djx_sno13" label="学号" />
          <el-table-column prop="djx_sname13" label="姓名" />
          <el-table-column prop="djx_score13" label="成绩" />
          <el-table-column prop="djx_scorerank13" label="排名" />
          <el-table-column prop="djx_courseaverage13" label="课程平均分" />
        </el-table>
      </section>
    </template>
  </PageContainer>
</template>
