import { createRouter, createWebHistory } from 'vue-router'

import MainLayout from '@/layouts/MainLayout.vue'
import { roleDashboard, useAuthStore, type Role } from '@/stores/auth'
import AdminDashboardView from '@/views/admin/AdminDashboardView.vue'
import LoginView from '@/views/login/LoginView.vue'
import StudentDashboardView from '@/views/student/StudentDashboardView.vue'
import TeacherDashboardView from '@/views/teacher/TeacherDashboardView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: () => roleDashboard(useAuthStore().role)
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/',
      component: MainLayout,
      meta: { requiresAuth: true },
      children: [
        {
          path: 'admin/dashboard',
          name: 'admin-dashboard',
          component: AdminDashboardView,
          meta: { role: 'ADMIN' }
        },
        {
          path: 'teacher/dashboard',
          name: 'teacher-dashboard',
          component: TeacherDashboardView,
          meta: { role: 'TEACHER' }
        },
        {
          path: 'student/dashboard',
          name: 'student-dashboard',
          component: StudentDashboardView,
          meta: { role: 'STUDENT' }
        }
      ]
    }
  ]
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.name === 'login' && auth.isAuthenticated) {
    return roleDashboard(auth.role)
  }
  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    return '/login'
  }
  const requiredRole = to.meta.role as Role | undefined
  if (requiredRole && auth.role !== requiredRole) {
    return roleDashboard(auth.role)
  }
  return true
})

export default router
