<script setup lang="ts">
import CrudPage from '@/components/CrudPage.vue'
import { classroomConfig } from './adminConfigs'
</script>

<template>
  <CrudPage :config="classroomConfig" />
</template>
