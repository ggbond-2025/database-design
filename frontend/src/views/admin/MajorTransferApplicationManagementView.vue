<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'

import { getAdminList, getLookupOptions, putAdmin } from '@/api/modules/admin'
import type { FieldOption } from '@/components/crudTypes'
import PageContainer from '@/components/PageContainer.vue'
import { formatDateTime, type Row } from '@/utils/formatters'

interface PageResult<T> {
  records: T[]
  total: number
  page: number
  size: number
}

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const rows = ref<Row[]>([])
const classes = ref<FieldOption[]>([])
const transferApplicationEnabled = ref(true)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const keyword = ref('')
const formRef = ref<FormInstance>()
const selected = ref<Row | null>(null)
const form = reactive({
  status: 'APPROVED',
  targetClassId: undefined as number | undefined,
  reviewComment: ''
})

const targetClassOptions = computed(() => {
  const majorId = selected.value?.targetMajorId
  return classes.value.filter((item) => !majorId || Number(item.meta?.majorId) === Number(majorId))
})

async function load() {
  loading.value = true
  try {
    const result = await getAdminList<PageResult<Row>>('/admin/major-transfer-applications', {
      keyword: keyword.value,
      page: page.value,
      size: size.value
    })
    rows.value = result.records
    total.value = result.total
  } finally {
    loading.value = false
  }
}

async function loadSetting() {
  const setting = await getAdminList<Record<string, unknown>>('/admin/major-transfer-setting')
  transferApplicationEnabled.value = Boolean(setting.enabled)
}

async function toggleTransferApplicationSetting(value: string | number | boolean) {
  await putAdmin('/admin/major-transfer-setting', { enabled: Boolean(value) })
  ElMessage.success(Boolean(value) ? '已开启学生转专业申请' : '已关闭学生转专业申请')
  await loadSetting()
}

async function openReview(row: Row) {
  selected.value = row
  form.status = 'APPROVED'
  form.targetClassId = undefined
  form.reviewComment = ''
  if (!classes.value.length) {
    classes.value = await getLookupOptions('classes')
  }
  dialogVisible.value = true
}

async function saveReview() {
  const valid = await formRef.value?.validate()
  if (!valid || !selected.value) {
    return
  }
  saving.value = true
  try {
    await putAdmin(`/admin/major-transfer-applications/${selected.value.applicationId}/review`, {
      status: form.status,
      targetClassId: form.status === 'APPROVED' ? form.targetClassId : null,
      reviewComment: form.reviewComment
    })
    ElMessage.success('审核完成')
    dialogVisible.value = false
    await load()
  } finally {
    saving.value = false
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
  await Promise.all([load(), loadSetting()])
})
</script>

<template>
  <PageContainer title="转专业审核" description="审核学生转专业申请；通过时选择目标专业下的转入班级，历史课程记录保留。">
    <div class="data-toolbar">
      <el-input v-model="keyword" class="toolbar-search" clearable placeholder="学号、姓名或目标专业" />
      <el-button type="primary" @click="load">查询</el-button>
      <el-switch
        v-model="transferApplicationEnabled"
        active-text="学生转专业申请已开启"
        inactive-text="学生转专业申请已关闭"
        @change="toggleTransferApplicationSetting"
      />
    </div>

    <el-table v-loading="loading" :data="rows" border class="data-table">
      <el-table-column prop="sno" label="学号" />
      <el-table-column prop="sname" label="姓名" />
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
      <el-table-column label="生效学期" min-width="150">
        <template #default="{ row }">{{ effectiveTermLabel(row) }}</template>
      </el-table-column>
      <el-table-column prop="reason" label="申请理由" min-width="180" />
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 'PENDING'" type="primary" link @click="openReview(row)">审核</el-button>
          <span v-else>-</span>
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
        @current-change="load"
        @size-change="load"
      />
    </div>

    <el-dialog v-model="dialogVisible" title="审核转专业申请" width="520px">
      <el-form ref="formRef" :model="form" label-width="110px">
        <el-form-item label="审核结果" prop="status" required>
          <el-radio-group v-model="form.status">
            <el-radio-button label="APPROVED">通过</el-radio-button>
            <el-radio-button label="REJECTED">驳回</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.status === 'APPROVED'" label="转入班级" prop="targetClassId" required>
          <el-select v-model="form.targetClassId" class="full-control" filterable placeholder="选择目标专业下班级">
            <el-option
              v-for="item in targetClassOptions"
              :key="String(item.value)"
              :label="item.label"
              :value="Number(item.value)"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input v-model="form.reviewComment" type="textarea" :rows="3" maxlength="500" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveReview">保存</el-button>
      </template>
    </el-dialog>
  </PageContainer>
</template>
