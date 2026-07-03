<script setup lang="ts">
import { onMounted, ref } from 'vue'

import { getCurrentProfile, type CurrentUserProfile } from '@/api/modules/auth'
import PageContainer from '@/components/PageContainer.vue'
import { formatGender, formatRole } from '@/utils/formatters'

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
  <PageContainer title="本人信息" description="查看当前账号绑定的教师档案和联系方式。">
    <el-skeleton v-if="loading" :rows="4" animated />
    <el-descriptions v-else border :column="2">
      <el-descriptions-item label="显示名称">{{ profile?.displayName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="账号角色">{{ formatRole(profile?.role) }}</el-descriptions-item>
      <el-descriptions-item label="教师编号">{{ profile?.teacherNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="姓名">{{ profile?.teacherName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="性别">{{ formatGender(profile?.teacherGender) }}</el-descriptions-item>
      <el-descriptions-item label="年龄">{{ profile?.teacherAge ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="职称">{{ profile?.teacherTitle || '-' }}</el-descriptions-item>
      <el-descriptions-item label="联系电话">{{ profile?.teacherPhone || '-' }}</el-descriptions-item>
    </el-descriptions>
  </PageContainer>
</template>
