<template>
  <article>
    <h1>Query Command Line Interface</h1>
    <form v-on:submit.prevent="publish">
      <input v-model="query" :autofocus="'autofocus'" />
    </form>
    <code>
      <pre>{{ response }}</pre>
    </code>
  </article>
</template>

<script>
import { publishMessage, addListener } from "../ws";

export default {
  name: "Cli",
  data: function () {
    return {
      query: "",
      response: "No response.",
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
    addListener((msg) => {
      try {
        var message = JSON.parse(msg.data);
        if (message.response) {
          this.$data.response = message.response;
        }
      } catch (err) {
        console.error("unexpected payload", msg.data);
      }
    });
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
  color: var(--fg);
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
  font-size: 0.9rem;
}
</style>
