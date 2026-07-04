import type { CrudPageConfig, FieldOption, FieldVisibilityContext } from '@/components/crudTypes'
import { classPeriodOptions, classPeriodRelatedFields } from '@/utils/classPeriods'

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

export const weekdayOptions = [
  { label: '周一', value: 1 },
  { label: '周二', value: 2 },
  { label: '周三', value: 3 },
  { label: '周四', value: 4 },
  { label: '周五', value: 5 }
]

function selectedMajorCourseHours(context: FieldVisibilityContext) {
  const majorCourseId = context.form.majorCourseId
  const option = context.lookupOptions['major-courses']?.find((item) => item.value === majorCourseId)
  return Number(option?.meta?.hours ?? 0)
}

function needsSecondScheduleSlot(context: FieldVisibilityContext) {
  return selectedMajorCourseHours(context) >= 48
}

function classroomMatchesSelectedBuilding(option: FieldOption, context: FieldVisibilityContext) {
  const buildingId = context.form.buildingId
  return !buildingId || Number(option.meta?.buildingId) === Number(buildingId)
}

export const regionConfig: CrudPageConfig = {
  title: '地区管理',
  description: '维护学生生源地，用于地区学生数量统计。',
  resource: 'regions',
  rowKey: 'regionId',
  fields: [
    { prop: 'regionId', label: '地区编号', form: false },
    { prop: 'regionName', label: '地区名称', required: true }
  ]
}

export const majorConfig: CrudPageConfig = {
  title: '专业管理',
  description: '维护学校专业基础数据和毕业所需学分。',
  resource: 'majors',
  rowKey: 'majorId',
  fields: [
    { prop: 'majorId', label: '专业编号', form: false },
    { prop: 'majorName', label: '专业名称', required: true },
    { prop: 'graduationCredits', label: '毕业所需学分', type: 'number', required: true }
  ]
}

export const classConfig: CrudPageConfig = {
  title: '班级管理',
  description: '维护班级、所属专业和入学年级。',
  resource: 'classes',
  rowKey: 'classId',
  fields: [
    { prop: 'classId', label: '班级编号', form: false },
    { prop: 'className', label: '班级名称', required: true },
    { prop: 'majorId', label: '所属专业', type: 'select', required: true, lookup: 'majors' },
    { prop: 'gradeYear', label: '年级', type: 'number', required: true }
  ]
}

export const studentConfig: CrudPageConfig = {
  title: '学生管理',
  description: '维护学生学号、班级、生源地和基础信息。',
  resource: 'students',
  rowKey: 'studentId',
  fields: [
    { prop: 'studentId', label: '学生编号', form: false },
    { prop: 'sno', label: '学号', required: true },
    { prop: 'sname', label: '姓名', required: true },
    { prop: 'gender', label: '性别', type: 'select', required: true, options: genderOptions },
    { prop: 'age', label: '年龄', type: 'number', required: true },
    { prop: 'classId', label: '所属班级', type: 'select', required: true, lookup: 'classes' },
    { prop: 'regionId', label: '生源地区', type: 'select', required: true, lookup: 'regions' },
    { prop: 'admissionDate', label: '入学时间', type: 'date', required: true }
  ]
}

export const teachingBuildingConfig: CrudPageConfig = {
  title: '教学楼管理',
  description: '维护可用于排课和考试地点显示的教学楼。',
  resource: 'teaching-buildings',
  rowKey: 'buildingId',
  fields: [
    { prop: 'buildingId', label: '教学楼编号', form: false },
    { prop: 'buildingName', label: '教学楼名称', required: true }
  ]
}

export const classroomConfig: CrudPageConfig = {
  title: '教室管理',
  description: '维护教学楼下的具体教室和容量。',
  resource: 'classrooms',
  rowKey: 'classroomId',
  fields: [
    { prop: 'classroomId', label: '教室编号', form: false },
    { prop: 'buildingId', label: '教学楼', type: 'select', required: true, lookup: 'teaching-buildings' },
    { prop: 'classroomName', label: '教室', required: true },
    { prop: 'capacity', label: '容量', type: 'number', required: true }
  ]
}

export const teacherConfig: CrudPageConfig = {
  title: '教师管理',
  description: '维护教师编号、职称和联系电话。',
  resource: 'teachers',
  rowKey: 'teacherId',
  fields: [
    { prop: 'teacherId', label: '教师记录编号', form: false },
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
  description: '维护课程编号、课程名称、学时、考核方式和学分。',
  resource: 'courses',
  rowKey: 'courseId',
  fields: [
    { prop: 'courseId', label: '课程记录编号', form: false },
    { prop: 'courseCode', label: '课程编号', required: true },
    { prop: 'courseName', label: '课程名称', required: true },
    { prop: 'hours', label: '学时', type: 'number', required: true },
    { prop: 'assessmentType', label: '考核方式', type: 'select', required: true, options: assessmentOptions },
    { prop: 'credit', label: '学分', type: 'number', required: true }
  ]
}

export const majorCourseConfig: CrudPageConfig = {
  title: '专业课程计划',
  description: '维护不同专业的必修/选修课程及建议修读年级学期。',
  resource: 'major-courses',
  rowKey: 'majorCourseId',
  fields: [
    { prop: 'majorCourseId', label: '计划编号', form: false },
    { prop: 'majorId', label: '所属专业', type: 'select', required: true, lookup: 'majors' },
    { prop: 'courseId', label: '课程', type: 'select', required: true, lookup: 'courses' },
    { prop: 'courseType', label: '课程类型', type: 'select', required: true, options: courseTypeOptions },
    { prop: 'targetGrade', label: '开设年级', type: 'select', required: true, options: [
      { label: '大一', value: 1 },
      { label: '大二', value: 2 },
      { label: '大三', value: 3 },
      { label: '大四', value: 4 }
    ] },
    { prop: 'targetSemester', label: '开设学期', type: 'select', required: true, options: [
      { label: '上学期', value: 1 },
      { label: '下学期', value: 2 }
    ] }
  ]
}

export const assignmentConfig: CrudPageConfig = {
  title: '开课安排',
  description: '按专业课程计划、班级、教师、学年学期发布开课安排。',
  resource: 'assignments',
  rowKey: 'assignmentId',
  fields: [
    { prop: 'assignmentId', label: '开课安排编号', form: false },
    { prop: 'majorCourseId', label: '专业课程计划', type: 'select', required: true, lookup: 'major-courses' },
    { prop: 'classId', label: '班级', type: 'select', required: true, lookup: 'classes' },
    { prop: 'teacherId', label: '教师', type: 'select', required: true, lookup: 'teachers' },
    {
      prop: 'buildingId',
      label: '教学楼',
      type: 'select',
      lookup: 'teaching-buildings',
      form: true,
      table: false,
      payload: false,
      group: '上课地点'
    },
    {
      prop: 'classroomId',
      label: '教室',
      type: 'select',
      required: true,
      lookup: 'classrooms',
      optionFilter: classroomMatchesSelectedBuilding,
      group: '上课地点'
    },
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
    { prop: 'capacity', label: '容量', type: 'number', required: true },
    { prop: 'enrollmentOpen', label: '开放选课', type: 'boolean' },
    { prop: 'weekdayOne', label: '上课日1', type: 'select', required: true, options: weekdayOptions, group: '课程时间1' },
    {
      prop: 'startTimeOne',
      label: '上课节次1',
      type: 'select',
      required: true,
      options: classPeriodOptions,
      relatedFields: classPeriodRelatedFields('endTimeOne'),
      group: '课程时间1'
    },
    { prop: 'endTimeOne', label: '结束时间1', form: false, table: false },
    {
      prop: 'weekdayTwo',
      label: '上课日2',
      type: 'select',
      required: true,
      options: weekdayOptions,
      group: '课程时间2',
      visibleWhen: needsSecondScheduleSlot,
      hiddenPayload: { weekdayTwo: null, startTimeTwo: null, endTimeTwo: null }
    },
    {
      prop: 'startTimeTwo',
      label: '上课节次2',
      type: 'select',
      required: true,
      options: classPeriodOptions,
      relatedFields: classPeriodRelatedFields('endTimeTwo'),
      group: '课程时间2',
      visibleWhen: needsSecondScheduleSlot,
      hiddenPayload: { weekdayTwo: null, startTimeTwo: null, endTimeTwo: null }
    },
    { prop: 'endTimeTwo', label: '结束时间2', form: false, table: false }
  ]
}

export const finalExamConfig: CrudPageConfig = {
  title: '期末考试管理',
  description: '按课程、学年和学期统一设置期末考试时间；考试地点默认使用对应开课安排的上课教室。',
  resource: 'final-exams',
  rowKey: 'examId',
  fields: [
    { prop: 'examId', label: '考试编号', form: false },
    { prop: 'courseId', label: '课程', type: 'select', required: true, lookup: 'courses' },
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
    { prop: 'examTime', label: '考试时间', type: 'datetime', required: true }
  ]
}

export const gradeConfig: CrudPageConfig = {
  title: '成绩管理',
  description: '按选课记录录入、修改和删除成绩。',
  resource: 'grades',
  rowKey: 'gradeId',
  fields: [
    { prop: 'gradeId', label: '成绩编号', form: false },
    { prop: 'enrollmentId', label: '选课记录', type: 'select', required: true, lookup: 'active-enrollments' },
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
    { prop: 'userId', label: '账号编号', form: false },
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
    { prop: 'studentId', label: '绑定学生', type: 'select', lookup: 'students' },
    { prop: 'teacherId', label: '绑定教师', type: 'select', lookup: 'teachers' },
    { prop: 'enabled', label: '启用', type: 'boolean' }
  ]
}
