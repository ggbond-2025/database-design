<script setup lang="ts">
import { ElMessageBox } from 'element-plus'

const props = withDefaults(
  defineProps<{
    message?: string
  }>(),
  {
    message: '确认删除该记录？'
  }
)

const emit = defineEmits<{
  confirm: []
}>()

async function confirmDelete() {
  await ElMessageBox.confirm(props.message, '删除确认', {
    type: 'warning',
    confirmButtonText: '删除',
    cancelButtonText: '取消'
  })
  emit('confirm')
}
</script>

<template>
  <el-button type="danger" link @click="confirmDelete">删除</el-button>
</template>
