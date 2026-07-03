<script setup lang="ts">
import { onMounted, ref } from 'vue'

import { teacherGet } from '@/api/modules/teacher'
import PageContainer from '@/components/PageContainer.vue'
import { courseOptionLabel, type Row } from '@/utils/formatters'

const loading = ref(false)
const averages = ref<Row[]>([])
const ranks = ref<Row[]>([])
const assignmentId = ref<number>()

async function loadAverages() {
  loading.value = true
  try {
    averages.value = await teacherGet('/statistics/course-averages')
  } finally {
    loading.value = false
  }
}

async function loadRank() {
  if (!assignmentId.value) return
  ranks.value = await teacherGet('/statistics/course-rank', { assignmentId: assignmentId.value })
}

onMounted(loadAverages)
</script>

<template>
  <PageContainer title="课程统计" description="查看本人任课课程平均分和指定课程排名。">
    <section class="stat-section">
      <h2>课程平均分</h2>
      <el-table v-loading="loading" :data="averages" border>
        <el-table-column prop="djx_assignmentid13" label="开课ID" />
        <el-table-column prop="djx_coursename13" label="课程" />
        <el-table-column prop="djx_averagescore13" label="平均分" />
        <el-table-column prop="djx_studentcount13" label="人数" />
      </el-table>
    </section>
    <section class="stat-section query-section">
      <h2>课程排名</h2>
      <div class="inline-form">
        <el-select v-model="assignmentId" class="query-select" placeholder="请选择任课课程">
          <el-option
            v-for="assignment in averages"
            :key="String(assignment.djx_assignmentid13)"
            :label="courseOptionLabel(assignment)"
            :value="Number(assignment.djx_assignmentid13)"
          />
        </el-select>
        <el-button type="primary" @click="loadRank">查询排名</el-button>
      </div>
      <el-table :data="ranks" border>
        <el-table-column prop="djx_sno13" label="学号" />
        <el-table-column prop="djx_sname13" label="姓名" />
        <el-table-column prop="djx_score13" label="成绩" />
        <el-table-column prop="djx_scorerank13" label="排名" />
      </el-table>
    </section>
  </PageContainer>
</template>
