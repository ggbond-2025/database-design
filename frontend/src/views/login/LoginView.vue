<script setup lang="ts">
import { reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { Lock, User } from '@element-plus/icons-vue'

import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const formRef = ref<FormInstance>()
const loading = ref(false)
const form = reactive({
  username: '',
  password: ''
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function submit() {
  const valid = await formRef.value?.validate()
  if (!valid) {
    return
  }
  loading.value = true
  try {
    await auth.login(form)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="login-page">
    <section class="login-panel">
      <div class="login-copy">
        <div class="system-badge">DengjxMIS13</div>
        <h1>高校教务系统</h1>
        <p>管理员、教师、学生统一登录，进入各自业务工作台。</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" class="login-form" @keyup.enter="submit">
        <h2>账号登录</h2>
        <el-form-item prop="username">
          <el-input v-model="form.username" size="large" placeholder="用户名" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            size="large"
            type="password"
            show-password
            placeholder="密码"
            :prefix-icon="Lock"
          />
        </el-form-item>
        <el-button type="primary" size="large" class="login-button" :loading="loading" @click="submit">
          登录
        </el-button>
      </el-form>
    </section>
  </main>
</template>
