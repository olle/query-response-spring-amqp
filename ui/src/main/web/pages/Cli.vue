<template>
  <article>
    <h1>Query Command Line Interface</h1>
    <form v-on:submit.prevent="publish">
      <input v-model="query" :autofocus="'autofocus'" />
    </form>
    <code>
      <em>No responses received yet.</em>
    </code>
  </article>
</template>

<script>
import { publishMessage } from "../ws";

export default {
  name: "Cli",
  data: function () {
    return {
      query: "",
    };
  },
  methods: {
    publish: function (evt) {
      publishMessage(this.$data.query);
      this.$data.query = "";
      evt.target.reset();
    },
  },
  mounted: function () {
    let input = document.querySelector("[autofocus]");
    if (input) {
      input.focus();
    }
  },
};
</script>

<style scoped>
form {
  font-family: monospace;
}
form * {
  border: none;
  font-family: inherit;
}
form > input,
code {
  background: var(--panel);
  border: var(--border);
  padding: 0.8rem 1rem;
}
form > input {
  width: 100%;
  border-bottom-style: dotted;
  font-size: 1rem;
}
code {
  display: block;
  border-top: none;
}
</style>
