import assert from 'node:assert/strict'
import test from 'node:test'

import { findActiveMenuGroup, getMenuGroups } from '../src/layouts/menuGroups.ts'

test('admin menu is grouped by functional categories', () => {
  const groups = getMenuGroups('ADMIN')

  assert.deepEqual(
    groups.map((group) => group.label),
    ['工作台', '基础数据', '教务业务', '查询与账号']
  )
  assert.deepEqual(
    groups.flatMap((group) => group.items.map((item) => item.label)),
    [
      '管理仪表盘',
      '专业管理',
      '班级管理',
      '学生管理',
      '教师管理',
      '课程管理',
      '专业课程计划',
      '开课安排',
      '选课记录',
      '成绩管理',
      '教学评价',
      '统计查询',
      '用户账号'
    ]
  )
})

test('student menu groups keep schedule under course learning', () => {
  const groups = getMenuGroups('STUDENT')
  const courseGroup = groups.find((group) => group.label === '选课学习')

  assert.ok(courseGroup)
  assert.deepEqual(
    courseGroup.items.map((item) => item.label),
    ['可选课程', '我的选课', '我的课程', '我的课表', '教学评价']
  )
})

test('teacher menu exposes teaching evaluations under teaching management', () => {
  const groups = getMenuGroups('TEACHER')
  const teachingGroup = groups.find((group) => group.label === '教学管理')

  assert.ok(teachingGroup)
  assert.deepEqual(
    teachingGroup.items.map((item) => item.label),
    ['我的任课', '选课名单', '成绩录入', '教学评价']
  )
})

test('active route resolves its parent group', () => {
  assert.equal(findActiveMenuGroup('TEACHER', '/teacher/grades'), 'teacher-teaching')
  assert.equal(findActiveMenuGroup('STUDENT', '/student/profile'), 'student-personal')
  assert.equal(findActiveMenuGroup('ADMIN', '/admin/not-found'), undefined)
})
