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
import MajorCourseManagementView from '@/views/admin/MajorCourseManagementView.vue'
import RegionManagementView from '@/views/admin/RegionManagementView.vue'
import StatisticsView from '@/views/admin/StatisticsView.vue'
import StudentManagementView from '@/views/admin/StudentManagementView.vue'
import TeachingEvaluationManagementView from '@/views/admin/TeachingEvaluationManagementView.vue'
import TeacherManagementView from '@/views/admin/TeacherManagementView.vue'
import UserManagementView from '@/views/admin/UserManagementView.vue'
import LoginView from '@/views/login/LoginView.vue'
import AvailableCoursesView from '@/views/student/AvailableCoursesView.vue'
import MyCoursesView from '@/views/student/MyCoursesView.vue'
import MyCreditsView from '@/views/student/MyCreditsView.vue'
import MyEnrollmentsView from '@/views/student/MyEnrollmentsView.vue'
import MyGradesView from '@/views/student/MyGradesView.vue'
import MyClassmatesView from '@/views/student/MyClassmatesView.vue'
import MyRankView from '@/views/student/MyRankView.vue'
import MyScheduleView from '@/views/student/MyScheduleView.vue'
import StudentProfileView from '@/views/student/StudentProfileView.vue'
import StudentDashboardView from '@/views/student/StudentDashboardView.vue'
import TeachingEvaluationsView from '@/views/student/TeachingEvaluationsView.vue'
import TeacherAssignmentsView from '@/views/teacher/TeacherAssignmentsView.vue'
import TeacherDashboardView from '@/views/teacher/TeacherDashboardView.vue'
import TeacherEvaluationsView from '@/views/teacher/TeacherEvaluationsView.vue'
import TeacherEnrollmentListView from '@/views/teacher/TeacherEnrollmentListView.vue'
import TeacherGradeEntryView from '@/views/teacher/TeacherGradeEntryView.vue'
import TeacherProfileView from '@/views/teacher/TeacherProfileView.vue'
import TeacherStatisticsView from '@/views/teacher/TeacherStatisticsView.vue'

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
          path: 'admin/major-courses',
          component: MajorCourseManagementView,
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
          path: 'admin/evaluations',
          component: TeachingEvaluationManagementView,
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
          path: 'teacher/assignments',
          component: TeacherAssignmentsView,
          meta: { role: 'TEACHER' }
        },
        {
          path: 'teacher/enrollments',
          component: TeacherEnrollmentListView,
          meta: { role: 'TEACHER' }
        },
        {
          path: 'teacher/grades',
          component: TeacherGradeEntryView,
          meta: { role: 'TEACHER' }
        },
        {
          path: 'teacher/evaluations',
          component: TeacherEvaluationsView,
          meta: { role: 'TEACHER' }
        },
        {
          path: 'teacher/statistics',
          component: TeacherStatisticsView,
          meta: { role: 'TEACHER' }
        },
        {
          path: 'teacher/profile',
          component: TeacherProfileView,
          meta: { role: 'TEACHER' }
        },
        {
          path: 'student/dashboard',
          name: 'student-dashboard',
          component: StudentDashboardView,
          meta: { role: 'STUDENT' }
        },
        {
          path: 'student/available-courses',
          component: AvailableCoursesView,
          meta: { role: 'STUDENT' }
        },
        {
          path: 'student/enrollments',
          component: MyEnrollmentsView,
          meta: { role: 'STUDENT' }
        },
        {
          path: 'student/courses',
          component: MyCoursesView,
          meta: { role: 'STUDENT' }
        },
        {
          path: 'student/schedule',
          component: MyScheduleView,
          meta: { role: 'STUDENT' }
        },
        {
          path: 'student/evaluations',
          component: TeachingEvaluationsView,
          meta: { role: 'STUDENT' }
        },
        {
          path: 'student/grades',
          component: MyGradesView,
          meta: { role: 'STUDENT' }
        },
        {
          path: 'student/credits',
          component: MyCreditsView,
          meta: { role: 'STUDENT' }
        },
        {
          path: 'student/classmates',
          component: MyClassmatesView,
          meta: { role: 'STUDENT' }
        },
        {
          path: 'student/rank',
          component: MyRankView,
          meta: { role: 'STUDENT' }
        },
        {
          path: 'student/profile',
          component: StudentProfileView,
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
