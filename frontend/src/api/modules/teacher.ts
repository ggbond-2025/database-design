import http from '@/api/http'

export function teacherGet<T = Record<string, unknown>[]>(path: string, params?: Record<string, unknown>) {
  return http.get<T, T>(`/teacher${path}`, { params })
}

export function teacherPost<T = Record<string, unknown>>(path: string, data: Record<string, unknown>) {
  return http.post<T, T>(`/teacher${path}`, data)
}

export function teacherPut<T = Record<string, unknown>>(path: string, data: Record<string, unknown>) {
  return http.put<T, T>(`/teacher${path}`, data)
}
