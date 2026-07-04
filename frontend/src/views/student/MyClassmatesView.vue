<script setup lang="ts">
import { onMounted, ref } from 'vue'

import { studentGet } from '@/api/modules/student'
import PageContainer from '@/components/PageContainer.vue'
import { formatGender, type Row } from '@/utils/formatters'

const rows = ref<Row[]>([])

onMounted(async () => {
  rows.value = await studentGet('/classmates')
})
</script>

<template>
  <PageContainer title="我的班级" description="查看本班学生名单。">
    <el-table :data="rows" border class="data-table">
      <el-table-column prop="sno" label="学号" />
      <el-table-column prop="sname" label="姓名" />
      <el-table-column label="性别">
        <template #default="{ row }">{{ formatGender(row.gender) }}</template>
      </el-table-column>
      <el-table-column prop="className" label="班级" />
    </el-table>
  </PageContainer>
</template>
