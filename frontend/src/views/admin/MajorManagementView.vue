<script setup lang="ts">
import CrudPage from '@/components/CrudPage.vue'
import { majorConfig } from './adminConfigs'
</script>

<template>
  <CrudPage :config="majorConfig" />
</template>
