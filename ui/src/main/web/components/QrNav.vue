<template>
  <li>Hello World</li>
</template>

<script>
export default {
  props: {
    page: String,
    active: {
      type: Boolean,
      default: false,
      required: false
    }
  },
  name: "qr-nav"
};
</script>
