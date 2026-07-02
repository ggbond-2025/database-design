<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { DataBoard, Reading, SwitchButton, UserFilled } from '@element-plus/icons-vue'

import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const route = useRoute()
const router = useRouter()

const menuItems = computed(() => {
  if (auth.role === 'ADMIN') {
    return [{ index: '/admin/dashboard', label: '管理仪表盘', icon: DataBoard }]
  }
  if (auth.role === 'TEACHER') {
    return [{ index: '/teacher/dashboard', label: '教师工作台', icon: Reading }]
  }
  return [{ index: '/student/dashboard', label: '学生中心', icon: UserFilled }]
})

const activeMenu = computed(() => route.path)

async function confirmLogout() {
  await ElMessageBox.confirm('确认退出当前账号？', '退出登录', {
    confirmButtonText: '退出',
    cancelButtonText: '取消',
    type: 'warning'
  })
  await auth.logout()
}
</script>

<template>
  <el-container class="app-shell">
    <el-aside width="248px" class="app-side">
      <div class="brand">
        <div class="brand-mark">教</div>
        <div>
          <div class="brand-title">高校教务系统</div>
          <div class="brand-subtitle">DengjxMIS13</div>
        </div>
      </div>

      <el-menu :default-active="activeMenu" router class="side-menu">
        <el-menu-item v-for="item in menuItems" :key="item.index" :index="item.index">
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="app-header">
        <div class="header-title">教务管理工作台</div>
        <el-dropdown trigger="click">
          <button class="user-chip" type="button">
            <span>{{ auth.displayName || '未命名用户' }}</span>
            <small>{{ auth.role }}</small>
          </button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item :icon="SwitchButton" @click="confirmLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <el-main class="app-main">
        <RouterView />
      </el-main>
    </el-container>
  </el-container>
</template>
