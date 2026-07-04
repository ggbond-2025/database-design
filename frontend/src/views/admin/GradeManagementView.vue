<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'

import { deleteAdmin, getAdminList, postAdmin, putAdmin } from '@/api/modules/admin'
import PageContainer from '@/components/PageContainer.vue'
import type { PageResult } from '@/api/modules/crud'
import {
  formatCourseType,
  formatDateTime,
  formatEnrollmentStatus,
  formatSemester,
  type Row
} from '@/utils/formatters'

const loading = ref(false)
const detailLoading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const assignments = ref<Row[]>([])
const students = ref<Row[]>([])
const currentAssignment = ref<Row>()
const editingRow = ref<Row>()
const scoreForm = reactive({
  score: undefined as number | undefined
})

const assignmentTitle = computed(() => {
  if (!currentAssignment.value) {
    return '学生成绩'
  }
  return [
    currentAssignment.value.courseName,
    currentAssignment.value.className,
    currentAssignment.value.teacherName,
    currentAssignment.value.academicYear,
    formatSemester(currentAssignment.value.semester)
  ]
    .filter(Boolean)
    .join(' / ')
})

async function loadAssignments() {
  loading.value = true
  try {
    const result = await getAdminList<PageResult<Row>>('/admin/grades/assignments', {
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
  await loadStudents()
}

async function loadStudents() {
  if (!currentAssignment.value?.assignmentId) {
    return
  }
  detailLoading.value = true
  try {
    students.value = await getAdminList<Row[]>(
      `/admin/grades/assignments/${currentAssignment.value.assignmentId}/students`
    )
  } finally {
    detailLoading.value = false
  }
}

function startEdit(row: Row) {
  editingRow.value = row
  scoreForm.score = row.score === null || row.score === undefined ? undefined : Number(row.score)
}

async function saveScore() {
  if (!editingRow.value?.enrollmentId || scoreForm.score === undefined) {
    ElMessage.warning('请填写成绩')
    return
  }
  if (scoreForm.score < 0 || scoreForm.score > 100) {
    ElMessage.warning('成绩必须在0到100之间')
    return
  }
  saving.value = true
  try {
    const payload = {
      enrollmentId: Number(editingRow.value.enrollmentId),
      score: scoreForm.score
    }
    if (editingRow.value.gradeId) {
      await putAdmin(`/admin/grades/${editingRow.value.gradeId}`, payload)
    } else {
      await postAdmin('/admin/grades', payload)
    }
    ElMessage.success('成绩已保存')
    editingRow.value = undefined
    await Promise.all([loadStudents(), loadAssignments()])
  } finally {
    saving.value = false
  }
}

async function clearScore(row: Row) {
  if (!row.gradeId) {
    return
  }
  await deleteAdmin(`/admin/grades/${row.gradeId}`)
  ElMessage.success('成绩已删除')
  await Promise.all([loadStudents(), loadAssignments()])
}

loadAssignments()
</script>

<template>
  <PageContainer title="成绩管理" description="按教学开课班级查看并维护每个学生的课程成绩。">
    <div class="data-toolbar">
      <el-button @click="loadAssignments">刷新</el-button>
    </div>

    <el-table v-loading="loading" :data="assignments" border class="data-table">
      <el-table-column prop="courseCode" label="课程编号" />
      <el-table-column prop="courseName" label="课程" min-width="140" />
      <el-table-column prop="className" label="教学班级" min-width="140" />
      <el-table-column prop="teacherName" label="任课教师" />
      <el-table-column label="课程性质">
        <template #default="{ row }">{{ formatCourseType(row.courseType) }}</template>
      </el-table-column>
      <el-table-column prop="academicYear" label="学年" />
      <el-table-column label="学期">
        <template #default="{ row }">{{ formatSemester(row.semester) }}</template>
      </el-table-column>
      <el-table-column prop="studentCount" label="学生数" />
      <el-table-column prop="gradedCount" label="已录成绩" />
      <el-table-column prop="averageScore" label="平均分">
        <template #default="{ row }">{{ row.averageScore ?? '-' }}</template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="openDetail(row)">查看学生成绩</el-button>
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

    <el-dialog v-model="dialogVisible" :title="assignmentTitle" width="860px">
      <el-table v-loading="detailLoading" :data="students" border class="data-table">
        <el-table-column prop="sno" label="学号" />
        <el-table-column prop="sname" label="姓名" />
        <el-table-column prop="className" label="行政班级" />
        <el-table-column label="选课状态">
          <template #default="{ row }">{{ formatEnrollmentStatus(row.status) }}</template>
        </el-table-column>
        <el-table-column label="成绩">
          <template #default="{ row }">
            <el-input-number
              v-if="editingRow?.enrollmentId === row.enrollmentId"
              v-model="scoreForm.score"
              :min="0"
              :max="100"
              :precision="1"
              controls-position="right"
            />
            <span v-else>{{ row.score ?? '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="录入时间" min-width="150">
          <template #default="{ row }">{{ formatDateTime(row.gradedAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <template v-if="editingRow?.enrollmentId === row.enrollmentId">
              <el-button type="primary" link :loading="saving" @click="saveScore">保存</el-button>
              <el-button link @click="editingRow = undefined">取消</el-button>
            </template>
            <template v-else>
              <el-button type="primary" link @click="startEdit(row)">
                {{ row.gradeId ? '修改' : '录入' }}
              </el-button>
              <el-button v-if="row.gradeId" type="danger" link @click="clearScore(row)">删除</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </PageContainer>
</template>
