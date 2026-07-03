<script setup lang="ts">
import { onMounted, ref } from 'vue'

import { teacherGet } from '@/api/modules/teacher'
import PageContainer from '@/components/PageContainer.vue'
import { formatSemester, type Row } from '@/utils/formatters'

const loading = ref(false)
const rows = ref<Row[]>([])

async function load() {
  loading.value = true
  try {
    rows.value = await teacherGet('/statistics/course-averages')
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <PageContainer title="我的任课" description="查看本人课程、班级、学年学期和成绩概况。">
    <el-button class="toolbar-action" @click="load">刷新</el-button>
    <el-table v-loading="loading" :data="rows" border class="data-table">
      <el-table-column prop="djx_assignmentid13" label="开课ID" width="100" />
      <el-table-column prop="djx_coursename13" label="课程" />
      <el-table-column prop="djx_classname13" label="班级" />
      <el-table-column prop="djx_academicyear13" label="学年" />
      <el-table-column label="学期" width="100">
        <template #default="{ row }">{{ formatSemester(row.djx_semester13) }}</template>
      </el-table-column>
      <el-table-column prop="djx_averagescore13" label="平均分" />
    </el-table>
  </PageContainer>
</template>
