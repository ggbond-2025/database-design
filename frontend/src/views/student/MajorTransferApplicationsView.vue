<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'

import { getStudentLookupOptions, studentGet, studentPost } from '@/api/modules/student'
import type { FieldOption } from '@/components/crudTypes'
import PageContainer from '@/components/PageContainer.vue'
import { formatDateTime, type Row } from '@/utils/formatters'

const loading = ref(false)
const submitting = ref(false)
const rows = ref<Row[]>([])
const majors = ref<FieldOption[]>([])
const transferApplicationEnabled = ref(true)
const transferApplicationClosedMessage = '当前转专业申请未开放'
const form = reactive({
  targetMajorId: undefined as number | undefined,
  reason: ''
})

async function load() {
  loading.value = true
  try {
    rows.value = await studentGet('/major-transfer-applications')
  } finally {
    loading.value = false
  }
}

async function loadSetting() {
  const setting = await studentGet<Record<string, unknown>>('/major-transfer-setting')
  transferApplicationEnabled.value = Boolean(setting.enabled)
}

async function submit() {
  if (!transferApplicationEnabled.value) {
    ElMessage.warning(transferApplicationClosedMessage)
    return
  }
  if (!form.targetMajorId) {
    ElMessage.warning('请选择目标专业')
    return
  }
  submitting.value = true
  try {
    await studentPost('/major-transfer-applications', {
      targetMajorId: form.targetMajorId,
      reason: form.reason
    })
    ElMessage.success('申请已提交')
    form.targetMajorId = undefined
    form.reason = ''
    await load()
  } finally {
    submitting.value = false
  }
}

function statusLabel(status: unknown) {
  const labels: Record<string, string> = {
    PENDING: '待审核',
    APPROVED: '已通过',
    REJECTED: '已驳回'
  }
  return labels[String(status)] ?? String(status ?? '-')
}

function statusType(status: unknown) {
  if (status === 'APPROVED') return 'success'
  if (status === 'REJECTED') return 'danger'
  return 'warning'
}

function effectiveTermLabel(row: Row) {
  if (!row.effectiveAcademicYear || !row.effectiveSemester) {
    return '-'
  }
  return `${row.effectiveAcademicYear} 第${row.effectiveSemester}学期`
}

onMounted(async () => {
  await Promise.all([
    loadSetting(),
    load(),
    getStudentLookupOptions('majors').then((result) => {
      majors.value = result
    })
  ])
})
</script>

<template>
  <PageContainer title="转专业申请" description="提交想要转入的专业，由管理员审核；已修读课程和成绩会继续保留。">
    <el-alert
      v-if="!transferApplicationEnabled"
      :title="transferApplicationClosedMessage"
      description="管理员暂未开放学生转专业申请，历史申请记录仍可查看。"
      type="warning"
      show-icon
      :closable="false"
    />
    <el-form v-else class="query-card" label-width="96px">
      <el-form-item label="目标专业" required>
        <el-select v-model="form.targetMajorId" class="full-control" filterable placeholder="选择目标专业">
          <el-option v-for="item in majors" :key="String(item.value)" :label="item.label" :value="Number(item.value)" />
        </el-select>
      </el-form-item>
      <el-form-item label="申请理由">
        <el-input v-model="form.reason" type="textarea" :rows="3" maxlength="500" show-word-limit />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="submitting" @click="submit">提交申请</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="rows" border class="data-table">
      <el-table-column prop="fromMajorName" label="原专业" />
      <el-table-column prop="targetMajorName" label="目标专业" />
      <el-table-column prop="targetClassName" label="转入班级" />
      <el-table-column label="状态">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="申请时间" min-width="170">
        <template #default="{ row }">{{ formatDateTime(row.appliedAt) }}</template>
      </el-table-column>
      <el-table-column label="审核时间" min-width="170">
        <template #default="{ row }">{{ formatDateTime(row.reviewedAt) }}</template>
      </el-table-column>
      <el-table-column label="生效学期" min-width="150">
        <template #default="{ row }">{{ effectiveTermLabel(row) }}</template>
      </el-table-column>
      <el-table-column prop="reviewComment" label="审核意见" min-width="180" />
    </el-table>
  </PageContainer>
</template>
