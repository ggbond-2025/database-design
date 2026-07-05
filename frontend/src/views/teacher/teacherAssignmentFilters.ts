export type TeacherAssignmentRow = Record<string, unknown>

export interface TeacherAssignmentFilterCriteria {
  academicYear?: string
  semester?: number | string
}

export function getAcademicYearOptions(rows: TeacherAssignmentRow[]) {
  return [...new Set(rows.map((row) => row.djx_academicyear13).filter(Boolean).map(String))].sort(
    (left, right) => right.localeCompare(left)
  )
}

export function filterTeacherAssignments(
  rows: TeacherAssignmentRow[],
  criteria: TeacherAssignmentFilterCriteria
) {
  return rows.filter((row) => {
    if (criteria.academicYear && String(row.djx_academicyear13) !== criteria.academicYear) {
      return false
    }
    if (
      criteria.semester !== undefined &&
      criteria.semester !== '' &&
      Number(row.djx_semester13) !== Number(criteria.semester)
    ) {
      return false
    }
    return true
  })
}
