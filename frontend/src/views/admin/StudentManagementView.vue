<script setup lang="ts">
import CrudPage from '@/components/CrudPage.vue'
import { studentConfig } from './adminConfigs'
</script>

<template>
  <CrudPage :config="studentConfig" />
</template>
