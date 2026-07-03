import http from '@/api/http'

export interface ChangePasswordPayload {
  oldPassword: string
  newPassword: string
  confirmPassword: string
}

export interface CurrentUserProfile {
  userId: number
  username: string
  role: string
  displayName: string
  enabled: boolean
  studentId?: number
  studentNo?: string
  studentName?: string
  studentGender?: string
  studentAge?: number
  className?: string
  majorName?: string
  regionName?: string
  totalCredits?: number | string
  teacherId?: number
  teacherNo?: string
  teacherName?: string
  teacherGender?: string
  teacherAge?: number
  teacherTitle?: string
  teacherPhone?: string
}

export function changePassword(data: ChangePasswordPayload) {
  return http.post<void, void>('/auth/password', data)
}

export function getCurrentProfile() {
  return http.get<CurrentUserProfile, CurrentUserProfile>('/auth/me')
}
