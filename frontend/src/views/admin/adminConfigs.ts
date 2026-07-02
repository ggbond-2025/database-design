import type { CrudPageConfig } from '@/components/crudTypes'

export const genderOptions = [
  { label: '男', value: 'MALE' },
  { label: '女', value: 'FEMALE' }
]

export const assessmentOptions = [
  { label: '考试', value: 'EXAM' },
  { label: '考查', value: 'CHECK' }
]

export const courseTypeOptions = [
  { label: '必修', value: 'REQUIRED' },
  { label: '选修', value: 'ELECTIVE' }
]

export const enrollmentStatusOptions = [
  { label: '已选', value: 'SELECTED' },
  { label: '已退', value: 'DROPPED' },
  { label: '已完成', value: 'COMPLETED' }
]

export const regionConfig: CrudPageConfig = {
  title: '地区管理',
  description: '维护学生生源地，用于地区学生数量统计。',
  resource: 'regions',
  rowKey: 'regionId',
  fields: [
    { prop: 'regionId', label: 'ID', form: false },
    { prop: 'regionName', label: '地区名称', required: true }
  ]
}

export const majorConfig: CrudPageConfig = {
  title: '专业管理',
  description: '维护学校专业基础数据。',
  resource: 'majors',
  rowKey: 'majorId',
  fields: [
    { prop: 'majorId', label: 'ID', form: false },
    { prop: 'majorName', label: '专业名称', required: true }
  ]
}

export const classConfig: CrudPageConfig = {
  title: '班级管理',
  description: '维护班级、所属专业和入学年级。',
  resource: 'classes',
  rowKey: 'classId',
  fields: [
    { prop: 'classId', label: 'ID', form: false },
    { prop: 'className', label: '班级名称', required: true },
    { prop: 'majorId', label: '专业ID', type: 'number', required: true },
    { prop: 'gradeYear', label: '年级', type: 'number', required: true }
  ]
}

export const studentConfig: CrudPageConfig = {
  title: '学生管理',
  description: '维护学生学号、班级、生源地和基础信息。',
  resource: 'students',
  rowKey: 'studentId',
  fields: [
    { prop: 'studentId', label: 'ID', form: false },
    { prop: 'sno', label: '学号', required: true },
    { prop: 'sname', label: '姓名', required: true },
    { prop: 'gender', label: '性别', type: 'select', required: true, options: genderOptions },
    { prop: 'age', label: '年龄', type: 'number', required: true },
    { prop: 'classId', label: '班级ID', type: 'number', required: true },
    { prop: 'regionId', label: '地区ID', type: 'number', required: true },
    { prop: 'totalCredits', label: '已修学分', form: false }
  ]
}

export const teacherConfig: CrudPageConfig = {
  title: '教师管理',
  description: '维护教师编号、职称和联系电话。',
  resource: 'teachers',
  rowKey: 'teacherId',
  fields: [
    { prop: 'teacherId', label: 'ID', form: false },
    { prop: 'tno', label: '教师编号', required: true },
    { prop: 'tname', label: '姓名', required: true },
    { prop: 'gender', label: '性别', type: 'select', required: true, options: genderOptions },
    { prop: 'age', label: '年龄', type: 'number', required: true },
    { prop: 'title', label: '职称', required: true },
    { prop: 'phone', label: '联系电话', required: true }
  ]
}

export const courseConfig: CrudPageConfig = {
  title: '课程管理',
  description: '维护课程编号、学时、考核方式和学分。',
  resource: 'courses',
  rowKey: 'courseId',
  fields: [
    { prop: 'courseId', label: 'ID', form: false },
    { prop: 'courseCode', label: '课程编号', required: true },
    { prop: 'courseName', label: '课程名称', required: true },
    { prop: 'hours', label: '学时', type: 'number', required: true },
    { prop: 'assessmentType', label: '考核方式', type: 'select', required: true, options: assessmentOptions },
    { prop: 'credit', label: '学分', type: 'number', required: true }
  ]
}

export const assignmentConfig: CrudPageConfig = {
  title: '开课安排',
  description: '按课程、班级、教师、学年学期发布必修或选修课程。',
  resource: 'assignments',
  rowKey: 'assignmentId',
  fields: [
    { prop: 'assignmentId', label: 'ID', form: false },
    { prop: 'courseId', label: '课程ID', type: 'number', required: true },
    { prop: 'classId', label: '班级ID', type: 'number', required: true },
    { prop: 'teacherId', label: '教师ID', type: 'number', required: true },
    { prop: 'academicYear', label: '学年', required: true },
    {
      prop: 'semester',
      label: '学期',
      type: 'select',
      required: true,
      options: [
        { label: '第1学期', value: 1 },
        { label: '第2学期', value: 2 }
      ]
    },
    { prop: 'courseType', label: '课程类型', type: 'select', required: true, options: courseTypeOptions },
    { prop: 'capacity', label: '容量', type: 'number', required: true },
    { prop: 'enrollmentOpen', label: '开放选课', type: 'boolean' }
  ]
}

export const gradeConfig: CrudPageConfig = {
  title: '成绩管理',
  description: '按选课记录录入、修改和删除成绩。',
  resource: 'grades',
  rowKey: 'gradeId',
  fields: [
    { prop: 'gradeId', label: 'ID', form: false },
    { prop: 'enrollmentId', label: '选课ID', type: 'number', required: true },
    { prop: 'score', label: '成绩', type: 'number', required: true },
    { prop: 'gradedAt', label: '录入时间', form: false }
  ]
}

export const userConfig: CrudPageConfig = {
  title: '用户账号管理',
  description: '维护管理员、教师、学生登录账号与绑定关系。',
  resource: 'users',
  rowKey: 'userId',
  fields: [
    { prop: 'userId', label: 'ID', form: false },
    { prop: 'username', label: '用户名', required: true },
    { prop: 'password', label: '密码', required: true, table: false },
    {
      prop: 'role',
      label: '角色',
      type: 'select',
      required: true,
      options: [
        { label: '管理员', value: 'ADMIN' },
        { label: '教师', value: 'TEACHER' },
        { label: '学生', value: 'STUDENT' }
      ]
    },
    { prop: 'studentId', label: '学生ID', type: 'number' },
    { prop: 'teacherId', label: '教师ID', type: 'number' },
    { prop: 'enabled', label: '启用', type: 'boolean' }
  ]
}
