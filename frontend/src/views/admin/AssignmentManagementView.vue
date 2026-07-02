<script setup lang="ts">
import CrudPage from '@/components/CrudPage.vue'
import { assignmentConfig } from './adminConfigs'
</script>

<template>
  <CrudPage :config="assignmentConfig" />
</template>
