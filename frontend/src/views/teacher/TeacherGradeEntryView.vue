<script setup lang="ts">
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'

import { teacherGet, teacherPost, teacherPut } from '@/api/modules/teacher'
import PageContainer from '@/components/PageContainer.vue'

const assignmentId = ref<number>()
const loading = ref(false)
const saving = ref(false)
const rows = ref<Record<string, unknown>[]>([])
const form = reactive({
  gradeId: undefined as number | undefined,
  enrollmentId: undefined as number | undefined,
  score: undefined as number | undefined
})

async function load() {
  if (!assignmentId.value) return
  loading.value = true
  try {
    rows.value = await teacherGet(`/grades/assignments/${assignmentId.value}`)
  } finally {
    loading.value = false
  }
}

function selectRow(row: Record<string, unknown>) {
  form.gradeId = row.djx_gradeid13 as number | undefined
  form.enrollmentId = row.djx_enrollmentid13 as number
  form.score = row.djx_score13 as number | undefined
}

async function save() {
  if (!form.enrollmentId || form.score === undefined) return
  saving.value = true
  try {
    if (form.gradeId) {
      await teacherPut(`/grades/${form.gradeId}`, { enrollmentId: form.enrollmentId, score: form.score })
    } else {
      await teacherPost('/grades', { enrollmentId: form.enrollmentId, score: form.score })
    }
    ElMessage.success('成绩已保存')
    await load()
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <PageContainer title="成绩录入" description="选择开课安排和学生后录入或修改成绩。">
    <div class="inline-form">
      <el-input-number v-model="assignmentId" :min="1" placeholder="开课ID" controls-position="right" />
      <el-button type="primary" @click="load">查询名单</el-button>
    </div>

    <el-table v-loading="loading" :data="rows" border class="data-table" @row-click="selectRow">
      <el-table-column prop="djx_enrollmentid13" label="选课ID" />
      <el-table-column prop="djx_sno13" label="学号" />
      <el-table-column prop="djx_sname13" label="姓名" />
      <el-table-column prop="djx_score13" label="成绩" />
      <el-table-column prop="djx_gradedat13" label="录入时间" min-width="170" />
    </el-table>

    <div class="edit-strip">
      <span>当前选课ID：{{ form.enrollmentId || '未选择' }}</span>
      <el-input-number v-model="form.score" :min="0" :max="100" placeholder="成绩" controls-position="right" />
      <el-button type="primary" :loading="saving" @click="save">保存成绩</el-button>
    </div>
  </PageContainer>
</template>
