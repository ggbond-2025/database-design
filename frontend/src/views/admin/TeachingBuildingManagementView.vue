<script setup lang="ts">
import CrudPage from '@/components/CrudPage.vue'
import { teachingBuildingConfig } from './adminConfigs'
</script>

<template>
  <CrudPage :config="teachingBuildingConfig" />
</template>
