<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'

import { studentGet } from '@/api/modules/student'
import PageContainer from '@/components/PageContainer.vue'
import { formatCourseType, formatTime, formatWeekday, type Row } from '@/utils/formatters'

interface ScheduleResponse {
  firstHalf: Record<string, Row[]>
  secondHalf: Record<string, Row[]>
}

const loading = ref(false)
const schedule = ref<ScheduleResponse>({
  firstHalf: {},
  secondHalf: {}
})
const activeHalf = ref<'firstHalf' | 'secondHalf'>('firstHalf')
const weekdays = [1, 2, 3, 4, 5]
const timeSlots = [
  { label: '第1-2节', start: '08:00', end: '09:40' },
  { label: '第3-4节', start: '09:55', end: '11:35' },
  { label: '第5-6节', start: '13:30', end: '15:10' },
  { label: '第7-8节', start: '15:25', end: '17:05' },
  { label: '第9-10节', start: '18:30', end: '20:10' }
]

const currentRows = computed(() => schedule.value[activeHalf.value])

async function load() {
  loading.value = true
  try {
    schedule.value = await studentGet<ScheduleResponse>('/schedule')
  } finally {
    loading.value = false
  }
}

function weekdayRows(weekday: number) {
  return currentRows.value[String(weekday)] ?? currentRows.value[weekday] ?? []
}

function slotRows(weekday: number, slot: (typeof timeSlots)[number]) {
  return weekdayRows(weekday).filter(
    (item) => formatTime(item.startTime) === slot.start && formatTime(item.endTime) === slot.end
  )
}

onMounted(load)
</script>

<template>
  <PageContainer title="我的课表" description="查询本学期课表，按浙江高校常用上课时间段和周一到周五分别展示。">
    <el-tabs v-model="activeHalf" class="schedule-tabs">
      <el-tab-pane label="前半学期（第1-8周）" name="firstHalf" />
      <el-tab-pane label="后半学期（第9周以后）" name="secondHalf" />
    </el-tabs>

    <el-table v-loading="loading" :data="timeSlots" border class="data-table schedule-table">
      <el-table-column label="时间段" width="150" fixed>
        <template #default="{ row }">
          <div class="slot-label">
            <strong>{{ row.label }}</strong>
            <span>{{ row.start }}-{{ row.end }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column v-for="weekday in weekdays" :key="weekday" :label="formatWeekday(weekday)" min-width="190">
        <template #default="{ row }">
          <div v-if="slotRows(weekday, row).length" class="schedule-list">
            <div
              v-for="item in slotRows(weekday, row)"
              :key="`${item.djx_assignmentid13}-${weekday}-${row.start}`"
              class="schedule-item"
            >
              <strong>{{ item.djx_coursename13 }}</strong>
              <small>{{ item.djx_tname13 }} / {{ formatCourseType(item.djx_coursetype13) }}</small>
            </div>
          </div>
          <span v-else class="empty-cell">无课程</span>
        </template>
      </el-table-column>
    </el-table>
  </PageContainer>
</template>

<style scoped>
.schedule-tabs {
  margin-bottom: 12px;
}

.schedule-list {
  display: grid;
  gap: 8px;
}

.slot-label {
  display: grid;
  gap: 4px;
}

.slot-label strong {
  color: #1f2d3d;
  font-weight: 600;
}

.slot-label span {
  color: #606266;
  font-size: 13px;
}

.schedule-item {
  display: grid;
  gap: 4px;
  min-height: 58px;
  padding: 8px 10px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  background: #f8fafc;
}

.schedule-item strong {
  color: #1f2d3d;
  font-weight: 600;
}

.schedule-item span {
  color: #303133;
}

.schedule-item small {
  color: #606266;
}

.empty-cell {
  color: #a8abb2;
}
</style>
