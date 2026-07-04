import assert from 'node:assert/strict'
import test from 'node:test'

import { classPeriodEndTimeMap, classPeriodOptions } from '../src/utils/classPeriods.ts'

test('class period options use fixed teaching time ranges', () => {
  assert.deepEqual(
    classPeriodOptions.map((option) => option.label),
    [
      '第1节 08:00-09:40',
      '第2节 09:55-11:35',
      '第3节 13:30-15:10',
      '第4节 15:25-17:05',
      '第5节 18:30-20:10'
    ]
  )
})

test('class period start time maps to matching end time', () => {
  assert.equal(classPeriodEndTimeMap['08:00:00'], '09:40:00')
  assert.equal(classPeriodEndTimeMap['18:30:00'], '20:10:00')
})
