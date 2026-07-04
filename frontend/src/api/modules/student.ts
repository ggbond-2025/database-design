import http from '@/api/http'
import type { FieldOption } from '@/components/crudTypes'

export function studentGet<T = Record<string, unknown>[]>(path: string, params?: Record<string, unknown>) {
  return http.get<T, T>(`/student${path}`, { params })
}

export function studentPost<T = Record<string, unknown>>(path: string, data?: Record<string, unknown>) {
  return http.post<T, T>(`/student${path}`, data)
}

export function studentDelete<T = Record<string, unknown>>(path: string) {
  return http.delete<T, T>(`/student${path}`)
}

export function getStudentLookupOptions(name: string) {
  return http.get<FieldOption[], FieldOption[]>(`/student/lookups/${name}`)
}
