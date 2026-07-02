<script setup lang="ts">
import CrudPage from '@/components/CrudPage.vue'
import { regionConfig } from './adminConfigs'
</script>

<template>
  <CrudPage :config="regionConfig" />
</template>
