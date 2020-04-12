import Vue from "vue";
//import App from "./App.vue";

import QrNav from "./components/QrNav.vue";

import HomeIcon from "./components/HomeIcon.vue";
import SunIcon from "./components/SunIcon.vue";
import MoonIcon from "./components/MoonIcon.vue";
import LiveIcon from "./components/LiveIcon.vue";
import QueriesIcon from "./components/QueriesIcon.vue";
import TopologyIcon from "./components/TopologyIcon.vue";
import LoggingIcon from "./components/LoggingIcon.vue";
import SettingsIcon from "./components/SettingsIcon.vue";

import Overview from "./Overview.vue";
import Live from "./Live.vue";
// import Queries from "./Queries.vue";
// import Topology from "./Topology.vue";
// import Logging from "./Logging.vue";
// import Settings from "./Settings.vue";

const routes = {
  "/": Overview,
  "/live": Live,
  // '/queries': Queries,
  // '/topology': Topology,
  // '/logging': Logging,
  // '/settings': Settings,
};

const app = new Vue({
  el: "#app",
  data: {
    currentRoute: window.location.pathname,
  },
  computed: {
    currentPage: () => {
      console.log(this.currentRoute);
      return routes[this.currentRoute] || routes["/"];
    },
  },
  components: {
    HomeIcon,
    SunIcon,
    MoonIcon,
    LiveIcon,
    QueriesIcon,
    TopologyIcon,
    LoggingIcon,
    SettingsIcon,
  },
});

// Connect Browser History to the app routing
window.addEventListener("popstate", () => {
  app.currentRoute = window.location.pathname;
});

// // ELEMENTS -------------------------------------------------------------------

// const selectLiByOnClick = (fun) =>
//   document.querySelector(`li[onclick*='${fun}']`);

// const $TOGGLE = selectLiByOnClick("toggleDarkTheme");
// const $HOME = selectLiByOnClick("overview");
// const $LIVE = selectLiByOnClick("live");
// const $QUERIES = selectLiByOnClick("queries");
// const $TOPOLOGY = selectLiByOnClick("topology");
// const $LOGGING = selectLiByOnClick("logging");
// const $SETTINGS = selectLiByOnClick("settings");

// // HTML/DOM UTILS -------------------------------------------------------------

// const h2e = (html) => {
//   var template = document.createElement("template");
//   html = html.trim();
//   template.innerHTML = html;
//   return template.content.firstChild;
// };

// // NAVIGATION -----------------------------------------------------------------

// const addNavigation = ($el, icon, title) => {
//   $el.appendChild(icon);
//   $el.appendChild(h2e(`<h2>${title}</h2>`));
// };

// addNavigation($HOME, $HOME_ICON, "Overview");
// addNavigation($LIVE, $LIVE_ICON, "Activity");
// addNavigation($QUERIES, $QUERIES_ICON, "Queries Insight");
// addNavigation($TOPOLOGY, $TOPOLOGY_ICON, "Q/R Topology");
// addNavigation($LOGGING, $LOGGING_ICON, "Logging");
// addNavigation($SETTINGS, $SETTINGS_ICON, "Settings");

// // DARK/LIGHT MODE ------------------------------------------------------------

// const $HTML = document.querySelector("html");

// const toggleTheme = (source) => {
//   let $htmlWithDarkTheme = document.querySelector("html[data-theme]");
//   if ($htmlWithDarkTheme) {
//     clearDarkTheme($htmlWithDarkTheme, $TOGGLE);
//   } else {
//     setDarkTheme($HTML, $TOGGLE);
//   }
// };

// const clearDarkTheme = ($html, $toggle) => {
//   $html.removeAttribute("data-theme");
//   $toggle.firstChild && $toggle.removeChild($toggle.firstChild);
//   $toggle.appendChild($MOON_ICON);
//   window.localStorage.setItem("theme", "light");
// };

// const setDarkTheme = ($html, $toggle) => {
//   $html.setAttribute("data-theme", "dark");
//   $toggle.firstChild && $toggle.removeChild($toggle.firstChild);
//   $toggle.appendChild($SUN_ICON);
//   window.localStorage.setItem("theme", "dark");
// };

// let theme = window.localStorage.getItem("theme");
// if (theme === "dark") {
//   setDarkTheme($HTML, $TOGGLE);
// } else if (theme === "light") {
//   clearDarkTheme($HTML, $TOGGLE);
// } else if (window.matchMedia("(prefers-color-scheme: dark)").matches) {
//   setDarkTheme($HTML, $TOGGLE);
// } else {
//   clearDarkTheme($HTML, $TOGGLE);
// }

// window.matchMedia("(prefers-color-scheme: dark)").addListener((evt) => {
//   if (evt.matches) {
//     setDarkTheme($HTML, $TOGGLE);
//   } else {
//     clearDarkTheme($HTML, $TOGGLE);
//   }
// });

// // METRICS --------------------------------------------------------------------

// let metrics = {
//   success: {
//     rate: 0,
//     grade: 0.0,
//     queries: 0,
//     responses: 0,
//     fallbacks: 0,
//   },
//   latency: {
//     average: 0,
//     unit: "ms",
//     grade: 0.0,
//   },
//   throughput: {
//     average: 0,
//     unit: "s",
//     queries: 0,
//     responses: 0,
//     fallbacks: 0,
//   },
// };

// const n2t = (num) => {
//   if (typeof num !== "number" || num < 1) {
//     return "0";
//   } else if (num > 99999999999999999999) {
//     return ">100E";
//   }
//   let i = Math.floor(Math.log(num) / Math.log(1000));
//   let q = 10 ** Math.min(3, i);
//   let value = Math.round((num / 1000 ** i + Number.EPSILON) * q) / q;
//   let suffix = ["", "k", "M", "G", "T", "P", "E"][i];
//   return `${value}${suffix}`;
// };

// // EXPORTS --------------------------------------------------------------------

// window.toggleDarkTheme = toggleTheme;
// window.n2t = n2t;
