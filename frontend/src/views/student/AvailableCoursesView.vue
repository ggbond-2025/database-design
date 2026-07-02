<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'

import { studentGet, studentPost } from '@/api/modules/student'
import PageContainer from '@/components/PageContainer.vue'

const loading = ref(false)
const rows = ref<Record<string, unknown>[]>([])

async function load() {
  loading.value = true
  try {
    rows.value = await studentGet('/enrollments/available')
  } finally {
    loading.value = false
  }
}

async function enroll(row: Record<string, unknown>) {
  await studentPost(`/enrollments/${row.djx_assignmentid13}`)
  ElMessage.success('选课成功')
  await load()
}

onMounted(load)
</script>

<template>
  <PageContainer title="可选课程" description="查看已开放选课的课程并完成选课。">
    <el-table v-loading="loading" :data="rows" border class="data-table">
      <el-table-column prop="djx_assignmentid13" label="开课ID" />
      <el-table-column prop="djx_coursename13" label="课程" />
      <el-table-column prop="djx_tname13" label="教师" />
      <el-table-column prop="djx_academicyear13" label="学年" />
      <el-table-column prop="djx_semester13" label="学期" />
      <el-table-column prop="djx_capacity13" label="容量" />
      <el-table-column prop="djx_selectedcount13" label="已选" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button type="primary" link @click="enroll(row)">选课</el-button>
        </template>
      </el-table-column>
    </el-table>
  </PageContainer>
</template>
