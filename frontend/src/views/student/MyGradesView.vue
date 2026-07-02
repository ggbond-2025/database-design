<script setup lang="ts">
import { onMounted, ref } from 'vue'

import { studentGet } from '@/api/modules/student'
import PageContainer from '@/components/PageContainer.vue'

const rows = ref<Record<string, unknown>[]>([])

onMounted(async () => {
  rows.value = await studentGet('/grades/mine')
})
</script>

<template>
  <PageContainer title="我的成绩" description="查看本人已录入成绩和课程学分。">
    <el-table :data="rows" border class="data-table">
      <el-table-column prop="djx_coursecode13" label="课程编号" />
      <el-table-column prop="djx_coursename13" label="课程" />
      <el-table-column prop="djx_tname13" label="教师" />
      <el-table-column prop="djx_score13" label="成绩" />
      <el-table-column prop="djx_credit13" label="学分" />
    </el-table>
  </PageContainer>
</template>
