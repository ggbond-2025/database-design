import http from '@/api/http'

export interface PageResult<T> {
  records: T[]
  total: number
  page: number
  size: number
}

export const crudApi = {
  list<T>(resource: string, params: Record<string, unknown>) {
    return http.get<PageResult<T>, PageResult<T>>(`/admin/${resource}`, { params })
  },
  create<T>(resource: string, data: Record<string, unknown>) {
    return http.post<T, T>(`/admin/${resource}`, data)
  },
  update<T>(resource: string, id: number | string, data: Record<string, unknown>) {
    return http.put<T, T>(`/admin/${resource}/${id}`, data)
  },
  remove(resource: string, id: number | string) {
    return http.delete<void, void>(`/admin/${resource}/${id}`)
  }
}
