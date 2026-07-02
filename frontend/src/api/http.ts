import axios, { AxiosError } from 'axios'
import { ElMessage } from 'element-plus'

const TOKEN_KEY = 'academic_affairs_token'

export interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
}

const http = axios.create({
  baseURL: '/api',
  timeout: 15000
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY)
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => {
    const payload = response.data as ApiResponse<unknown>
    if (payload && typeof payload.success === 'boolean') {
      if (!payload.success) {
        ElMessage.error(payload.message || '请求失败')
        return Promise.reject(new Error(payload.message || '请求失败'))
      }
      return payload.data
    }
    return response.data
  },
  (error: AxiosError<ApiResponse<unknown>>) => {
    if (error.response?.status === 401) {
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem('academic_affairs_role')
      localStorage.removeItem('academic_affairs_display_name')
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }
    const message = error.response?.data?.message || error.message || '网络请求失败'
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export { TOKEN_KEY }
export default http
