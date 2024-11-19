import{_ as o,c as t,a2 as s,o as a}from"./chunks/framework.DYGJpL-T.js";const h=JSON.parse('{"title":"Query/Response Protocol","description":"","frontmatter":{},"headers":[],"relativePath":"reference/the-query-response-protocol.md","filePath":"reference/the-query-response-protocol.md","lastUpdated":1720550803000}'),r={name:"reference/the-query-response-protocol.md"};function n(i,e,d,l,u,c){return a(),t("div",null,e[0]||(e[0]=[s('<h1 id="query-response-protocol" tabindex="-1">Query/Response Protocol <a class="header-anchor" href="#query-response-protocol" aria-label="Permalink to &quot;Query/Response Protocol&quot;">​</a></h1><p>I&#39;d like to describe the Query/Response pattern in a more formal but not too strict way, since it&#39;s not in any way some type of <em>standard</em> or <em>protocol</em>. This is a pattern derived from the general idea of expressing a <em>need</em> or <em>demand</em>, as previously told. It is shaped here, into a specific version, or flavour, in the <strong>Query/Response pattern</strong>. It simply contains my recommendations and suggestions on rules or principles to follow.</p><p>Please, take what you like, leave the rest, and extend as you seem fit.</p><p>Use of the keywords: &quot;MUST&quot;, &quot;MUST NOT&quot;, &quot;REQUIRED&quot;, &quot;SHALL&quot;, &quot;SHALL NOT&quot;, &quot;SHOULD&quot;, &quot;SHOULD NOT&quot;, &quot;RECOMMENDED&quot;, &quot;MAY&quot;, and &quot;OPTIONAL&quot; are intended to follow the definitions of <a href="https://www.ietf.org/rfc/rfc2119.txt" target="_blank" rel="noreferrer">RFC 2119</a>.</p><h2 id="intent" tabindex="-1">Intent <a class="header-anchor" href="#intent" aria-label="Permalink to &quot;Intent&quot;">​</a></h2><p>The Query/Response pattern aims to describe a model for information sharing in a distributed system. It does so by using strong decoupling of system actors and establishing asynchronous message-based high-level data exchange, as the only means of communication.</p><p>The following specifications tries to provide a set of rules and guides, which can be used as an authoritative source for developer, implementing the pattern.</p><h2 id="components-and-collaborators" tabindex="-1">Components and Collaborators <a class="header-anchor" href="#components-and-collaborators" aria-label="Permalink to &quot;Components and Collaborators&quot;">​</a></h2><table tabindex="0"><thead><tr><th>Name</th><th>Type</th><th>Description</th></tr></thead><tbody><tr><td><code>Query</code></td><td>message</td><td>Very small, published notification.</td></tr><tr><td><code>Response</code></td><td>message</td><td>Carries information as payload.</td></tr><tr><td><code>Address</code></td><td>location</td><td>Reference to &quot;a mailbox&quot;</td></tr><tr><td><code>Publisher</code></td><td>actor</td><td>Initiates <em>publish</em> method calls.</td></tr><tr><td><code>Consumer</code></td><td>actor</td><td>Accepts <em>consume</em> method calls.</td></tr></tbody></table><h3 id="query" tabindex="-1"><code>Query</code> <a class="header-anchor" href="#query" aria-label="Permalink to &quot;`Query`&quot;">​</a></h3><p>A notification that expresses a specific <em>need</em> or <em>whish</em>, which can be fulfilled by a response, published to a specified return address. The query MUST state its <em>need</em> or <em>whish</em> in an interpretable way. It may use any suitable syntax, semantics or language. Most commonly a simple string or term is used, similar to a message subject, -name or an event <em>routing-key</em>. A query MUST specify an address for responses, which SHOULD be <em>appropriate</em> for the stated query and, technically <em>available</em>, as the query is created.</p><div class="tip custom-block"><p class="custom-block-title">Recommendation</p><p>I very much recommend creating queries with expressions or terms from a domain specific, or ubiquitous language. This allows for broader understanding and involvement of stakeholders. Keeping queries human readable makes sense. It&#39;s often desirable to use structured terms, with semantics, such as filters or parameters. This is quite common and not at all bad.</p></div><h3 id="response" tabindex="-1"><code>Response</code> <a class="header-anchor" href="#response" aria-label="Permalink to &quot;`Response`&quot;">​</a></h3><p>A notification, published, as a response to a query, optionally carrying an information- or data-payload. A response MUST NOT be sent without an intent to <em>answer</em> a specific query (use event notifications for that). The response MUST be sent to the address of the query it responds to, without manipulating it. A response SHOULD carry an appropriate information- or data-payload, with the intent to answer the query it responds to. Note that this is not a strict requirement. Responses SHOULD be sent within an appropriate time frame of seeing a query.</p><div class="tip custom-block"><p class="custom-block-title">TIP</p><p>In most cases it&#39;s desirable to publish a response as quick as possible, after consuming a query.</p></div><h3 id="address" tabindex="-1"><code>Address</code> <a class="header-anchor" href="#address" aria-label="Permalink to &quot;`Address`&quot;">​</a></h3><p>Describes and designates an addressable <em>location</em> with the capability to receive and handle responses. Typically a messaging <em>mailbox</em> or a queue. The address MUST NOT describe a system actor or collaborator, but instead ensure decoupling between a publisher and a consumer.</p><div class="tip custom-block"><p class="custom-block-title">TIP</p><p>In messaging or broker based systems, the address is typically a routing key, topic or a queue-name.</p></div><h3 id="publisher" tabindex="-1"><code>Publisher</code> <a class="header-anchor" href="#publisher" aria-label="Permalink to &quot;`Publisher`&quot;">​</a></h3><p>An actor that initiates the publishing of a notification, either a query or a response depending on its current role. The publisher MUST NOT be responsible for the arrival of any published information. Publishers MUST NOT know any consumers.</p><div class="info custom-block"><p class="custom-block-title">Note</p><p>The concrete <em>interpolated</em> roles <code>Query-Publisher</code> and <code>Response-Publisher</code>, does not have to be bound to a single or unique actor.</p></div><p><em>It is open for the implementation of the Query/Response pattern to solve or choose how it ensures delivery of messages, e.g. using a broker- or queue- based messaging system or some other solution for asynchronous communication.</em></p><h3 id="consumer" tabindex="-1"><code>Consumer</code> <a class="header-anchor" href="#consumer" aria-label="Permalink to &quot;`Consumer`&quot;">​</a></h3><p>An actor that willingly yields to the consumption of notifications, from some external source, either a response or a query depending on its current role. Consumers MUST NOT know any publishers.</p><div class="info custom-block"><p class="custom-block-title">Note</p><p>The concrete <em>interpolated</em> roles <code>Query-Consumer</code> and <code>Response-Consumer</code>, does not have to be bound to a single or unique actor.</p></div><h2 id="methods-and-actions" tabindex="-1">Methods and Actions <a class="header-anchor" href="#methods-and-actions" aria-label="Permalink to &quot;Methods and Actions&quot;">​</a></h2><p><em>Nothing in the Query/Response pattern is synchronous, or based on the notion of guaranteed delivery (or only-once semantics). The following structured step-by-step description is only for documentation purposes, and does not, in any way, define a sequence which can be relied upon.</em></p><h3 id="prepare-address" tabindex="-1">Prepare <code>Address</code> <a class="header-anchor" href="#prepare-address" aria-label="Permalink to &quot;Prepare `Address`&quot;">​</a></h3><p>Before publishing a query, the query publisher SHOULD ensure that an appropriate address, specified for the query, can be handled.</p><div class="info custom-block"><p class="custom-block-title">Note</p><p>Implementations are free to use a best-effort approach. It may be that the only option is to use short-lived or temporary resources, which may or may not fail to be allocated. Therefore there&#39;s no strict requirement to ensure that the address can be handled.</p></div><h3 id="publish-query" tabindex="-1">Publish <code>Query</code> <a class="header-anchor" href="#publish-query" aria-label="Permalink to &quot;Publish `Query`&quot;">​</a></h3><p>The query publisher can, at any time, choose to publish a query. No ACK or NACK will be provided and the query publisher MUST NOT assume that the query has been consumed, or that a response will be returned at this time. The publisher SHOULD consider the case where the query is lost, examine options to detect and repair this, if possible; <em>timeouts, retries or fallbacks are perhaps options to investigate</em>.</p><h3 id="consume-query" tabindex="-1">Consume <code>Query</code> <a class="header-anchor" href="#consume-query" aria-label="Permalink to &quot;Consume `Query`&quot;">​</a></h3><p>A query consumer, that is willingly listening for queries, may at any time receive, and choose to handle a query. Consuming queries is an unbound operation. The consumer SHOULD handle queries with an intent to provide a response, or ignore the query. A consumer MAY decide to publish none, one or any number of responses to the query - it is optional. A consumer MAY at any time choose to stop listening for queries.</p><div class="tip custom-block"><p class="custom-block-title">TIP</p><p>Please note that the Query/Response pattern does not protect against query consumers with harmful intent. Implementations should consider issues like security, encryption and trust as extensions to it.</p></div><h3 id="publish-response" tabindex="-1">Publish <code>Response</code> <a class="header-anchor" href="#publish-response" aria-label="Permalink to &quot;Publish `Response`&quot;">​</a></h3><p>A response publisher MUST use the provided address of the query it responds to, when publishing responses. No ACK or NACK will be provided and the publisher MUST NOT assume that the response has been delivered, arrived properly or consumed.</p><h3 id="consume-response" tabindex="-1">Consume <code>Response</code> <a class="header-anchor" href="#consume-response" aria-label="Permalink to &quot;Consume `Response`&quot;">​</a></h3><p>A response consumer, listening for responses at a previously created address, MAY at any time receive one or several responses - or not at all. Consuming responses is an unbounded operation. Any received response MAY have a payload or body of information. The consumer SHOULD assert and validate any transferred information with great care. A consumer MAY at any time choose to stop listening for responses.</p>',39)]))}const m=o(r,[["render",n]]);export{h as __pageData,m as default};
