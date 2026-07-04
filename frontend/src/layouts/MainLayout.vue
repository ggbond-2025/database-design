<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Lock,
  SwitchButton,
} from '@element-plus/icons-vue'

import { changePassword } from '@/api/modules/auth'
import { findActiveMenuGroup, getMenuGroups } from '@/layouts/menuGroups'
import { useAuthStore } from '@/stores/auth'
import { formatRole } from '@/utils/formatters'

const auth = useAuthStore()
const route = useRoute()
const passwordDialogVisible = ref(false)
const passwordSaving = ref(false)
const passwordFormRef = ref<FormInstance>()
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const passwordRules: FormRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '新密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [{ required: true, message: '请再次输入新密码', trigger: 'blur' }]
}

const menuGroups = computed(() => getMenuGroups(auth.role))
const activeMenu = computed(() => route.path)
const activeMenuGroup = computed(() => findActiveMenuGroup(auth.role, route.path))
const defaultOpeneds = computed(() => (activeMenuGroup.value ? [activeMenuGroup.value] : []))
const roleText = computed(() => formatRole(auth.role))

function openPasswordDialog() {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordDialogVisible.value = true
}

async function submitPassword() {
  const valid = await passwordFormRef.value?.validate()
  if (!valid) {
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.error('两次输入的新密码不一致')
    return
  }
  passwordSaving.value = true
  try {
    await changePassword(passwordForm)
    ElMessage.success('密码已修改，请重新登录')
    passwordDialogVisible.value = false
    await auth.logout()
  } finally {
    passwordSaving.value = false
  }
}

async function confirmLogout() {
  await ElMessageBox.confirm('确认退出当前账号？', '退出登录', {
    confirmButtonText: '退出',
    cancelButtonText: '取消',
    type: 'warning'
  })
  await auth.logout()
}
</script>

<template>
  <el-container class="app-shell">
    <el-aside width="248px" class="app-side">
      <div class="brand">
        <div class="brand-mark">教</div>
        <div>
          <div class="brand-title">高校教务系统</div>
          <div class="brand-subtitle">DengjxMIS13</div>
        </div>
      </div>

      <el-menu
        :key="`${auth.role}-${activeMenuGroup || 'menu'}`"
        :default-active="activeMenu"
        :default-openeds="defaultOpeneds"
        router
        class="side-menu"
      >
        <el-sub-menu v-for="group in menuGroups" :key="group.index" :index="group.index">
          <template #title>
            <el-icon><component :is="group.icon" /></el-icon>
            <span>{{ group.label }}</span>
          </template>
          <el-menu-item v-for="item in group.items" :key="item.index" :index="item.index">
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.label }}</span>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="app-header">
        <div class="header-title">教务管理工作台</div>
        <el-dropdown trigger="click">
          <button class="user-chip" type="button">
            <span>{{ auth.displayName || '未命名用户' }}</span>
            <small>{{ roleText }}</small>
          </button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item :icon="Lock" @click="openPasswordDialog">修改密码</el-dropdown-item>
              <el-dropdown-item :icon="SwitchButton" @click="confirmLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <el-main class="app-main">
        <RouterView />
      </el-main>
    </el-container>
  </el-container>

  <el-dialog v-model="passwordDialogVisible" title="修改密码" width="460px">
    <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="96px">
      <el-form-item label="原密码" prop="oldPassword">
        <el-input v-model="passwordForm.oldPassword" type="password" show-password autocomplete="current-password" />
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input v-model="passwordForm.newPassword" type="password" show-password autocomplete="new-password" />
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input v-model="passwordForm.confirmPassword" type="password" show-password autocomplete="new-password" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="passwordDialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="passwordSaving" @click="submitPassword">保存</el-button>
    </template>
  </el-dialog>
</template>
