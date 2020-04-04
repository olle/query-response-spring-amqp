const toggleTheme = () => {
  if (($html = document.querySelector("html[data-theme]"))) {
    clearTheme($html);
  } else {
    setTheme(document.querySelector("html"));
  }
};

const clearTheme = el => {
  el.removeAttribute("data-theme");
  window.localStorage.removeItem("theme");
};

const setTheme = el => {
  el.setAttribute("data-theme", "dark");
  window.localStorage.setItem("theme", "dark");
};

if (window.localStorage.getItem("theme")) {
  setTheme(document.querySelector("html"));
}

var toggle = toggleTheme;
