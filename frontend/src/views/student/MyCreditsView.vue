<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'

import { studentGet } from '@/api/modules/student'
import PageContainer from '@/components/PageContainer.vue'
import { formatNumber, type Row } from '@/utils/formatters'

const summary = ref<Row>({})
const isCreditConsistent = computed(() => {
  return String(summary.value.djx_totalcredits13 ?? '') === String(summary.value.djx_calculatedcredits13 ?? '')
})

onMounted(async () => {
  summary.value = await studentGet<Record<string, unknown>>('/statistics/credits')
})
</script>

<template>
  <PageContainer title="我的学分" description="查看已完成课程、通过课程和学分汇总情况。">
    <div class="metric-grid">
      <div class="metric-card">
        <span>已修学分</span>
        <strong>{{ formatNumber(summary.djx_totalcredits13) }}</strong>
      </div>
      <div class="metric-card">
        <span>通过课程</span>
        <strong>{{ formatNumber(summary.djx_passedcourses13) }}</strong>
      </div>
      <div class="metric-card">
        <span>完成课程</span>
        <strong>{{ formatNumber(summary.djx_completedcourses13) }}</strong>
      </div>
      <div class="metric-card">
        <span>校验学分</span>
        <strong>{{ formatNumber(summary.djx_calculatedcredits13) }}</strong>
      </div>
    </div>

    <el-alert
      class="section-alert"
      :type="isCreditConsistent ? 'success' : 'warning'"
      :closable="false"
      :title="isCreditConsistent ? '学分汇总与成绩明细一致' : '学分汇总与成绩明细不一致，请联系管理员核对'"
    />

    <el-descriptions border :column="2">
      <el-descriptions-item label="学号">{{ summary.djx_sno13 || '-' }}</el-descriptions-item>
      <el-descriptions-item label="姓名">{{ summary.djx_sname13 || '-' }}</el-descriptions-item>
    </el-descriptions>
  </PageContainer>
</template>
