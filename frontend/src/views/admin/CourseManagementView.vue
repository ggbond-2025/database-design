<script setup lang="ts">
import CrudPage from '@/components/CrudPage.vue'
import { courseConfig } from './adminConfigs'
</script>

<template>
  <CrudPage :config="courseConfig" />
</template>
