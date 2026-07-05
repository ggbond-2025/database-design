import { createRouter, createWebHistory } from 'vue-router'

import MainLayout from '@/layouts/MainLayout.vue'
import { roleDashboard, useAuthStore, type Role } from '@/stores/auth'

const LoginView = () => import('@/views/login/LoginView.vue')

const AdminDashboardView = () => import('@/views/admin/AdminDashboardView.vue')
const AssignmentManagementView = () => import('@/views/admin/AssignmentManagementView.vue')
const ClassroomManagementView = () => import('@/views/admin/ClassroomManagementView.vue')
const ClassManagementView = () => import('@/views/admin/ClassManagementView.vue')
const CourseManagementView = () => import('@/views/admin/CourseManagementView.vue')
const EnrollmentManagementView = () => import('@/views/admin/EnrollmentManagementView.vue')
const FinalExamManagementView = () => import('@/views/admin/FinalExamManagementView.vue')
const GradeManagementView = () => import('@/views/admin/GradeManagementView.vue')
const MajorCourseManagementView = () => import('@/views/admin/MajorCourseManagementView.vue')
const MajorManagementView = () => import('@/views/admin/MajorManagementView.vue')
const MajorTransferApplicationManagementView = () =>
  import('@/views/admin/MajorTransferApplicationManagementView.vue')
const RegionManagementView = () => import('@/views/admin/RegionManagementView.vue')
const StatisticsView = () => import('@/views/admin/StatisticsView.vue')
const StudentManagementView = () => import('@/views/admin/StudentManagementView.vue')
const TeacherManagementView = () => import('@/views/admin/TeacherManagementView.vue')
const TeachingBuildingManagementView = () => import('@/views/admin/TeachingBuildingManagementView.vue')
const TeachingEvaluationManagementView = () => import('@/views/admin/TeachingEvaluationManagementView.vue')
const UserManagementView = () => import('@/views/admin/UserManagementView.vue')

const AvailableCoursesView = () => import('@/views/student/AvailableCoursesView.vue')
const MajorTransferApplicationsView = () => import('@/views/student/MajorTransferApplicationsView.vue')
const MyClassmatesView = () => import('@/views/student/MyClassmatesView.vue')
const MyCoursesView = () => import('@/views/student/MyCoursesView.vue')
const MyCreditsView = () => import('@/views/student/MyCreditsView.vue')
const MyEnrollmentsView = () => import('@/views/student/MyEnrollmentsView.vue')
const MyGradesView = () => import('@/views/student/MyGradesView.vue')
const MyRankView = () => import('@/views/student/MyRankView.vue')
const MyScheduleView = () => import('@/views/student/MyScheduleView.vue')
const StudentDashboardView = () => import('@/views/student/StudentDashboardView.vue')
const StudentFinalExamsView = () => import('@/views/student/StudentFinalExamsView.vue')
const StudentProfileView = () => import('@/views/student/StudentProfileView.vue')
const TeachingEvaluationsView = () => import('@/views/student/TeachingEvaluationsView.vue')

const TeacherAssignmentsView = () => import('@/views/teacher/TeacherAssignmentsView.vue')
const TeacherDashboardView = () => import('@/views/teacher/TeacherDashboardView.vue')
const TeacherEnrollmentListView = () => import('@/views/teacher/TeacherEnrollmentListView.vue')
const TeacherEvaluationsView = () => import('@/views/teacher/TeacherEvaluationsView.vue')
const TeacherFinalExamsView = () => import('@/views/teacher/TeacherFinalExamsView.vue')
const TeacherGradeEntryView = () => import('@/views/teacher/TeacherGradeEntryView.vue')
const TeacherProfileView = () => import('@/views/teacher/TeacherProfileView.vue')
const TeacherScheduleView = () => import('@/views/teacher/TeacherScheduleView.vue')
const TeacherStatisticsView = () => import('@/views/teacher/TeacherStatisticsView.vue')

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
          path: 'admin/teaching-buildings',
          component: TeachingBuildingManagementView,
          meta: { role: 'ADMIN' }
        },
        {
          path: 'admin/classrooms',
          component: ClassroomManagementView,
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
          path: 'admin/final-exams',
          component: FinalExamManagementView,
          meta: { role: 'ADMIN' }
        },
        {
          path: 'admin/major-transfer-applications',
          component: MajorTransferApplicationManagementView,
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
          path: 'teacher/schedule',
          component: TeacherScheduleView,
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
          path: 'teacher/final-exams',
          component: TeacherFinalExamsView,
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
          path: 'student/final-exams',
          component: StudentFinalExamsView,
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
        },
        {
          path: 'student/major-transfer-applications',
          component: MajorTransferApplicationsView,
          meta: { role: 'STUDENT' }
        }
      ]
    }
  ]
})

router.beforeEach(async (to) => {
  const auth = useAuthStore()
  if (auth.isAuthenticated && !auth.profileLoaded) {
    try {
      await auth.refreshProfile()
    } catch {
      return to.name === 'login' ? true : '/login'
    }
  }
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
