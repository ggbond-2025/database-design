<script setup lang="ts">
import CrudPage from '@/components/CrudPage.vue'
import { finalExamConfig } from './adminConfigs'
</script>

<template>
  <CrudPage :config="finalExamConfig" />
</template>
