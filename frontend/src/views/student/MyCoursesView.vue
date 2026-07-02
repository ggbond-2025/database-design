<script setup lang="ts">
import { onMounted, ref } from 'vue'

import { studentGet } from '@/api/modules/student'
import PageContainer from '@/components/PageContainer.vue'

const rows = ref<Record<string, unknown>[]>([])

onMounted(async () => {
  rows.value = await studentGet('/enrollments/mine')
})
</script>

<template>
  <PageContainer title="我的课程" description="查看当前账号关联的课程清单。">
    <el-table :data="rows" border class="data-table">
      <el-table-column prop="djx_coursename13" label="课程" />
      <el-table-column prop="djx_credit13" label="学分" />
      <el-table-column prop="djx_tname13" label="教师" />
      <el-table-column prop="djx_academicyear13" label="学年" />
      <el-table-column prop="djx_semester13" label="学期" />
      <el-table-column prop="djx_coursetype13" label="类型" />
    </el-table>
  </PageContainer>
</template>
