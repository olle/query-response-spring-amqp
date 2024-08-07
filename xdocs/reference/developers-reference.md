---
outline: deep
---

<script setup>
import * as attr from "../attributes.js";
</script>

# Developers Reference

## Overview

The goal of {{attr.BRAND}} is to provide developers with tools that are easy to use
and understand. We believe that a procedural and imperative style of writing
programs, or thinking about tasks in programs, is broadly understood and a
very valuable model. With {{attr.BRAND}} we try to support this, rather than introducing
any new concepts for _streams_, _futures_ or _promises_.

With {{attr.BRAND}} developers should feel enabled to write code in a _normal_ way.
However, it is our mission to raise awareness of things that are hard to
consider, when building [distributed systems](https://en.wikipedia.org/wiki/Distributed_computing).
The tools try to convey these considerations, by making them transparent and
part of the API.

In the next couple of sections we'll look closer at the `QueryBuilder` and the
`ResponseBuilder` types. We'll discuss how to use them in detail, and try to
explain the concepts behind them, and the intention of their implementation.

## `QueryBuilder`

The `QueryBuilder` class is a central point of entry, and provides a fluent
builder-API, for publishing queries. It's provided as a bean, by enabling
{{attr.BRAND}}, using the `@EnableQueryResponse` annotation. It may be injected
as a dependency to provide access from methods in any Spring component.

We recommend injecting it via the component constructor, and keeping it as a
private field. The `findAuthors()` method below, shows how to access the
`queryBuilder` field in order to publish a query.

<<<@../../examples/querying/src/main/java/examples/Authors.java#class{java}

In the example above, the published query is defined by the string **term**
`"authors"`. This is how the most basic contract of Query/Response is defined.
Any string or text term may be published as a query.

The second argument is the expected type of any received response elements. It
is not published with the query, but rather used to coerce or interpret any
received responses. This means that regardless of the payload of any response,
in this case {{attr.BRAND}} will attempt to read the response elements as the
declared type `String.class`.

Queries are built and published using the `queryFor(..)` _initial_ method.
Any following call to one of the _terminal_ methods `orEmpty()`,
`orDefaults(..)` and `orThrows(..)` will build and execute the query, and block
on the calling thread.

Since the call above to `orEmpty()` blocks the thread, users have to specify
one or more query _conditionals_. In the example above, the call to
`waitingFor(..)` defines that the call will block for around 800 milliseconds.

Constructing queries with the `QueryBuilder` revolves around creating a
composition of _initial_, _conditional_, an optional _informal_ and
exactly one _terminal_ method call. In the table below is a short review of the
different builder methods and their types.

### `QueryBuilder` fluid API method types

| Method              | Type          | Description                                                     |
| ------------------- | ------------- | --------------------------------------------------------------- |
| `queryFor(..)`      | _initial_     | Creates a new builder for a query                               |
| `waitingFor(..)`    | _conditional_ | Specifies the waiting/blocking condition                        |
| `takingAtMost(..)`  | _conditional_ | Sets a limit condition, a maximum                               |
| `takingAtLeast(..)` | _conditional_ | Sets a limit condition, a minimum                               |
| `orEmpty()`         | _terminal_    | Terminates with empty, after conditionals are evaluated         |
| `orDefaults(..)`    | _terminal_    | Terminates with some defaults, after conditionals are evaluated |
| `orThrow(..)`       | _terminal_    | Terminates by throwing, after conditionals are evaluated        |
| `onError(..)`       | _informal_    | Allows for explicit logging etc.                                |

Let's take a closer look at each of the builder method types.

### _Initial_ methods

At the moment there's only one _initial_ method and it's declared as:

```java
public <T> ChainingQueryBuilder<T> queryFor(String term, Class<T> type)
```

So we can query for any `String` **term** and given the expected mapped or
coerced **type** as a `Class<T>`. The returned `ChainingQueryBuilder<T>`
provides the capabilities of the fluid API.

### _Conditional_ methods

All _conditional_ properties can be composed together by the `QueryResponse`
builder API, to define whether a query is successful or not. If an executing
query is completed in a _successful_ way, fulfilling the _conditionals_, it will
return and not consume any more responses.

- `waitingFor(..)` - defines a timeout _conditional_. The built query will
  evaluate as _successful_ if _any_ responses were consumed after the
  (approximate) given time limit has elapsed. There are a few different methods
  declared, to specify the timeout:

  - `waitingFor(long millis)`
  - `waitingFor(long amount, TemporalUnit timeUnit)`
  - `waitingFor(Duration duration)`

- `takingAtMost(int atMost)` - defines a limiting _conditional_ on the
  aggregated number of received elements. The built query evaluates to
  _successful_, and returns, when the given amount is reached.

- `takingAtLeast(int atLeast)` - defines a minimum _conditional_ on the number
  of received element. The built query evaluates to _successful_, only if at
  least the given number of elements can be consumed.

### _Terminal_ methods

Only one _terminal_ method can be invoked on the builder, per query. It
will ensure that the query is built and executed. All _terminal_ methods are
declared to return `Collection<T>` where the type parameter `<T>` is given
in the _initial_ method `type` parameter.

- `orEmpty()` - defines the query to return an empty `Collection` in case the
  _conditionals_ do not evaluate to _successful_.

- `orDefaults(..)` - defines the query to return with some provided _defaults_
  in case the _conditionals_ do not evaluate to _successful_. There are a couple
  different methods declared for defaults:

  - `orDefaults(Collection<T> defaults)` - set at _call-time_.
  - `orDefaults(Supplier<Collection<T>> defaults)` - supplied lazily at _run-time_.

- `orThrow(..)` - defines the query to throw an exception in case the
  _conditionals_ do not evaluate to _successful_.

::: warning Careful
Note the difference in _call-time_ and _response-time_ - since the call to the
_terminal_ method is a blocking call, any fetched results as defaults are
prepared as the query is built. In order to dynamically provide better defaults
at _run-time_, use the lazy supplier instead.
:::

### _Informal_ methods

Currently there's only one _informal_ builder method, allowing for extended
logging or information capture, in case the query fails or an exception is
thrown.

```java
public ChainingQueryBuilder<T> onError(Consumer<Throwable> handler)
```

::: tip
Try to think more about how the `QueryBuilder` API covers the exceptional
query-cases, as part of the composition of _conditionals_. If clients try
to use _terminals_ that provide sensible defaults, it may not be necessary
to build other types of complex recovery or retries.
:::

### `QueryBuilder` examples

Below are some examples of how the different `QueryBuilder` API methods can be
combined.

Using `takingAtMost(..)`, combined with `waitingFor(..)`, preserves system
resources and the client can be protected from consuming too much data.

```java
return queryBuilder.queryFor("authors", String.class)
                .takingAtMost(10)
                .waitingFor(800)
                .orDefaults(Authors.defaults());
```

It is possible to express constraints at the integration point, also when using
{{attr.BRAND}}, throwing on an unfulfilled query, as an option to more lenient
handling with defaults.

```java
return queryBuilder.queryFor("offers/rental", Offer.class)
                .takingAtLeast(10)
                .takingAtMost(20)
                .waitingFor(2, ChronoUnit.SECONDS)
                .orThrow(TooFewOffersConstraintException::new);
```

The _informal_ builder feature, allows for transparency into queries that may
have to be observed.

```java
return queryBuilder.queryFor("offers/rental", NewOffer.class)
                .takingAtLeast(3)
                .waitingFor(400)
                .onError(error -> LOG.error("Failure!", error))
                .orThrow(TooFewOffersConstraintException::new);
```

## `ResponseBuilder`

Another entry-point into {{attr.BRAND}} is the `ResponseBuilder`. It provides a
fluid builder-API that allows users to create responding services or components.

It is also provided as a bean, when using the `@EnableQueryResponse` annotation
in a Spring application. It can easily be injected as a dependency to provide
access from methods in Spring components.

The `respondWithAuthors()` method below, shows how the injected builder is used
to create a responding service. It is invoked by the Spring application context,
on the `ApplicationReadyEvent` event.

<<<@../../examples/responding/src/main/java/examples/OnlyThreeAuthors.java#class{java}

In the example above the responding service is defined by calling the builder
method `respondTo(..)` with the query **term** parameter `"authors"`. It will
be bound to publish the given 3 authors as `String.class` entries, whenever it
consumes a query for the matching string **term** `"authors"`.

This is the most basic premiss of Query/Response, that any string or text term
may be interpreted as a query - it is however up to the response publisher to
determine what the query means.

::: tip
We've tried to provide information around the Query/Response _protocol_ and
philosophy in the later chapter on [The Query/Response Protocol](./the-query-response-protocol.md).
Go there to find out more.
:::

The second parameter is the the type of each element, that will be published in
the response. It is given both as a type hint for the compiler, as well as a
parameter to the data mapper. Here it's trivial, the three authors are given as
`String.class` entries.

::: info Note
The data mapper mentioned above, is in fact the
`com.fasterxml.jackson.databind.ObjectMapper` and {{attr.BRAND}} currently uses
JSON as the transport format. This means that type hints, JSON mapping
configuration annotations or custom mappings will apply. However as data
mapping on the consumer side is done by coercion, the published format
must conform to some agreed upon standard, shape or protocol.
:::

Response publishers are built using the `respondTo(..)` _initial_ method. Any
following call to one of the _terminal_ methods `from(..)` or `suppliedBy(..)`
will create and register it, as its own consumer in another thread. The
builder call returns immediately.

The `ResponseBuilder` comes with some methods to allow for _partitioning_ or
_batching_, which can be used to control the transfer of data to some degree.

The table below shows a summary of the builder methods and types.

### `ResponseBuilder` fluid API method types

| Method              | Type       | Description                                 |
| ------------------- | ---------- | ------------------------------------------- |
| `respondTo(..)`     | _initial_  | Creates a new builder for a query           |
| `withAll()`         | _batching_ | Specifies NO batches                        |
| `withBatchesOf(..)` | _batching_ | Sets the batch size of responses            |
| `from(..)`          | _terminal_ | Terminates with some given response data    |
| `suppliedBy(..)`    | _terminal_ | Terminates with some supplied response data |

Let's take a closer look at each of the builder method types.

### _Initial_ methods

At the moment there's only one _initial_ method for building responses. It is
declared as:

```java
public <T> ChainingResponseBuilder<T> respondTo(String term, Class<T> type)
```

So we can create a response for any `String` **term** and declare that we intend
to publish elements of some **type** given as a `Class<T>`. The returned
`ChainingResponseBuilder<T>` provides the capabilities of the fluid API.

### _Batching_ methods

Control over how response elements are published can be made by using the
_batching_ methods that the builder provides.

- `withAll()` - defines that **no** batching should be used, and will publish
  all given elements, or try to drain a supplied `Iterator` all at once.

- `withBatchesOf(int size)` - defines a batch size, which the response publisher
  will use, to create a series of response messages, with up-to the given `size`
  of elements.

### _Terminal_ methods

Only one _terminal_ method can be called on the builder, per response. It will
ensure that a responder is created and added as a query-consumer, a subscriber
to the query **term** as a topic. It is not attached to the calling thread, so
the builder call always returns after the _terminal_ call.

- `from(..)` - declares the source for the provided response data elements. It
  is declared in a few different ways, for alternative use:

  - `from(T... elements)` - vararg elements
  - `from(Collection<T> elements)` - provided collection at _build-time_
  - `from(Supplier<Iterator<T>> elements)` - supplied iterator at _build-time_

- `suppliedBy(Supplier<Collection<T>> elements)` - declares that response data
  is supplied at _run-time_.

### `ResponseBuilder` examples

Batch responses provide developers with more options to tune and throttle a
system using Query/Response across many services. It may tune and change the profile
of resource use, in a network.

```java
responseBuilder.respondTo("offers/monday", Offer.class)
    .withBatchesOf(20)
    .from(offers.findAllOffersByDayOfWeek(Calendar.MONDAY));
```

Dynamic responses are easy to build, with an API that suits modern Java, using
lazy calls to suppliers of data.

```java
responseBuilder.respondTo("users/current", Token.class)
    .withBatchesOf(128)
    .suppliedBy(userTokenService::findAllCurrentUserTokens);
```
