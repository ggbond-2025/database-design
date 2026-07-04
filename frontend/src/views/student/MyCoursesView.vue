<script setup lang="ts">
import { onMounted, ref } from 'vue'

import { studentGet } from '@/api/modules/student'
import PageContainer from '@/components/PageContainer.vue'
import { formatCourseType, formatScheduleSlots, formatSemester, type Row } from '@/utils/formatters'

const rows = ref<Row[]>([])

onMounted(async () => {
  rows.value = await studentGet('/enrollments/mine')
})
</script>

<template>
  <PageContainer title="我的课程" description="查看本学期当前账号关联的课程清单。">
    <el-table :data="rows" border class="data-table">
      <el-table-column prop="djx_coursename13" label="课程" />
      <el-table-column prop="djx_credit13" label="学分" />
      <el-table-column prop="djx_tname13" label="教师" />
      <el-table-column prop="djx_classroomlabel13" label="上课地点" />
      <el-table-column label="上课时间" min-width="220">
        <template #default="{ row }">{{ formatScheduleSlots(row) }}</template>
      </el-table-column>
      <el-table-column prop="djx_academicyear13" label="学年" />
      <el-table-column label="学期">
        <template #default="{ row }">{{ formatSemester(row.djx_semester13) }}</template>
      </el-table-column>
      <el-table-column label="类型">
        <template #default="{ row }">{{ formatCourseType(row.djx_coursetype13) }}</template>
      </el-table-column>
    </el-table>
  </PageContainer>
</template>
