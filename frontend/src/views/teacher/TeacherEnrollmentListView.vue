<script setup lang="ts">
import { onMounted, ref } from 'vue'

import { teacherGet } from '@/api/modules/teacher'
import PageContainer from '@/components/PageContainer.vue'
import { courseOptionLabel, formatDateTime, formatEnrollmentStatus, type Row } from '@/utils/formatters'

const assignmentId = ref<number>()
const loading = ref(false)
const assignments = ref<Row[]>([])
const rows = ref<Row[]>([])

async function load() {
  if (!assignmentId.value) return
  loading.value = true
  try {
    rows.value = await teacherGet(`/enrollments/assignments/${assignmentId.value}/students`)
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  assignments.value = await teacherGet('/statistics/course-averages')
})
</script>

<template>
  <PageContainer title="选课名单" description="选择本人任课课程，查看已选、已完成或退选学生。">
    <div class="inline-form">
      <el-select v-model="assignmentId" class="query-select" placeholder="请选择任课课程">
        <el-option
          v-for="assignment in assignments"
          :key="String(assignment.djx_assignmentid13)"
          :label="courseOptionLabel(assignment)"
          :value="Number(assignment.djx_assignmentid13)"
        />
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
    </div>
    <el-table v-loading="loading" :data="rows" border class="data-table">
      <el-table-column prop="djx_sno13" label="学号" />
      <el-table-column prop="djx_sname13" label="姓名" />
      <el-table-column prop="djx_classname13" label="班级" />
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
    </el-table>
  </PageContainer>
</template>
