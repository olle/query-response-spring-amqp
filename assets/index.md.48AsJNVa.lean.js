import{c as t,a2 as n,o as a}from"./chunks/framework.DYGJpL-T.js";const l=JSON.parse('{"title":"","description":"","frontmatter":{"layout":"home","hero":{"name":"Query/Response","text":"For Spring® AMQP","tagline":"A messaging pattern for building highly decoupled evolving service architectures.","actions":[{"theme":"brand","text":"What is Query/Response?","link":"/guide/what-is-query-response"},{"theme":"alt","text":"Getting started","link":"/guide/getting-started"},{"theme":"alt","text":"GitHub","link":"https://github.com/olle/query-response-spring-amqp"}]},"features":[{"icon":"<svg  xmlns=\\"http://www.w3.org/2000/svg\\"  width=\\"40\\"  height=\\"40\\"  viewBox=\\"0 0 24 24\\"  fill=\\"none\\"  stroke=\\"currentColor\\"  stroke-width=\\"1\\"  stroke-linecap=\\"round\\"  stroke-linejoin=\\"round\\"  class=\\"icon icon-tabler icons-tabler-outline icon-tabler-shield-bolt\\"><path stroke=\\"none\\" d=\\"M0 0h24v24H0z\\" fill=\\"none\\"/><path d=\\"M13.342 20.566c-.436 .17 -.884 .315 -1.342 .434a12 12 0 0 1 -8.5 -15a12 12 0 0 0 8.5 -3a12 12 0 0 0 8.5 3a12 12 0 0 1 .117 6.34\\" /><path d=\\"M19 16l-2 3h4l-2 3\\" /></svg>","title":"Resilient","details":"Build safer and more resilient distributed services."},{"icon":"<svg  xmlns=\\"http://www.w3.org/2000/svg\\"  width=\\"40\\"  height=\\"40\\"  viewBox=\\"0 0 24 24\\"  fill=\\"none\\"  stroke=\\"currentColor\\"  stroke-width=\\"1\\"  stroke-linecap=\\"round\\"  stroke-linejoin=\\"round\\"  class=\\"icon icon-tabler icons-tabler-outline icon-tabler-messages\\"><path stroke=\\"none\\" d=\\"M0 0h24v24H0z\\" fill=\\"none\\"/><path d=\\"M21 14l-3 -3h-7a1 1 0 0 1 -1 -1v-6a1 1 0 0 1 1 -1h9a1 1 0 0 1 1 1v10\\" /><path d=\\"M14 15v2a1 1 0 0 1 -1 1h-7l-3 3v-10a1 1 0 0 1 1 -1h2\\" /></svg>","title":"Asynchronous","details":"Get the benefits of an always async approach to data exchange."},{"icon":"<svg  xmlns=\\"http://www.w3.org/2000/svg\\"  width=\\"40\\"  height=\\"40\\"  viewBox=\\"0 0 24 24\\"  fill=\\"none\\"  stroke=\\"currentColor\\"  stroke-width=\\"1\\"  stroke-linecap=\\"round\\"  stroke-linejoin=\\"round\\"  class=\\"icon icon-tabler icons-tabler-outline icon-tabler-network\\"><path stroke=\\"none\\" d=\\"M0 0h24v24H0z\\" fill=\\"none\\"/><path d=\\"M6 9a6 6 0 1 0 12 0a6 6 0 0 0 -12 0\\" /><path d=\\"M12 3c1.333 .333 2 2.333 2 6s-.667 5.667 -2 6\\" /><path d=\\"M12 3c-1.333 .333 -2 2.333 -2 6s.667 5.667 2 6\\" /><path d=\\"M6 9h12\\" /><path d=\\"M3 20h7\\" /><path d=\\"M14 20h7\\" /><path d=\\"M10 20a2 2 0 1 0 4 0a2 2 0 0 0 -4 0\\" /><path d=\\"M12 15v3\\" /></svg>","title":"Decoupled","details":"Ensure decoupling of components. Create scalable solutions."},{"icon":"<svg  xmlns=\\"http://www.w3.org/2000/svg\\"  width=\\"40\\"  height=\\"40\\"  viewBox=\\"0 0 24 24\\"  fill=\\"none\\"  stroke=\\"currentColor\\"  stroke-width=\\"1\\"  stroke-linecap=\\"round\\"  stroke-linejoin=\\"round\\"  class=\\"icon icon-tabler icons-tabler-outline icon-tabler-share\\"><path stroke=\\"none\\" d=\\"M0 0h24v24H0z\\" fill=\\"none\\"/><path d=\\"M6 12m-3 0a3 3 0 1 0 6 0a3 3 0 1 0 -6 0\\" /><path d=\\"M18 6m-3 0a3 3 0 1 0 6 0a3 3 0 1 0 -6 0\\" /><path d=\\"M18 18m-3 0a3 3 0 1 0 6 0a3 3 0 1 0 -6 0\\" /><path d=\\"M8.7 10.7l6.6 -3.4\\" /><path d=\\"M8.7 13.3l6.6 3.4\\" /></svg>","title":"Evolving","details":"Be better prepared for system evolution."}]},"headers":[],"relativePath":"index.md","filePath":"index.md","lastUpdated":1720560186000}'),o={name:"index.md"},h=Object.assign(o,{setup(r){return(i,e)=>(a(),t("div",null,e[0]||(e[0]=[n('<h2 id="change-the-way-you-think-and-design-by-using-query-response-for-spring-amqp" tabindex="-1">Change the way you think and design, by using Query/Response for Spring AMQP. <a class="header-anchor" href="#change-the-way-you-think-and-design-by-using-query-response-for-spring-amqp" aria-label="Permalink to &quot;Change the way you think and design, by using Query/Response for Spring AMQP.&quot;">​</a></h2><p><em>Sometime around 2015 I came across a presentation with <a href="https://twitter.com/fgeorge52" target="_blank" rel="noreferrer">Fred George</a>, about the <a href="https://youtu.be/yPf5MfOZPY0" target="_blank" rel="noreferrer">Challenges in Implementing Microservices</a>. It&#39;s a great talk, with lots of good and relevant information. Experience comes from learning through failures, and at this point in time I had just learned a hard lesson about the problems with distributed services and blocking API calls. I had seen how latencies would go up and availability go down the drain, as calls from service A to B were actually depending on service B calling service C, calling service D. It was a mess.</em></p><p><em>In his talk George lands at the question &quot;Synchronous or Asynchronous?&quot; and proceeds to describe, what he calls, the &quot;Needs Pattern&quot;. Service A would, instead of calling service B, publish a <strong>query</strong>, and service B would listen for it and send back a <strong>response</strong>. After hearing this I began to think a lot about the effects of moving to asynchronous communication between services. There was clearly a lot more there than just decoupling. Something more fundamental.</em></p><p><em>The <strong>Query/Response</strong> pattern, that I arrived at, challenges developers to really think hard about the responsibilities and autonomy of services. It provides very few guarantees, which will force decisions around resilience and availability at a much earlier stage in the design and development process. It literally turns things around - an inversion of responsibility - which I truly believe we can benefit from.</em></p><p><em>Olle Törnström, 2019</em></p>',5)])))}});export{l as __pageData,h as default};
