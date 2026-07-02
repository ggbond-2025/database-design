<script setup lang="ts">
import CrudPage from '@/components/CrudPage.vue'
import { userConfig } from './adminConfigs'
</script>

<template>
  <CrudPage :config="userConfig" />
</template>
