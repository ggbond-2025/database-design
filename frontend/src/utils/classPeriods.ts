export const classPeriodOptions = [
  { label: '第1节 08:00-09:40', value: '08:00:00' },
  { label: '第2节 09:55-11:35', value: '09:55:00' },
  { label: '第3节 13:30-15:10', value: '13:30:00' },
  { label: '第4节 15:25-17:05', value: '15:25:00' },
  { label: '第5节 18:30-20:10', value: '18:30:00' }
]

export const classPeriodEndTimeMap: Record<string, string> = {
  '08:00:00': '09:40:00',
  '09:55:00': '11:35:00',
  '13:30:00': '15:10:00',
  '15:25:00': '17:05:00',
  '18:30:00': '20:10:00'
}

export function classPeriodRelatedFields(endTimeProp: string) {
  return Object.fromEntries(
    Object.entries(classPeriodEndTimeMap).map(([startTime, endTime]) => [startTime, { [endTimeProp]: endTime }])
  )
}
