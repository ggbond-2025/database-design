<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { UploadFile, UploadInstance } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'

import { importStudentsFromCsv } from '@/api/modules/admin'
import type { StudentImportResult } from '@/api/modules/admin'
import CrudPage from '@/components/CrudPage.vue'
import { studentConfig } from './adminConfigs'

const importDialogVisible = ref(false)
const importing = ref(false)
const selectedFile = ref<File | null>(null)
const uploadRef = ref<UploadInstance>()
const fileInputRef = ref<HTMLInputElement>()
const importResult = ref<StudentImportResult | null>(null)

function openImportDialog() {
  importDialogVisible.value = true
  selectedFile.value = null
  importResult.value = null
  uploadRef.value?.clearFiles()
  clearNativeFileInput()
}

function handleFileChange(uploadFile: UploadFile) {
  selectedFile.value = uploadFile.raw ?? null
  importResult.value = null
  clearNativeFileInput()
}

function handleFileRemove() {
  selectedFile.value = null
  clearNativeFileInput()
}

function handleNativeFileChange(event: Event) {
  const input = event.target as HTMLInputElement
  selectedFile.value = input.files?.[0] ?? null
  importResult.value = null
  uploadRef.value?.clearFiles()
}

function clearNativeFileInput() {
  if (fileInputRef.value) {
    fileInputRef.value.value = ''
  }
}

async function submitImport(reload: () => Promise<void>) {
  if (!selectedFile.value) {
    ElMessage.warning('请先选择CSV文件')
    return
  }
  importing.value = true
  try {
    const result = await importStudentsFromCsv(selectedFile.value)
    importResult.value = result
    await reload()
    if (result.failureCount > 0) {
      ElMessage.warning(`导入完成：成功${result.successCount}条，失败${result.failureCount}条`)
      return
    }
    ElMessage.success(`导入成功：${result.successCount}条`)
  } finally {
    importing.value = false
  }
}
</script>

<template>
  <CrudPage :config="studentConfig">
    <template #toolbar-extra="{ reload }">
      <el-button @click="openImportDialog">CSV导入</el-button>

      <el-dialog v-model="importDialogVisible" title="CSV导入学生" width="620px">
        <el-alert
          title="CSV表头必须为：sno,sname,gender,age,classId,regionId,admissionDate"
          type="info"
          :closable="false"
          show-icon
        />
        <input
          id="student-csv-file-input"
          ref="fileInputRef"
          class="native-file-input"
          type="file"
          accept=".csv,text/csv"
          @change="handleNativeFileChange"
        />
        <el-upload
          ref="uploadRef"
          class="student-import-upload"
          drag
          :auto-upload="false"
          :limit="1"
          accept=".csv,text/csv"
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
        >
          <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
          <div class="el-upload__text">拖拽CSV文件到此处</div>
          <label
            class="el-button el-button--primary select-file-button"
            for="student-csv-file-input"
            @click.stop
          >
            选择CSV文件
          </label>
          <template #tip>
            <div class="el-upload__tip">
              性别使用 MALE 或 FEMALE，入学时间格式为 yyyy-MM-dd。
            </div>
          </template>
        </el-upload>

        <el-alert
          v-if="selectedFile"
          class="selected-file-alert"
          :title="`已添加文件：${selectedFile.name}`"
          type="success"
          :closable="false"
          show-icon
        />

        <div v-if="importResult" class="import-result">
          <el-alert
            :title="`成功${importResult.successCount}条，失败${importResult.failureCount}条`"
            :type="importResult.failureCount > 0 ? 'warning' : 'success'"
            :closable="false"
            show-icon
          />
          <el-scrollbar v-if="importResult.errors.length" max-height="180px" class="import-errors">
            <div v-for="error in importResult.errors" :key="error" class="import-error-row">{{ error }}</div>
          </el-scrollbar>
        </div>

        <template #footer>
          <el-button @click="importDialogVisible = false">关闭</el-button>
          <el-button type="primary" :loading="importing" @click="submitImport(reload)">开始导入</el-button>
        </template>
      </el-dialog>
    </template>
  </CrudPage>
</template>

<style scoped>
.student-import-upload {
  margin-top: 16px;
}

.select-file-button {
  margin-top: 10px;
}

.native-file-input {
  position: absolute;
  width: 1px;
  height: 1px;
  opacity: 0;
  pointer-events: none;
}

.selected-file-alert {
  margin-top: 12px;
}

.import-result {
  margin-top: 16px;
}

.import-errors {
  margin-top: 12px;
  padding: 8px 12px;
  border: 1px solid var(--el-border-color);
  border-radius: 6px;
}

.import-error-row {
  line-height: 24px;
  color: var(--el-color-danger);
}
</style>
