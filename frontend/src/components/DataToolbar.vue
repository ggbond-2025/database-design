<script setup lang="ts">
defineProps<{
  createText?: string
}>()

const keyword = defineModel<string>({ default: '' })
const emit = defineEmits<{
  search: []
  create: []
}>()
</script>

<template>
  <div class="data-toolbar">
    <el-input
      v-model="keyword"
      clearable
      placeholder="输入关键词查询"
      class="toolbar-search"
      @keyup.enter="emit('search')"
      @clear="emit('search')"
    />
    <el-button @click="emit('search')">查询</el-button>
    <el-button type="primary" @click="emit('create')">{{ createText || '新增' }}</el-button>
    <slot />
  </div>
</template>
