import assert from 'node:assert/strict'
import test from 'node:test'

import {
  filterTeacherAssignments,
  getAcademicYearOptions
} from '../src/views/teacher/teacherAssignmentFilters.ts'

const rows = [
  { djx_assignmentid13: 1, djx_academicyear13: '2024-2025', djx_semester13: 2 },
  { djx_assignmentid13: 2, djx_academicyear13: '2025-2026', djx_semester13: 1 },
  { djx_assignmentid13: 3, djx_academicyear13: '2025-2026', djx_semester13: 2 }
]

test('teacher assignment year options are unique and newest first', () => {
  assert.deepEqual(getAcademicYearOptions(rows), ['2025-2026', '2024-2025'])
})

test('teacher assignments can be filtered by academic year and semester', () => {
  assert.deepEqual(
    filterTeacherAssignments(rows, { academicYear: '2025-2026', semester: 2 }).map(
      (row) => row.djx_assignmentid13
    ),
    [3]
  )
})

test('teacher assignment filters ignore empty criteria', () => {
  assert.deepEqual(
    filterTeacherAssignments(rows, { academicYear: '', semester: undefined }).map(
      (row) => row.djx_assignmentid13
    ),
    [1, 2, 3]
  )
})
