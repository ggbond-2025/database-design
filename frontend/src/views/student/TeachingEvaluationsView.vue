<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'

import { studentGet, studentPost } from '@/api/modules/student'
import PageContainer from '@/components/PageContainer.vue'
import {
  formatDateTime,
  formatEvaluationRating,
  formatSemester,
  type Row
} from '@/utils/formatters'

const ratingOptions = [
  { label: '非常满意', value: 5 },
  { label: '满意', value: 4 },
  { label: '一般', value: 3 },
  { label: '不满意', value: 2 },
  { label: '非常不满意', value: 1 }
]

const loading = ref(false)
const savingId = ref<number>()
const rows = ref<Row[]>([])
const forms = reactive<Record<string, { rating?: number; comment: string }>>({})

async function load() {
  loading.value = true
  try {
    rows.value = await studentGet('/evaluations/courses')
    for (const row of rows.value) {
      const key = String(row.enrollmentId)
      forms[key] ??= { rating: undefined, comment: '' }
    }
  } finally {
    loading.value = false
  }
}

async function submit(row: Row) {
  const enrollmentId = Number(row.enrollmentId)
  const form = forms[String(enrollmentId)]
  if (!form?.rating) {
    ElMessage.warning('请选择评价等级')
    return
  }
  savingId.value = enrollmentId
  try {
    await studentPost('/evaluations', {
      enrollmentId,
      rating: form.rating,
      comment: form.comment || null
    })
    ElMessage.success('评价已提交，提交后不可修改')
    await load()
  } finally {
    savingId.value = undefined
  }
}

onMounted(load)
</script>

<template>
  <PageContainer title="教学评价" description="对已完成课程进行一次性最终评价。">
    <el-table v-loading="loading" :data="rows" border class="data-table">
      <el-table-column prop="courseName" label="课程" min-width="140" />
      <el-table-column prop="teacherName" label="教师" />
      <el-table-column prop="className" label="教学班" min-width="130" />
      <el-table-column prop="academicYear" label="学年" />
      <el-table-column label="学期">
        <template #default="{ row }">{{ formatSemester(row.semester) }}</template>
      </el-table-column>
      <el-table-column label="评价" min-width="260">
        <template #default="{ row }">
          <template v-if="row.evaluationId">
            <el-tag type="success">{{ formatEvaluationRating(row.rating) }}</el-tag>
            <span class="inline-note">{{ row.comment || '未填写理由' }}</span>
          </template>
          <template v-else>
            <el-select
              v-model="forms[String(row.enrollmentId)].rating"
              class="rating-select"
              placeholder="选择等级"
            >
              <el-option
                v-for="option in ratingOptions"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              />
            </el-select>
            <el-input
              v-model="forms[String(row.enrollmentId)].comment"
              class="comment-input"
              maxlength="500"
              placeholder="评价理由，可不填"
            />
          </template>
        </template>
      </el-table-column>
      <el-table-column label="提交时间" min-width="160">
        <template #default="{ row }">{{ formatDateTime(row.evaluatedAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="110" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="!row.evaluationId"
            type="primary"
            link
            :loading="savingId === Number(row.enrollmentId)"
            @click="submit(row)"
          >
            提交
          </el-button>
          <span v-else>已评价</span>
        </template>
      </el-table-column>
    </el-table>
  </PageContainer>
</template>

<style scoped>
.rating-select {
  width: 120px;
  margin-right: 8px;
}

.comment-input {
  width: 220px;
}

.inline-note {
  margin-left: 8px;
}
</style>
