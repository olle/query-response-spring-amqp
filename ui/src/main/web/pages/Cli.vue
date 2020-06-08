<template>
  <article>
    <h1>Query Command Line Interface</h1>
    <form v-on:submit.prevent="publish">
      <label>&gt;</label>
      <input v-model="query" v-focus :autofocus="'autofocus'" placeholder="query [timeout:150]" />
    </form>
    <pre><code>{{ response }}</code></pre>
  </article>
</template>

<script>
import Vue from "vue";

import { publishMessage, addListener } from "../ws";

Vue.directive("focus", {
  inserted: function (el) {
    el.focus();
  },
});

export default {
  name: "Cli",
  data: function () {
    return {
      query: "",
      response: "# " + new Date(),
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
    addListener((msg) => {
      try {
        var message = JSON.parse(msg.data);
        if (message.response) {
          this.$data.response = JSON.stringify(message.response, null, 2);
        }
      } catch (err) {
        console.error("unexpected payload", msg.data);
      }
    });
  },
};
</script>

<style scoped>
form * {
  border: none;
}
form {
  display: flex;
}
form > label,
form > input,
pre {
  background: var(--panel);
  color: var(--fg);
  border: var(--border);
  padding: 0.8rem 1rem;
  font-size: 1.1rem;
  font-family: monospace;
}
form > label {
  border-right: none;
  padding-right: 0;
}
form > input {
  flex: 1;
  width: 100%;
  border-left: none;
  padding-left: 0.5rem;
}
form > input:focus {
  outline: none;
}
pre {
  border-top: none;
  margin-top: 0;
  font-family: monospace;
}
</style>
