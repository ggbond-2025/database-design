import { defineStore } from 'pinia'

import http, { TOKEN_KEY } from '@/api/http'
import router from '@/router'

export type Role = 'ADMIN' | 'TEACHER' | 'STUDENT'

interface LoginPayload {
  username: string
  password: string
}

interface LoginResponse {
  token: string
  role: Role
  displayName: string
}

interface CurrentUserProfileResponse {
  role: Role
  displayName: string
}

const ROLE_KEY = 'academic_affairs_role'
const DISPLAY_NAME_KEY = 'academic_affairs_display_name'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem(TOKEN_KEY) || '',
    role: (localStorage.getItem(ROLE_KEY) || '') as Role | '',
    displayName: localStorage.getItem(DISPLAY_NAME_KEY) || '',
    profileLoaded: false
  }),
  getters: {
    isAuthenticated: (state) => Boolean(state.token)
  },
  actions: {
    async login(payload: LoginPayload) {
      const data = await http.post<LoginResponse, LoginResponse>('/auth/login', payload)
      this.token = data.token
      this.role = data.role
      this.displayName = data.displayName
      this.profileLoaded = true
      localStorage.setItem(TOKEN_KEY, data.token)
      localStorage.setItem(ROLE_KEY, data.role)
      localStorage.setItem(DISPLAY_NAME_KEY, data.displayName)
      await router.replace(roleDashboard(data.role))
    },
    async refreshProfile() {
      if (!this.token) {
        return
      }
      try {
        const data = await http.get<CurrentUserProfileResponse, CurrentUserProfileResponse>('/auth/me')
        this.role = data.role
        this.displayName = data.displayName
        this.profileLoaded = true
        localStorage.setItem(ROLE_KEY, data.role)
        localStorage.setItem(DISPLAY_NAME_KEY, data.displayName)
      } catch (error) {
        this.clear()
        throw error
      }
    },
    async logout() {
      this.clear()
      await router.replace('/login')
    },
    clear() {
      this.token = ''
      this.role = ''
      this.displayName = ''
      this.profileLoaded = false
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(ROLE_KEY)
      localStorage.removeItem(DISPLAY_NAME_KEY)
    }
  }
})

export function roleDashboard(role: Role | '') {
  if (role === 'ADMIN') {
    return '/admin/dashboard'
  }
  if (role === 'TEACHER') {
    return '/teacher/dashboard'
  }
  if (role === 'STUDENT') {
    return '/student/dashboard'
  }
  return '/login'
}
