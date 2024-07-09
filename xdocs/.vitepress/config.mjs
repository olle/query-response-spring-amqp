import { defineConfig } from "vitepress";

export default defineConfig({
  title: "Query/Response",
  description: "A VitePress Site",
  lastUpdated: true,
  themeConfig: {
    search: {
      provider: "local",
    },
    nav: [{ text: "Home", link: "/" }],
    sidebar: [
      {
        text: "Guide",
        items: [
          {
            text: "What is Query/Response",
            link: "/guide/what-is-query-response",
          },
          { text: "Getting started", link: "/guide/getting-started" },
          { text: "Example revisited", link: "/guide/the-example-revisited" },
        ],
      },
      {
        text: "Reference",
        items: [
          {
            text: "Developers Reference",
            link: "/reference/developers-reference",
          },
          {
            text: "Query/Response Protocol",
            link: "/reference/the-query-response-protocol",
          },
          {
            text: "Maturity Model",
            link: "/reference/query-response-maturity-model",
          },
        ],
      },
    ],
    socialLinks: [
      {
        icon: "github",
        link: "https://github.com/olle/query-response-spring-amqp",
      },
    ],
    footer: {
      message: "Published under the Apache-2.0 license",
      copyright: `Copyright © 2019-${new Date().getFullYear()} Olle Törnström and all other contributors.`,
    },
  },
});
