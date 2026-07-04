<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

import { studentGet } from '@/api/modules/student'
import PageContainer from '@/components/PageContainer.vue'
import { courseOptionLabel, type Row } from '@/utils/formatters'

const assignmentId = ref<number>()
const courses = ref<Row[]>([])
const rows = ref<Row[]>([])
const route = useRoute()
const selectedCourse = computed(() => courses.value.find((item) => item.djx_assignmentid13 === assignmentId.value))

async function load() {
  if (!assignmentId.value) return
  rows.value = await studentGet('/statistics/rank', { assignmentId: assignmentId.value })
}

onMounted(async () => {
  const result = await studentGet<Row[]>('/enrollments/mine')
  courses.value = result.filter((item) => item.djx_status13 !== 'DROPPED')
  const queryAssignmentId = Number(route.query.assignmentId)
  if (queryAssignmentId) {
    assignmentId.value = queryAssignmentId
    await load()
  }
})
</script>

<template>
  <PageContainer title="我的排名" description="选择本人已选课程后查看课程成绩排名。">
    <div class="inline-form">
      <el-select v-model="assignmentId" class="query-select" placeholder="请选择课程" @change="load">
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
    <el-alert
      v-if="assignmentId && rows.length === 0"
      class="section-alert"
      type="warning"
      :closable="false"
      title="该课程暂无成绩排名，请等待教师录入成绩后再查看。"
    />
    <el-table :data="rows" border class="data-table">
      <el-table-column prop="djx_sno13" label="学号" />
      <el-table-column prop="djx_sname13" label="姓名" />
      <el-table-column prop="djx_score13" label="成绩" />
      <el-table-column prop="djx_scorerank13" label="排名" />
    </el-table>
  </PageContainer>
</template>
