<script setup lang="ts">
import { computed, ref } from 'vue'

import { teacherGet } from '@/api/modules/teacher'
import PageContainer from '@/components/PageContainer.vue'
import type { PageResult } from '@/api/modules/crud'
import { formatDateTime, formatEvaluationRating, formatSemester, type Row } from '@/utils/formatters'

const loading = ref(false)
const detailLoading = ref(false)
const dialogVisible = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const assignments = ref<Row[]>([])
const evaluations = ref<Row[]>([])
const currentAssignment = ref<Row>()

const dialogTitle = computed(() => {
  if (!currentAssignment.value) {
    return '学生评价'
  }
  return [currentAssignment.value.courseName, currentAssignment.value.className, currentAssignment.value.academicYear]
    .filter(Boolean)
    .join(' / ')
})

async function loadAssignments() {
  loading.value = true
  try {
    const result = await teacherGet<PageResult<Row>>('/evaluations/assignments', {
      page: page.value,
      size: size.value
    })
    assignments.value = result.records
    total.value = result.total
  } finally {
    loading.value = false
  }
}

async function openDetail(row: Row) {
  currentAssignment.value = row
  dialogVisible.value = true
  detailLoading.value = true
  try {
    evaluations.value = await teacherGet(`/evaluations/assignments/${row.assignmentId}`)
  } finally {
    detailLoading.value = false
  }
}

loadAssignments()
</script>

<template>
  <PageContainer title="教学评价" description="按本人教学班查看学生提交的最终评价。">
    <div class="data-toolbar">
      <el-button @click="loadAssignments">刷新</el-button>
    </div>

    <el-table v-loading="loading" :data="assignments" border class="data-table">
      <el-table-column prop="courseCode" label="课程编号" />
      <el-table-column prop="courseName" label="课程" min-width="140" />
      <el-table-column prop="className" label="教学班" min-width="130" />
      <el-table-column prop="academicYear" label="学年" />
      <el-table-column label="学期">
        <template #default="{ row }">{{ formatSemester(row.semester) }}</template>
      </el-table-column>
      <el-table-column prop="evaluationCount" label="评价数" />
      <el-table-column label="平均等级">
        <template #default="{ row }">{{ row.averageRating ?? '-' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="openDetail(row)">查看评价</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pager-row">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        background
        layout="total, sizes, prev, pager, next"
        :total="total"
        @current-change="loadAssignments"
        @size-change="loadAssignments"
      />
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="840px">
      <el-table v-loading="detailLoading" :data="evaluations" border class="data-table">
        <el-table-column prop="sno" label="学号" />
        <el-table-column prop="sname" label="姓名" />
        <el-table-column label="等级">
          <template #default="{ row }">{{ formatEvaluationRating(row.rating) }}</template>
        </el-table-column>
        <el-table-column prop="comment" label="理由" min-width="220">
          <template #default="{ row }">{{ row.comment || '-' }}</template>
        </el-table-column>
        <el-table-column label="提交时间" min-width="160">
          <template #default="{ row }">{{ formatDateTime(row.evaluatedAt) }}</template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </PageContainer>
</template>
