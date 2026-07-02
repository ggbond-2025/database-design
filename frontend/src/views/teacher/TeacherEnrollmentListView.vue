<script setup lang="ts">
import { ref } from 'vue'

import { teacherGet } from '@/api/modules/teacher'
import PageContainer from '@/components/PageContainer.vue'

const assignmentId = ref<number>()
const loading = ref(false)
const rows = ref<Record<string, unknown>[]>([])

async function load() {
  if (!assignmentId.value) return
  loading.value = true
  try {
    rows.value = await teacherGet(`/enrollments/assignments/${assignmentId.value}/students`)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <PageContainer title="选课名单" description="按开课安排查看已选、已完成或退选学生。">
    <div class="inline-form">
      <el-input-number v-model="assignmentId" :min="1" placeholder="开课ID" controls-position="right" />
      <el-button type="primary" @click="load">查询</el-button>
    </div>
    <el-table v-loading="loading" :data="rows" border class="data-table">
      <el-table-column prop="djx_sno13" label="学号" />
      <el-table-column prop="djx_sname13" label="姓名" />
      <el-table-column prop="djx_classname13" label="班级" />
      <el-table-column prop="djx_status13" label="状态" />
      <el-table-column prop="djx_selectedat13" label="选课时间" min-width="170" />
    </el-table>
  </PageContainer>
</template>
