import { useToggle } from "@vueuse/core";
import { defineStore } from "pinia";
import { ref } from "vue";

export const useColorPaletteStore = defineStore("color-palette", () => {
  const show = ref(false);
  const toggle = useToggle(show);
  return {
    show,
    toggle,
  };
});
