<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'

import { studentGet } from '@/api/modules/student'
import PageContainer from '@/components/PageContainer.vue'
import { courseOptionLabel, type Row } from '@/utils/formatters'

const assignmentId = ref<number>()
const courses = ref<Row[]>([])
const rows = ref<Row[]>([])
const selectedCourse = computed(() => courses.value.find((item) => item.djx_assignmentid13 === assignmentId.value))

async function load() {
  if (!assignmentId.value) return
  rows.value = await studentGet('/statistics/rank', { assignmentId: assignmentId.value })
}

onMounted(async () => {
  const result = await studentGet<Row[]>('/enrollments/mine')
  courses.value = result.filter((item) => item.djx_status13 !== 'DROPPED')
})
</script>

<template>
  <PageContainer title="我的排名" description="选择本人已选课程后查看课程成绩排名。">
    <div class="inline-form">
      <el-select v-model="assignmentId" class="query-select" placeholder="请选择课程">
        <el-option
          v-for="course in courses"
          :key="String(course.djx_assignmentid13)"
          :label="courseOptionLabel(course)"
          :value="Number(course.djx_assignmentid13)"
        />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
    </div>
    <el-alert
      v-if="selectedCourse"
      class="section-alert"
      type="info"
      :closable="false"
      :title="`当前课程：${courseOptionLabel(selectedCourse)}`"
    />
    <el-table :data="rows" border class="data-table">
      <el-table-column prop="djx_sno13" label="学号" />
      <el-table-column prop="djx_sname13" label="姓名" />
      <el-table-column prop="djx_score13" label="成绩" />
      <el-table-column prop="djx_scorerank13" label="排名" />
    </el-table>
  </PageContainer>
</template>
