<template>
  <p>
    &copy; Copyright
    <span>{{ new Date().getFullYear() }}</span>
    Olle Törnström -
    <a
      href="https://github.com/olle/query-response-spring-amqp/blob/master/LICENSE"
      >The Apache License 2.0</a
    >
  </p>
</template>

<style scoped>
p {
  text-align: center;
  font-size: 0.7rem;
  color: var(--fg-light);
}
p > a {
  color: inherit;
}
</style>
