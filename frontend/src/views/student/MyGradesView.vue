<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'

import { studentGet } from '@/api/modules/student'
import PageContainer from '@/components/PageContainer.vue'
import { formatGradeTerm, formatSemester, type Row } from '@/utils/formatters'

const rows = ref<Row[]>([])
const groupedRows = computed(() => {
  const groups = new Map<string, Row[]>()
  for (const row of rows.value) {
    const key = `${row.djx_targetgrade13 ?? '-'}-${row.djx_targetsemester13 ?? row.djx_semester13 ?? '-'}`
    groups.set(key, [...(groups.get(key) ?? []), row])
  }
  return [...groups.entries()].map(([key, items]) => {
    const [grade, semester] = key.split('-')
    return {
      key,
      title: formatGradeTerm(grade, semester),
      rows: items
    }
  })
})

onMounted(async () => {
  rows.value = await studentGet('/grades/mine')
})
</script>

<template>
  <PageContainer title="我的成绩" description="查看本人已录入成绩和课程学分。">
    <el-empty v-if="!groupedRows.length" description="暂无成绩" />
    <el-tabs v-else type="border-card">
      <el-tab-pane v-for="group in groupedRows" :key="group.key" :label="group.title">
        <el-table :data="group.rows" border class="data-table">
          <el-table-column prop="djx_coursecode13" label="课程编号" />
          <el-table-column prop="djx_coursename13" label="课程" />
          <el-table-column prop="djx_tname13" label="教师" />
          <el-table-column prop="djx_academicyear13" label="学年" />
          <el-table-column label="教学学期">
            <template #default="{ row }">{{ formatSemester(row.djx_semester13) }}</template>
          </el-table-column>
          <el-table-column prop="djx_score13" label="成绩" />
          <el-table-column prop="djx_credit13" label="学分" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </PageContainer>
</template>
