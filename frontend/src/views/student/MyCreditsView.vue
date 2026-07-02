<script setup lang="ts">
import { onMounted, ref } from 'vue'

import { studentGet } from '@/api/modules/student'
import PageContainer from '@/components/PageContainer.vue'

const summary = ref<Record<string, unknown>>({})

onMounted(async () => {
  summary.value = await studentGet<Record<string, unknown>>('/statistics/credits')
})
</script>

<template>
  <PageContainer title="我的学分" description="查看触发器自动汇总后的已修学分。">
    <el-descriptions border :column="2">
      <el-descriptions-item label="学号">{{ summary.djx_sno13 }}</el-descriptions-item>
      <el-descriptions-item label="姓名">{{ summary.djx_sname13 }}</el-descriptions-item>
      <el-descriptions-item label="已完成课程">{{ summary.djx_completedcourses13 }}</el-descriptions-item>
      <el-descriptions-item label="已通过课程">{{ summary.djx_passedcourses13 }}</el-descriptions-item>
      <el-descriptions-item label="已修学分">{{ summary.djx_totalcredits13 }}</el-descriptions-item>
      <el-descriptions-item label="计算学分">{{ summary.djx_calculatedcredits13 }}</el-descriptions-item>
    </el-descriptions>
  </PageContainer>
</template>
