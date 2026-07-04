import type { Component } from 'vue'
import {
  DataAnalysis,
  DataBoard,
  Files,
  Notebook,
  OfficeBuilding,
  Reading,
  Calendar,
  School,
  Tickets,
  User,
  UserFilled
} from '@element-plus/icons-vue'

export type MenuRole = 'ADMIN' | 'TEACHER' | 'STUDENT'

export interface MenuItem {
  index: string
  label: string
  icon: Component
}

export interface MenuGroup {
  index: string
  label: string
  icon: Component
  items: MenuItem[]
}

const adminMenuGroups: MenuGroup[] = [
  {
    index: 'admin-workbench',
    label: '工作台',
    icon: DataBoard,
    items: [{ index: '/admin/dashboard', label: '管理仪表盘', icon: DataBoard }]
  },
  {
    index: 'admin-base',
    label: '基础数据',
    icon: School,
    items: [
      { index: '/admin/majors', label: '专业管理', icon: School },
      { index: '/admin/classes', label: '班级管理', icon: Files },
      { index: '/admin/teaching-buildings', label: '教学楼管理', icon: OfficeBuilding },
      { index: '/admin/classrooms', label: '教室管理', icon: OfficeBuilding },
      { index: '/admin/students', label: '学生管理', icon: UserFilled },
      { index: '/admin/teachers', label: '教师管理', icon: User },
      { index: '/admin/courses', label: '课程管理', icon: Notebook },
      { index: '/admin/major-courses', label: '专业课程计划', icon: School }
    ]
  },
  {
    index: 'admin-affairs',
    label: '教务业务',
    icon: Tickets,
    items: [
      { index: '/admin/assignments', label: '开课安排', icon: Tickets },
      { index: '/admin/final-exams', label: '期末考试', icon: Calendar },
      { index: '/admin/major-transfer-applications', label: '转专业审核', icon: School },
      { index: '/admin/enrollments', label: '选课记录', icon: Reading },
      { index: '/admin/grades', label: '成绩管理', icon: DataAnalysis },
      { index: '/admin/evaluations', label: '教学评价', icon: DataAnalysis }
    ]
  },
  {
    index: 'admin-query-account',
    label: '查询与账号',
    icon: DataAnalysis,
    items: [
      { index: '/admin/statistics', label: '统计查询', icon: DataBoard },
      { index: '/admin/users', label: '用户账号', icon: User }
    ]
  }
]

const teacherMenuGroups: MenuGroup[] = [
  {
    index: 'teacher-workbench',
    label: '工作台',
    icon: Reading,
    items: [{ index: '/teacher/dashboard', label: '教师工作台', icon: Reading }]
  },
  {
    index: 'teacher-teaching',
    label: '教学管理',
    icon: Notebook,
    items: [
      { index: '/teacher/assignments', label: '我的任课', icon: Notebook },
      { index: '/teacher/enrollments', label: '选课名单', icon: UserFilled },
      { index: '/teacher/grades', label: '成绩录入', icon: DataAnalysis },
      { index: '/teacher/evaluations', label: '教学评价', icon: DataAnalysis },
      { index: '/teacher/final-exams', label: '期末考试', icon: Calendar }
    ]
  },
  {
    index: 'teacher-query-profile',
    label: '查询与个人',
    icon: DataBoard,
    items: [
      { index: '/teacher/statistics', label: '课程统计', icon: DataBoard },
      { index: '/teacher/profile', label: '本人信息', icon: User }
    ]
  }
]

const studentMenuGroups: MenuGroup[] = [
  {
    index: 'student-workbench',
    label: '工作台',
    icon: UserFilled,
    items: [{ index: '/student/dashboard', label: '学生中心', icon: UserFilled }]
  },
  {
    index: 'student-course',
    label: '选课学习',
    icon: Tickets,
    items: [
      { index: '/student/available-courses', label: '可选课程', icon: Tickets },
      { index: '/student/enrollments', label: '我的选课', icon: Reading },
      { index: '/student/courses', label: '我的课程', icon: Notebook },
      { index: '/student/schedule', label: '我的课表', icon: Calendar },
      { index: '/student/evaluations', label: '教学评价', icon: DataAnalysis },
      { index: '/student/final-exams', label: '期末考试', icon: Calendar }
    ]
  },
  {
    index: 'student-academic',
    label: '成绩学业',
    icon: DataAnalysis,
    items: [
      { index: '/student/grades', label: '我的成绩', icon: DataAnalysis },
      { index: '/student/credits', label: '我的学分', icon: DataBoard },
      { index: '/student/rank', label: '我的排名', icon: Files }
    ]
  },
  {
    index: 'student-personal',
    label: '个人班级',
    icon: OfficeBuilding,
    items: [
      { index: '/student/classmates', label: '我的班级', icon: OfficeBuilding },
      { index: '/student/profile', label: '本人信息', icon: User },
      { index: '/student/major-transfer-applications', label: '转专业申请', icon: School }
    ]
  }
]

export function getMenuGroups(role: MenuRole | '') {
  if (role === 'ADMIN') {
    return adminMenuGroups
  }
  if (role === 'TEACHER') {
    return teacherMenuGroups
  }
  return studentMenuGroups
}

export function findActiveMenuGroup(role: MenuRole | '', path: string) {
  return getMenuGroups(role).find((group) => group.items.some((item) => item.index === path))?.index
}
