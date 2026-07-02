import http from '@/api/http'

export function getAdminList<T = Record<string, unknown>>(path: string, params?: Record<string, unknown>) {
  return http.get<T, T>(path, { params })
}

export function postAdmin<T = Record<string, unknown>>(path: string, data: Record<string, unknown>) {
  return http.post<T, T>(path, data)
}

export function deleteAdmin(path: string) {
  return http.delete<void, void>(path)
}
