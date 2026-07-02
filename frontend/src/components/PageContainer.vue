<script setup lang="ts">
defineProps<{
  title: string
  description?: string
}>()
</script>

<template>
  <section class="page-container">
    <header class="page-header">
      <div>
        <h1>{{ title }}</h1>
        <p v-if="description">{{ description }}</p>
      </div>
      <slot name="actions" />
    </header>
    <slot />
  </section>
</template>
