import{B as i}from"./chunks/attributes.BUGtF-W3.js";import{c as n,j as e,a as s,t,a3 as a,o as l}from"./chunks/framework.Bsyxd66g.js";const h=e("h1",{id:"developers-reference",tabindex:"-1"},[s("Developers Reference "),e("a",{class:"header-anchor",href:"#developers-reference","aria-label":'Permalink to "Developers Reference"'},"​")],-1),o=e("h2",{id:"overview",tabindex:"-1"},[s("Overview "),e("a",{class:"header-anchor",href:"#overview","aria-label":'Permalink to "Overview"'},"​")],-1),r=e("em",null,"streams",-1),d=e("em",null,"futures",-1),p=e("em",null,"promises",-1),k=e("em",null,"normal",-1),c=e("a",{href:"https://en.wikipedia.org/wiki/Distributed_computing",target:"_blank",rel:"noreferrer"},"distributed systems",-1),u=e("p",null,[s("In the next couple of sections we'll look closer at the "),e("code",null,"QueryBuilder"),s(" and the "),e("code",null,"ResponseBuilder"),s(" types. We'll discuss how to use them in detail, and try to explain the concepts behind them, and the intention of their implementation.")],-1),E=e("h2",{id:"querybuilder",tabindex:"-1"},[e("code",null,"QueryBuilder"),s(),e("a",{class:"header-anchor",href:"#querybuilder","aria-label":'Permalink to "`QueryBuilder`"'},"​")],-1),m=e("code",null,"QueryBuilder",-1),g=e("code",null,"@EnableQueryResponse",-1),y=a(`<p>We recommend injecting it via the component constructor, and keeping it as a private field. The <code>findAuthors()</code> method below, shows how to access the <code>queryBuilder</code> field in order to publish a query.</p><div class="language-java vp-adaptive-theme"><button title="Copy Code" class="copy"></button><span class="lang">java</span><pre class="shiki shiki-themes github-light github-dark vp-code" tabindex="0"><code><span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">@</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">Component</span></span>
<span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">public</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;"> class</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> Authors</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;"> {</span></span>
<span class="line"></span>
<span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">    private</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;"> final</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;"> QueryBuilder queryBuilder;</span></span>
<span class="line"></span>
<span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">    public</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> Authors</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(QueryBuilder </span><span style="--shiki-light:#E36209;--shiki-dark:#FFAB70;">queryBuilder</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">) {</span></span>
<span class="line"><span style="--shiki-light:#005CC5;--shiki-dark:#79B8FF;">        this</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">.queryBuilder </span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">=</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;"> queryBuilder;</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">    }</span></span>
<span class="line"></span>
<span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">    public</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;"> Collection&lt;</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">String</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">&gt; </span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">findAuthors</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">() {</span></span>
<span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">        return</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;"> queryBuilder.</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">queryFor</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">&quot;authors&quot;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">, String.class)</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">            .</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">waitingFor</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#005CC5;--shiki-dark:#79B8FF;">800</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">)</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">            .</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">orEmpty</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">();</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">    }</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">}</span></span></code></pre></div><p>In the example above, the published query is defined by the string <strong>term</strong><code>&quot;authors&quot;</code>. This is how the most basic contract of Query/Response is defined. Any string or text term may be published as a query.</p>`,3),b=e("code",null,"String.class",-1),f=a('<p>Queries are built and published using the <code>queryFor(..)</code> <em>initial</em> method. Any following call to one of the <em>terminal</em> methods <code>orEmpty()</code>, <code>orDefaults(..)</code> and <code>orThrows(..)</code> will build and execute the query, and block on the calling thread.</p><p>Since the call above to <code>orEmpty()</code> blocks the thread, users have to specify one or more query <em>conditionals</em>. In the example above, the call to <code>waitingFor(..)</code> defines that the call will block for around 800 milliseconds.</p><p>Constructing queries with the <code>QueryBuilder</code> revolves around creating a composition of <em>initial</em>, <em>conditional</em>, an optional <em>informal</em> and exactly one <em>terminal</em> method call. In the table below is a short review of the different builder methods and their types.</p><h3 id="querybuilder-fluid-api-method-types" tabindex="-1"><code>QueryBuilder</code> fluid API method types <a class="header-anchor" href="#querybuilder-fluid-api-method-types" aria-label="Permalink to &quot;`QueryBuilder` fluid API method types&quot;">​</a></h3><table tabindex="0"><thead><tr><th>Method</th><th>Type</th><th>Description</th></tr></thead><tbody><tr><td><code>queryFor(..)</code></td><td><em>initial</em></td><td>Creates a new builder for a query</td></tr><tr><td><code>waitingFor(..)</code></td><td><em>conditional</em></td><td>Specifies the waiting/blocking condition</td></tr><tr><td><code>takingAtMost(..)</code></td><td><em>conditional</em></td><td>Sets a limit condition, a maximum</td></tr><tr><td><code>takingAtLeast(..)</code></td><td><em>conditional</em></td><td>Sets a limit condition, a minimum</td></tr><tr><td><code>orEmpty()</code></td><td><em>terminal</em></td><td>Terminates with empty, after conditionals are evaluated</td></tr><tr><td><code>orDefaults(..)</code></td><td><em>terminal</em></td><td>Terminates with some defaults, after conditionals are evaluated</td></tr><tr><td><code>orThrow(..)</code></td><td><em>terminal</em></td><td>Terminates by throwing, after conditionals are evaluated</td></tr><tr><td><code>onError(..)</code></td><td><em>informal</em></td><td>Allows for explicit logging etc.</td></tr></tbody></table><p>Let&#39;s take a closer look at each of the builder method types.</p><h3 id="initial-methods" tabindex="-1"><em>Initial</em> methods <a class="header-anchor" href="#initial-methods" aria-label="Permalink to &quot;_Initial_ methods&quot;">​</a></h3><p>At the moment there&#39;s only one <em>initial</em> method and it&#39;s declared as:</p><div class="language-java vp-adaptive-theme"><button title="Copy Code" class="copy"></button><span class="lang">java</span><pre class="shiki shiki-themes github-light github-dark vp-code" tabindex="0"><code><span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">public</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;"> &lt;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">T</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">&gt;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;"> ChainingQueryBuilder</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">&lt;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">T</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">&gt;</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> queryFor</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(String term, Class</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">&lt;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">T</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">&gt;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;"> type)</span></span></code></pre></div><p>So we can query for any <code>String</code> <strong>term</strong> and given the expected mapped or coerced <strong>type</strong> as a <code>Class&lt;T&gt;</code>. The returned <code>ChainingQueryBuilder&lt;T&gt;</code> provides the capabilities of the fluid API.</p><h3 id="conditional-methods" tabindex="-1"><em>Conditional</em> methods <a class="header-anchor" href="#conditional-methods" aria-label="Permalink to &quot;_Conditional_ methods&quot;">​</a></h3><p>All <em>conditional</em> properties can be composed together by the <code>QueryResponse</code> builder API, to define whether a query is successful or not. If an executing query is completed in a <em>successful</em> way, fulfilling the <em>conditionals</em>, it will return and not consume any more responses.</p><ul><li><p><code>waitingFor(..)</code> - defines a timeout <em>conditional</em>. The built query will evaluate as <em>successful</em> if <em>any</em> responses were consumed after the (approximate) given time limit has elapsed. There are a few different methods declared, to specify the timeout:</p><ul><li><code>waitingFor(long millis)</code></li><li><code>waitingFor(long amount, TemporalUnit timeUnit)</code></li><li><code>waitingFor(Duration duration)</code></li></ul></li><li><p><code>takingAtMost(int atMost)</code> - defines a limiting <em>conditional</em> on the aggregated number of received elements. The built query evaluates to <em>successful</em>, and returns, when the given amount is reached.</p></li><li><p><code>takingAtLeast(int atLeast)</code> - defines a minimum <em>conditional</em> on the number of received element. The built query evaluates to <em>successful</em>, only if at least the given number of elements can be consumed.</p></li></ul><h3 id="terminal-methods" tabindex="-1"><em>Terminal</em> methods <a class="header-anchor" href="#terminal-methods" aria-label="Permalink to &quot;_Terminal_ methods&quot;">​</a></h3><p>Only one <em>terminal</em> method can be invoked on the builder, per query. It will ensure that the query is built and executed. All <em>terminal</em> methods are declared to return <code>Collection&lt;T&gt;</code> where the type parameter <code>&lt;T&gt;</code> is given in the <em>initial</em> method <code>type</code> parameter.</p><ul><li><p><code>orEmpty()</code> - defines the query to return an empty <code>Collection</code> in case the <em>conditionals</em> do not evaluate to <em>successful</em>.</p></li><li><p><code>orDefaults(..)</code> - defines the query to return with some provided <em>defaults</em> in case the <em>conditionals</em> do not evaluate to <em>successful</em>. There are a couple different methods declared for defaults:</p><ul><li><code>orDefaults(Collection&lt;T&gt; defaults)</code> - set at <em>call-time</em>.</li><li><code>orDefaults(Supplier&lt;Collection&lt;T&gt;&gt; defaults)</code> - supplied lazily at <em>run-time</em>.</li></ul></li><li><p><code>orThrow(..)</code> - defines the query to throw an exception in case the <em>conditionals</em> do not evaluate to <em>successful</em>.</p></li></ul><div class="warning custom-block"><p class="custom-block-title">Careful</p><p>Note the difference in <em>call-time</em> and <em>response-time</em> - since the call to the <em>terminal</em> method is a blocking call, any fetched results as defaults are prepared as the query is built. In order to dynamically provide better defaults at <em>run-time</em>, use the lazy supplier instead.</p></div><h3 id="informal-methods" tabindex="-1"><em>Informal</em> methods <a class="header-anchor" href="#informal-methods" aria-label="Permalink to &quot;_Informal_ methods&quot;">​</a></h3><p>Currently there&#39;s only one <em>informal</em> builder method, allowing for extended logging or information capture, in case the query fails or an exception is thrown.</p><div class="language-java vp-adaptive-theme"><button title="Copy Code" class="copy"></button><span class="lang">java</span><pre class="shiki shiki-themes github-light github-dark vp-code" tabindex="0"><code><span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">public</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;"> ChainingQueryBuilder</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">&lt;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">T</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">&gt;</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> onError</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(Consumer</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">&lt;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">Throwable</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">&gt;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;"> handler)</span></span></code></pre></div><div class="tip custom-block"><p class="custom-block-title">TIP</p><p>Try to think more about how the <code>QueryBuilder</code> API covers the exceptional query-cases, as part of the composition of <em>conditionals</em>. If clients try to use <em>terminals</em> that provide sensible defaults, it may not be necessary to build other types of complex recovery or retries.</p></div><h3 id="querybuilder-examples" tabindex="-1"><code>QueryBuilder</code> examples <a class="header-anchor" href="#querybuilder-examples" aria-label="Permalink to &quot;`QueryBuilder` examples&quot;">​</a></h3><p>Below are some examples of how the different <code>QueryBuilder</code> API methods can be combined.</p><p>Using <code>takingAtMost(..)</code>, combined with <code>waitingFor(..)</code>, preserves system resources and the client can be protected from consuming too much data.</p><div class="language-java vp-adaptive-theme"><button title="Copy Code" class="copy"></button><span class="lang">java</span><pre class="shiki shiki-themes github-light github-dark vp-code" tabindex="0"><code><span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">return</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;"> queryBuilder.</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">queryFor</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">&quot;authors&quot;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">, String.class)</span></span>\n<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">                .</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">takingAtMost</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#005CC5;--shiki-dark:#79B8FF;">10</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">)</span></span>\n<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">                .</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">waitingFor</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#005CC5;--shiki-dark:#79B8FF;">800</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">)</span></span>\n<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">                .</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">orDefaults</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(Authors.</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">defaults</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">());</span></span></code></pre></div>',25),F=a(`<div class="language-java vp-adaptive-theme"><button title="Copy Code" class="copy"></button><span class="lang">java</span><pre class="shiki shiki-themes github-light github-dark vp-code" tabindex="0"><code><span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">return</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;"> queryBuilder.</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">queryFor</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">&quot;offers/rental&quot;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">, Offer.class)</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">                .</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">takingAtLeast</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#005CC5;--shiki-dark:#79B8FF;">10</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">)</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">                .</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">takingAtMost</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#005CC5;--shiki-dark:#79B8FF;">20</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">)</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">                .</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">waitingFor</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#005CC5;--shiki-dark:#79B8FF;">2</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">, ChronoUnit.SECONDS)</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">                .</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">orThrow</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(TooFewOffersConstraintException</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">::new</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">);</span></span></code></pre></div><p>The <em>informal</em> builder feature, allows for transparency into queries that may have to be observed.</p><div class="language-java vp-adaptive-theme"><button title="Copy Code" class="copy"></button><span class="lang">java</span><pre class="shiki shiki-themes github-light github-dark vp-code" tabindex="0"><code><span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">return</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;"> queryBuilder.</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">queryFor</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">&quot;offers/rental&quot;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">, NewOffer.class)</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">                .</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">takingAtLeast</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#005CC5;--shiki-dark:#79B8FF;">3</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">)</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">                .</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">waitingFor</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#005CC5;--shiki-dark:#79B8FF;">400</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">)</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">                .</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">onError</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(error </span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">-&gt;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;"> LOG.</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">error</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">&quot;Failure!&quot;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">, error))</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">                .</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">orThrow</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(TooFewOffersConstraintException</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">::new</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">);</span></span></code></pre></div><h2 id="responsebuilder" tabindex="-1"><code>ResponseBuilder</code> <a class="header-anchor" href="#responsebuilder" aria-label="Permalink to &quot;\`ResponseBuilder\`&quot;">​</a></h2>`,4),v=e("code",null,"ResponseBuilder",-1),C=a(`<p>It is also provided as a bean, when using the <code>@EnableQueryResponse</code> annotation in a Spring application. It can easily be injected as a dependency to provide access from methods in Spring components.</p><p>The <code>respondWithAuthors()</code> method below, shows how the injected builder is used to create a responding service. It is invoked by the Spring application context, on the <code>ApplicationReadyEvent</code> event.</p><div class="language-java vp-adaptive-theme"><button title="Copy Code" class="copy"></button><span class="lang">java</span><pre class="shiki shiki-themes github-light github-dark vp-code" tabindex="0"><code><span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">@</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">Component</span></span>
<span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">public</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;"> class</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> OnlyThreeAuthors</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;"> {</span></span>
<span class="line"></span>
<span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">    private</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;"> final</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;"> ResponseBuilder responseBuilder;</span></span>
<span class="line"></span>
<span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">    public</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> OnlyThreeAuthors</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(ResponseBuilder </span><span style="--shiki-light:#E36209;--shiki-dark:#FFAB70;">responseBuilder</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">) {</span></span>
<span class="line"><span style="--shiki-light:#005CC5;--shiki-dark:#79B8FF;">        this</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">.responseBuilder </span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">=</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;"> responseBuilder;</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">    }</span></span>
<span class="line"></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">    @</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">EventListener</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(ApplicationReadyEvent.class)</span></span>
<span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">    public</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;"> void</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> respondWithAuthors</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">() {</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">        responseBuilder.</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">respondTo</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">&quot;authors&quot;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">, String.class)</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">            .</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">withAll</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">()</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">            .</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">from</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">&quot;Tolkien&quot;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">, </span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">&quot;Lewis&quot;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">, </span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">&quot;Rowling&quot;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">);</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">    }</span></span>
<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">}</span></span></code></pre></div><p>In the example above the responding service is defined by calling the builder method <code>respondTo(..)</code> with the query <strong>term</strong> parameter <code>&quot;authors&quot;</code>. It will be bound to publish the given 3 authors as <code>String.class</code> entries, whenever it consumes a query for the matching string <strong>term</strong> <code>&quot;authors&quot;</code>.</p><p>This is the most basic premiss of Query/Response, that any string or text term may be interpreted as a query - it is however up to the response publisher to determine what the query means.</p><div class="tip custom-block"><p class="custom-block-title">TIP</p><p>We&#39;ve tried to provide information around the Query/Response <em>protocol</em> and philosophy in the later chapter on <a href="./the-query-response-protocol.html">The Query/Response Protocol</a>. Go there to find out more.</p></div><p>The second parameter is the the type of each element, that will be published in the response. It is given both as a type hint for the compiler, as well as a parameter to the data mapper. Here it&#39;s trivial, the three authors are given as <code>String.class</code> entries.</p>`,7),_={class:"info custom-block"},w=e("p",{class:"custom-block-title"},"Note",-1),B=e("code",null,"com.fasterxml.jackson.databind.ObjectMapper",-1),A=a('<p>Response publishers are built using the <code>respondTo(..)</code> <em>initial</em> method. Any following call to one of the <em>terminal</em> methods <code>from(..)</code> or <code>suppliedBy(..)</code> will create and register it, as its own consumer in another thread. The builder call returns immediately.</p><p>The <code>ResponseBuilder</code> comes with some methods to allow for <em>partitioning</em> or <em>batching</em>, which can be used to control the transfer of data to some degree.</p><p>The table below shows a summary of the builder methods and types.</p><h3 id="responsebuilder-fluid-api-method-types" tabindex="-1"><code>ResponseBuilder</code> fluid API method types <a class="header-anchor" href="#responsebuilder-fluid-api-method-types" aria-label="Permalink to &quot;`ResponseBuilder` fluid API method types&quot;">​</a></h3><table tabindex="0"><thead><tr><th>Method</th><th>Type</th><th>Description</th></tr></thead><tbody><tr><td><code>respondTo(..)</code></td><td><em>initial</em></td><td>Creates a new builder for a query</td></tr><tr><td><code>withAll()</code></td><td><em>batching</em></td><td>Specifies NO batches</td></tr><tr><td><code>withBatchesOf(..)</code></td><td><em>batching</em></td><td>Sets the batch size of responses</td></tr><tr><td><code>from(..)</code></td><td><em>terminal</em></td><td>Terminates with some given response data</td></tr><tr><td><code>suppliedBy(..)</code></td><td><em>terminal</em></td><td>Terminates with some supplied response data</td></tr></tbody></table><p>Let&#39;s take a closer look at each of the builder method types.</p><h3 id="initial-methods-1" tabindex="-1"><em>Initial</em> methods <a class="header-anchor" href="#initial-methods-1" aria-label="Permalink to &quot;_Initial_ methods&quot;">​</a></h3><p>At the moment there&#39;s only one <em>initial</em> method for building responses. It is declared as:</p><div class="language-java vp-adaptive-theme"><button title="Copy Code" class="copy"></button><span class="lang">java</span><pre class="shiki shiki-themes github-light github-dark vp-code" tabindex="0"><code><span class="line"><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">public</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;"> &lt;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">T</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">&gt;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;"> ChainingResponseBuilder</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">&lt;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">T</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">&gt;</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;"> respondTo</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(String term, Class</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">&lt;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">T</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">&gt;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;"> type)</span></span></code></pre></div><p>So we can create a response for any <code>String</code> <strong>term</strong> and declare that we intend to publish elements of some <strong>type</strong> given as a <code>Class&lt;T&gt;</code>. The returned <code>ChainingResponseBuilder&lt;T&gt;</code> provides the capabilities of the fluid API.</p><h3 id="batching-methods" tabindex="-1"><em>Batching</em> methods <a class="header-anchor" href="#batching-methods" aria-label="Permalink to &quot;_Batching_ methods&quot;">​</a></h3><p>Control over how response elements are published can be made by using the <em>batching</em> methods that the builder provides.</p><ul><li><p><code>withAll()</code> - defines that <strong>no</strong> batching should be used, and will publish all given elements, or try to drain a supplied <code>Iterator</code> all at once.</p></li><li><p><code>withBatchesOf(int size)</code> - defines a batch size, which the response publisher will use, to create a series of response messages, with up-to the given <code>size</code> of elements.</p></li></ul><h3 id="terminal-methods-1" tabindex="-1"><em>Terminal</em> methods <a class="header-anchor" href="#terminal-methods-1" aria-label="Permalink to &quot;_Terminal_ methods&quot;">​</a></h3><p>Only one <em>terminal</em> method can be called on the builder, per response. It will ensure that a responder is created and added as a query-consumer, a subscriber to the query <strong>term</strong> as a topic. It is not attached to the calling thread, so the builder call always returns after the <em>terminal</em> call.</p><ul><li><p><code>from(..)</code> - declares the source for the provided response data elements. It is declared in a few different ways, for alternative use:</p><ul><li><code>from(T... elements)</code> - vararg elements</li><li><code>from(Collection&lt;T&gt; elements)</code> - provided collection at <em>build-time</em></li><li><code>from(Supplier&lt;Iterator&lt;T&gt;&gt; elements)</code> - supplied iterator at <em>build-time</em></li></ul></li><li><p><code>suppliedBy(Supplier&lt;Collection&lt;T&gt;&gt; elements)</code> - declares that response data is supplied at <em>run-time</em>.</p></li></ul><h3 id="responsebuilder-examples" tabindex="-1"><code>ResponseBuilder</code> examples <a class="header-anchor" href="#responsebuilder-examples" aria-label="Permalink to &quot;`ResponseBuilder` examples&quot;">​</a></h3><p>Batch responses provide developers with more options to tune and throttle a system using Query/Response across many services. It may tune and change the profile of resource use, in a network.</p><div class="language-java vp-adaptive-theme"><button title="Copy Code" class="copy"></button><span class="lang">java</span><pre class="shiki shiki-themes github-light github-dark vp-code" tabindex="0"><code><span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">responseBuilder.</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">respondTo</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">&quot;offers/monday&quot;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">, Offer.class)</span></span>\n<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">    .</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">withBatchesOf</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#005CC5;--shiki-dark:#79B8FF;">20</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">)</span></span>\n<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">    .</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">from</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(offers.</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">findAllOffersByDayOfWeek</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(Calendar.MONDAY));</span></span></code></pre></div><p>Dynamic responses are easy to build, with an API that suits modern Java, using lazy calls to suppliers of data.</p><div class="language-java vp-adaptive-theme"><button title="Copy Code" class="copy"></button><span class="lang">java</span><pre class="shiki shiki-themes github-light github-dark vp-code" tabindex="0"><code><span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">responseBuilder.</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">respondTo</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#032F62;--shiki-dark:#9ECBFF;">&quot;users/current&quot;</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">, Token.class)</span></span>\n<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">    .</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">withBatchesOf</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(</span><span style="--shiki-light:#005CC5;--shiki-dark:#79B8FF;">128</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">)</span></span>\n<span class="line"><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">    .</span><span style="--shiki-light:#6F42C1;--shiki-dark:#B392F0;">suppliedBy</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">(userTokenService</span><span style="--shiki-light:#D73A49;--shiki-dark:#F97583;">::</span><span style="--shiki-light:#24292E;--shiki-dark:#E1E4E8;">findAllCurrentUserTokens);</span></span></code></pre></div>',21),P=JSON.parse('{"title":"Developers Reference","description":"","frontmatter":{"outline":"deep"},"headers":[],"relativePath":"reference/developers-reference.md","filePath":"reference/developers-reference.md","lastUpdated":1720631212000}'),T={name:"reference/developers-reference.md"},R=Object.assign(T,{setup(q){return(D,x)=>(l(),n("div",null,[h,o,e("p",null,[s("The goal of "+t(i)+" is to provide developers with tools that are easy to use and understand. We believe that a procedural and imperative style of writing programs, or thinking about tasks in programs, is broadly understood and a very valuable model. With "+t(i)+" we try to support this, rather than introducing any new concepts for ",1),r,s(", "),d,s(" or "),p,s(".")]),e("p",null,[s("With "+t(i)+" developers should feel enabled to write code in a ",1),k,s(" way. However, it is our mission to raise awareness of things that are hard to consider, when building "),c,s(". The tools try to convey these considerations, by making them transparent and part of the API.")]),u,E,e("p",null,[s("The "),m,s(" class is a central point of entry, and provides a fluent builder-API, for publishing queries. It's provided as a bean, by enabling "+t(i)+", using the ",1),g,s(" annotation. It may be injected as a dependency to provide access from methods in any Spring component.")]),y,e("p",null,[s("The second argument is the expected type of any received response elements. It is not published with the query, but rather used to coerce or interpret any received responses. This means that regardless of the payload of any response, in this case "+t(i)+" will attempt to read the response elements as the declared type ",1),b,s(".")]),f,e("p",null,"It is possible to express constraints at the integration point, also when using "+t(i)+", throwing on an unfulfilled query, as an option to more lenient handling with defaults.",1),F,e("p",null,[s("Another entry-point into "+t(i)+" is the ",1),v,s(". It provides a fluid builder-API that allows users to create responding services or components.")]),C,e("div",_,[w,e("p",null,[s("The data mapper mentioned above, is in fact the "),B,s(" and "+t(i)+" currently uses JSON as the transport format. This means that type hints, JSON mapping configuration annotations or custom mappings will apply. However as data mapping on the consumer side is done by coercion, the published format must conform to some agreed upon standard, shape or protocol.",1)])]),A]))}});export{P as __pageData,R as default};
