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
        <span v-if="spinner" class="spinner">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="icon icon-tabler icon-tabler-hourglass"
            width="24"
            height="24"
            viewBox="0 0 24 24"
            stroke-width="2"
            stroke="currentColor"
            fill="none"
            stroke-linecap="round"
            stroke-linejoin="round"
          >
            <path d="M6.5 7h11" />
            <path d="M6.5 17h11" />
            <path
              d="M6 20v-2a6 6 0 1 1 12 0v2a1 1 0 0 1 -1 1h-10a1 1 0 0 1 -1 -1z"
            />
            <path
              d="M6 4v2a6 6 0 1 0 12 0v-2a1 1 0 0 0 -1 -1h-10a1 1 0 0 0 -1 1z"
            />
          </svg>
        </span>
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

const query = ref("");
const response = ref(`# ${new Date()}`);
const spinner = ref(false);

onMounted(() => {
  document.querySelector("input").focus();
});

function publish() {
  let q = query.value;
  setTimeout(() => {
    spinner.value = true;
    response.value = "...";
  }, 1);
  setTimeout(() => {
    response.value = publishQuery(q);
    query.value = "";
    spinner.value = false;
    document.querySelector("input").focus();
  }, 10);
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

.spinner {
  color: var(--action);
}
.spinner > svg {
  transform-origin: 12px 12px;
  animation: rotation 0.8s infinite both;
}
</style>
