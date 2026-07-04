<script setup lang="ts">
import CrudPage from '@/components/CrudPage.vue'
import { majorCourseConfig } from './adminConfigs'
</script>

<template>
  <CrudPage :config="majorCourseConfig" />
</template>
