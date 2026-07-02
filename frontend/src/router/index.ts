import { createRouter, createWebHistory } from 'vue-router'

import MainLayout from '@/layouts/MainLayout.vue'
import { roleDashboard, useAuthStore, type Role } from '@/stores/auth'
import AssignmentManagementView from '@/views/admin/AssignmentManagementView.vue'
import ClassManagementView from '@/views/admin/ClassManagementView.vue'
import CourseManagementView from '@/views/admin/CourseManagementView.vue'
import AdminDashboardView from '@/views/admin/AdminDashboardView.vue'
import EnrollmentManagementView from '@/views/admin/EnrollmentManagementView.vue'
import GradeManagementView from '@/views/admin/GradeManagementView.vue'
import MajorManagementView from '@/views/admin/MajorManagementView.vue'
import RegionManagementView from '@/views/admin/RegionManagementView.vue'
import StatisticsView from '@/views/admin/StatisticsView.vue'
import StudentManagementView from '@/views/admin/StudentManagementView.vue'
import TeacherManagementView from '@/views/admin/TeacherManagementView.vue'
import UserManagementView from '@/views/admin/UserManagementView.vue'
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
          path: 'admin/regions',
          component: RegionManagementView,
          meta: { role: 'ADMIN' }
        },
        {
          path: 'admin/majors',
          component: MajorManagementView,
          meta: { role: 'ADMIN' }
        },
        {
          path: 'admin/classes',
          component: ClassManagementView,
          meta: { role: 'ADMIN' }
        },
        {
          path: 'admin/students',
          component: StudentManagementView,
          meta: { role: 'ADMIN' }
        },
        {
          path: 'admin/teachers',
          component: TeacherManagementView,
          meta: { role: 'ADMIN' }
        },
        {
          path: 'admin/courses',
          component: CourseManagementView,
          meta: { role: 'ADMIN' }
        },
        {
          path: 'admin/assignments',
          component: AssignmentManagementView,
          meta: { role: 'ADMIN' }
        },
        {
          path: 'admin/enrollments',
          component: EnrollmentManagementView,
          meta: { role: 'ADMIN' }
        },
        {
          path: 'admin/grades',
          component: GradeManagementView,
          meta: { role: 'ADMIN' }
        },
        {
          path: 'admin/statistics',
          component: StatisticsView,
          meta: { role: 'ADMIN' }
        },
        {
          path: 'admin/users',
          component: UserManagementView,
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
