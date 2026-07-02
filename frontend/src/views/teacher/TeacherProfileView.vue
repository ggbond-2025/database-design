<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
import PageContainer from '@/components/PageContainer.vue'

const auth = useAuthStore()
</script>

<template>
  <PageContainer title="本人信息" description="当前登录教师账号信息。">
    <el-descriptions border :column="1">
      <el-descriptions-item label="显示名称">{{ auth.displayName }}</el-descriptions-item>
      <el-descriptions-item label="角色">{{ auth.role }}</el-descriptions-item>
    </el-descriptions>
  </PageContainer>
</template>
