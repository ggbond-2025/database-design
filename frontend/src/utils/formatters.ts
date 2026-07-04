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
  const text = String(value)
  if (text.endsWith('Z')) {
    return new Intl.DateTimeFormat('zh-CN', {
      timeZone: 'Asia/Shanghai',
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      hour12: false
    })
      .format(new Date(text))
      .replace(/\//g, '-')
  }
  return text.replace('T', ' ').slice(0, 16)
}

export function formatGradeTerm(grade: unknown, semester: unknown) {
  const gradeText = ['大一', '大二', '大三', '大四'][Number(grade) - 1] ?? `大${grade}`
  return `${gradeText}${Number(semester) === 1 ? '上学期' : '下学期'}`
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
