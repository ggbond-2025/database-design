<script setup lang="ts">
import CrudPage from '@/components/CrudPage.vue'
import { classConfig } from './adminConfigs'
</script>

<template>
  <CrudPage :config="classConfig" />
</template>
