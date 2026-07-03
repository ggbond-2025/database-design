export type Row = Record<string, unknown>

export const roleLabels: Record<string, string> = {
  ADMIN: '管理员',
  TEACHER: '教师',
  STUDENT: '学生'
}

export const genderLabels: Record<string, string> = {
  MALE: '男',
  FEMALE: '女'
}

export const enrollmentStatusLabels: Record<string, string> = {
  SELECTED: '已选',
  DROPPED: '已退',
  COMPLETED: '已完成'
}

export const courseTypeLabels: Record<string, string> = {
  REQUIRED: '必修',
  ELECTIVE: '选修'
}

export const assessmentTypeLabels: Record<string, string> = {
  EXAM: '考试',
  CHECK: '考查'
}

export function formatRole(value: unknown) {
  return formatEnum(value, roleLabels)
}

export function formatGender(value: unknown) {
  return formatEnum(value, genderLabels)
}

export function formatEnrollmentStatus(value: unknown) {
  return formatEnum(value, enrollmentStatusLabels)
}

export function formatCourseType(value: unknown) {
  return formatEnum(value, courseTypeLabels)
}

export function formatAssessmentType(value: unknown) {
  return formatEnum(value, assessmentTypeLabels)
}

export function formatSemester(value: unknown) {
  if (value === null || value === undefined || value === '') {
    return '-'
  }
  return `第${value}学期`
}

export function formatBoolean(value: unknown) {
  return value ? '是' : '否'
}

export function formatNumber(value: unknown, fallback = '0') {
  if (value === null || value === undefined || value === '') {
    return fallback
  }
  return String(value)
}

export function formatDateTime(value: unknown) {
  if (!value) {
    return '-'
  }
  return String(value).replace('T', ' ')
}

export function formatEnum(value: unknown, labels: Record<string, string>) {
  if (value === null || value === undefined || value === '') {
    return '-'
  }
  const key = String(value)
  return labels[key] ?? key
}

export function courseOptionLabel(row: Row) {
  return [
    row.djx_coursename13,
    row.djx_classname13,
    row.djx_academicyear13,
    formatSemester(row.djx_semester13),
    row.djx_tname13 ? `教师：${row.djx_tname13}` : undefined
  ]
    .filter(Boolean)
    .join(' / ')
}
