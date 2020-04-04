const TOGGLE = document.querySelector("li[onclick*='toggleDarkTheme']");
const HOME = document.querySelector("li[onclick*='overview']");

const toggleTheme = source => {
  if (($html = document.querySelector("html[data-theme]"))) {
    clearDarkTheme($html, TOGGLE);
  } else {
    setDarkTheme(document.querySelector("html"), TOGGLE);
  }
};

const clearDarkTheme = ($html, $toggle) => {
  $html.removeAttribute("data-theme");
  $toggle.firstChild && $toggle.removeChild($toggle.firstChild);
  $toggle.appendChild(MOON_ICON);
  window.localStorage.removeItem("theme");
};

const setDarkTheme = ($html, $toggle) => {
  $html.setAttribute("data-theme", "dark");
  $toggle.firstChild && $toggle.removeChild($toggle.firstChild);
  $toggle.appendChild(SUN_ICON);
  window.localStorage.setItem("theme", "dark");
};

const h2e = html => {
  var template = document.createElement("template");
  html = html.trim();
  template.innerHTML = html;
  return template.content.firstChild;
};

const addNavigation = (el, icon, title) => {
  el.appendChild(icon);
  el.appendChild(h2e(`<h2>${title}</h2>`));
};

// SVG ICONS ------------------------------------------------------------------

const SUN_ICON = h2e(`
<svg xmlns="http://www.w3.org/2000/svg" class="icon icon-tabler icon-tabler-sun" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
<path stroke="none" d="M0 0h24v24H0z"/>
<circle cx="12" cy="12" r="4" />
<path d="M3 12h1M12 3v1M20 12h1M12 20v1M5.6 5.6l.7 .7M18.4 5.6l-.7 .7M17.7 17.7l.7 .7M6.3 17.7l-.7 .7" />
</svg>`);

const MOON_ICON = h2e(`
<svg xmlns="http://www.w3.org/2000/svg" class="icon icon-tabler icon-tabler-moon" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
<path stroke="none" d="M0 0h24v24H0z"/>
<path d="M16.2 4a9.03 9.03 0 1 0 3.9 12a6.5 6.5 0 1 1 -3.9 -12" />
</svg>`);

const HOME_ICON = h2e(`
<svg xmlns="http://www.w3.org/2000/svg" class="icon icon-tabler icon-tabler-home-2" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
<path stroke="none" d="M0 0h24v24H0z"/>
<polyline points="5 12 3 12 12 3 21 12 19 12" />
<path d="M5 12v7a2 2 0 0 0 2 2h10a2 2 0 0 0 2 -2v-7" />
<rect x="10" y="12" width="4" height="4" />
</svg>`);

// MAIN -----------------------------------------------------------------------

addNavigation(HOME, HOME_ICON, "Overview");

const HTML = document.querySelector("html");

if (window.localStorage.getItem("theme")) {
  setDarkTheme(HTML, TOGGLE);
} else {
  clearDarkTheme(HTML, TOGGLE);
}

window.matchMedia("(prefers-color-scheme: dark)").addListener(evt => {
  if (evt.matches) {
    setDarkTheme(HTML, TOGGLE);
  } else {
    clearDarkTheme(HTML, TOGGLE);
  }
});

// EXPORTS --------------------------------------------------------------------

var toggleDarkTheme = toggleTheme;
