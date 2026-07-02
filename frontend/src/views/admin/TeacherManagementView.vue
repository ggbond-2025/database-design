<script setup lang="ts">
import CrudPage from '@/components/CrudPage.vue'
import { teacherConfig } from './adminConfigs'
</script>

<template>
  <CrudPage :config="teacherConfig" />
</template>
