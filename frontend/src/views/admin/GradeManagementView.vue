<script setup lang="ts">
import CrudPage from '@/components/CrudPage.vue'
import { gradeConfig } from './adminConfigs'
</script>

<template>
  <CrudPage :config="gradeConfig" />
</template>
