import http from '@/api/http'
import type { FieldOption } from '@/components/crudTypes'

export function getAdminList<T = Record<string, unknown>>(path: string, params?: Record<string, unknown>) {
  return http.get<T, T>(path, { params })
}

export function postAdmin<T = Record<string, unknown>>(path: string, data: Record<string, unknown>) {
  return http.post<T, T>(path, data)
}

export function putAdmin<T = Record<string, unknown>>(path: string, data: Record<string, unknown>) {
  return http.put<T, T>(path, data)
}

export function deleteAdmin(path: string) {
  return http.delete<void, void>(path)
}

export function getLookupOptions(name: string) {
  return http.get<FieldOption[], FieldOption[]>(`/admin/lookups/${name}`)
}
