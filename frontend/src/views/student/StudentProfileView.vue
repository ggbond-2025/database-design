<script setup lang="ts">
import { onMounted, ref } from 'vue'

import { getCurrentProfile, type CurrentUserProfile } from '@/api/modules/auth'
import PageContainer from '@/components/PageContainer.vue'
import { formatGender, formatNumber, formatRole } from '@/utils/formatters'

const loading = ref(false)
const profile = ref<CurrentUserProfile>()

async function load() {
  loading.value = true
  try {
    profile.value = await getCurrentProfile()
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <PageContainer title="本人信息" description="查看当前账号绑定的学生档案、班级专业和学分信息。">
    <el-skeleton v-if="loading" :rows="5" animated />
    <el-descriptions v-else border :column="2">
      <el-descriptions-item label="显示名称">{{ profile?.displayName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="账号角色">{{ formatRole(profile?.role) }}</el-descriptions-item>
      <el-descriptions-item label="学号">{{ profile?.studentNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="姓名">{{ profile?.studentName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="性别">{{ formatGender(profile?.studentGender) }}</el-descriptions-item>
      <el-descriptions-item label="年龄">{{ profile?.studentAge ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="班级">{{ profile?.className || '-' }}</el-descriptions-item>
      <el-descriptions-item label="专业">{{ profile?.majorName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="生源地">{{ profile?.regionName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="已修学分">{{ formatNumber(profile?.totalCredits) }}</el-descriptions-item>
    </el-descriptions>
  </PageContainer>
</template>
