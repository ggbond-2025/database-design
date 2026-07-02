<script setup lang="ts">
import { ref } from 'vue'

import { studentGet } from '@/api/modules/student'
import PageContainer from '@/components/PageContainer.vue'

const assignmentId = ref<number>()
const rows = ref<Record<string, unknown>[]>([])

async function load() {
  if (!assignmentId.value) return
  rows.value = await studentGet('/statistics/rank', { assignmentId: assignmentId.value })
}
</script>

<template>
  <PageContainer title="我的排名" description="输入已选课程的开课ID查看课程排名。">
    <div class="inline-form">
      <el-input-number v-model="assignmentId" :min="1" placeholder="开课ID" controls-position="right" />
      <el-button type="primary" @click="load">查询</el-button>
    </div>
    <el-table :data="rows" border class="data-table">
      <el-table-column prop="djx_sno13" label="学号" />
      <el-table-column prop="djx_sname13" label="姓名" />
      <el-table-column prop="djx_score13" label="成绩" />
      <el-table-column prop="djx_scorerank13" label="排名" />
    </el-table>
  </PageContainer>
</template>
