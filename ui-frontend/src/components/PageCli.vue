<template>
  <article>
    <h1>Query Command Line Interface</h1>
    <form v-on:submit.prevent="publish">
      <div class="input-wrapper">
        <label>&gt;</label>
        <input
          v-model="query"
          :autofocus="'autofocus'"
          placeholder="query [timeout [limit]]"
        />
        <IconSpinner v-if="spinner" />
      </div>
    </form>
    <div class="output-wrapper">
      <pre><code>{{ response }}</code></pre>
    </div>
  </article>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { publishQuery } from "../api";

import IconSpinner from "./IconSpinner.vue";

const query = ref("");
const response = ref(`# ${new Date()}`);
const spinner = ref(false);

onMounted(() => {
  document.querySelector("input").focus();
});

function reset(value) {
  response.value = value || `# ERROR ${new Date()}`;
  query.value = "";
  spinner.value = false;
  document.querySelector("input").focus();
}

function publish() {
  let q = query.value;
  setTimeout(() => {
    spinner.value = true;
    response.value = "...";
  }, 1);
  try {
    publishQuery(q).then(
      (resp) => {
        if (resp.ok) {
          resp.json().then((json) => {
            reset(JSON.stringify(json, null, 2));
          });
        } else {
          reset();
        }
      },
      (err) => reset()
    );
  } catch (err) {
    reset();
  }
}
</script>

<style scoped>
.input-wrapper {
  width: 100%;
  display: flex;
  flex-direction: row;
  align-items: center;
}
.input-wrapper > input {
  flex: 1;
}

.output-wrapper {
  text-align: left;
  margin-top: 0.1rem;
}

.input-wrapper,
.output-wrapper {
  font-size: 1.1rem;
  background: var(--panel);
  color: var(--fg);
  border: var(--border);
  padding: 0.8rem 1rem;
  font-family: monospace;
}

input {
  font-size: inherit;
  font-family: inherit;
  background: inherit;
  color: inherit;
  border: none;
  outline: none;
}
</style>
