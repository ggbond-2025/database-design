<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import {
  DataAnalysis,
  DataBoard,
  Files,
  Notebook,
  OfficeBuilding,
  Reading,
  School,
  SwitchButton,
  Tickets,
  User,
  UserFilled
} from '@element-plus/icons-vue'

import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const route = useRoute()

const menuItems = computed(() => {
  if (auth.role === 'ADMIN') {
    return [
      { index: '/admin/dashboard', label: '管理仪表盘', icon: DataBoard },
      { index: '/admin/regions', label: '地区管理', icon: OfficeBuilding },
      { index: '/admin/majors', label: '专业管理', icon: School },
      { index: '/admin/classes', label: '班级管理', icon: Files },
      { index: '/admin/students', label: '学生管理', icon: UserFilled },
      { index: '/admin/teachers', label: '教师管理', icon: User },
      { index: '/admin/courses', label: '课程管理', icon: Notebook },
      { index: '/admin/assignments', label: '开课安排', icon: Tickets },
      { index: '/admin/enrollments', label: '选课记录', icon: Reading },
      { index: '/admin/grades', label: '成绩管理', icon: DataAnalysis },
      { index: '/admin/statistics', label: '统计查询', icon: DataBoard },
      { index: '/admin/users', label: '用户账号', icon: User }
    ]
  }
  if (auth.role === 'TEACHER') {
    return [
      { index: '/teacher/dashboard', label: '教师工作台', icon: Reading },
      { index: '/teacher/assignments', label: '我的任课', icon: Notebook },
      { index: '/teacher/enrollments', label: '选课名单', icon: UserFilled },
      { index: '/teacher/grades', label: '成绩录入', icon: DataAnalysis },
      { index: '/teacher/statistics', label: '课程统计', icon: DataBoard },
      { index: '/teacher/profile', label: '本人信息', icon: User }
    ]
  }
  return [
    { index: '/student/dashboard', label: '学生中心', icon: UserFilled },
    { index: '/student/available-courses', label: '可选课程', icon: Tickets },
    { index: '/student/enrollments', label: '我的选课', icon: Reading },
    { index: '/student/courses', label: '我的课程', icon: Notebook },
    { index: '/student/grades', label: '我的成绩', icon: DataAnalysis },
    { index: '/student/credits', label: '我的学分', icon: DataBoard },
    { index: '/student/rank', label: '我的排名', icon: Files },
    { index: '/student/profile', label: '本人信息', icon: User }
  ]
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
