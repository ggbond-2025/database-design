<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'

import { studentDelete, studentGet } from '@/api/modules/student'
import ConfirmDeleteButton from '@/components/ConfirmDeleteButton.vue'
import PageContainer from '@/components/PageContainer.vue'

const loading = ref(false)
const rows = ref<Record<string, unknown>[]>([])

async function load() {
  loading.value = true
  try {
    rows.value = await studentGet('/enrollments/mine')
  } finally {
    loading.value = false
  }
}

async function drop(row: Record<string, unknown>) {
  await studentDelete(`/enrollments/${row.djx_assignmentid13}`)
  ElMessage.success('退课成功')
  await load()
}

onMounted(load)
</script>

<template>
  <PageContainer title="我的选课" description="查看已选、退选和已完成课程记录。">
    <el-table v-loading="loading" :data="rows" border class="data-table">
      <el-table-column prop="djx_coursename13" label="课程" />
      <el-table-column prop="djx_tname13" label="教师" />
      <el-table-column prop="djx_status13" label="状态" />
      <el-table-column prop="djx_selectedat13" label="选课时间" min-width="170" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <ConfirmDeleteButton
            v-if="row.djx_status13 === 'SELECTED'"
            message="确认退选该课程？"
            @confirm="drop(row)"
          />
        </template>
      </el-table-column>
    </el-table>
  </PageContainer>
</template>
