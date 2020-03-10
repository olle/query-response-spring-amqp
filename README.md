Query/Response for Spring® AMQP
===============================

![Java CI](https://github.com/olle/spring-query-response-amqp/workflows/Java%20CI/badge.svg)

**WIP: Driving the initial version from this README, please feel free to
       provide feedback through issues.**

Build safer and more resilient microservices. Get the benefits of an _always
async_ approach to data exchange. Ensure decoupling of components. Be better
prepared for system evolution. Create more scalable solutions. Change the way
you think and design, by using Query/Response for Spring AMQP.

Queries
-------

The fluid Query/Response API, makes it easy for developers and understand and
decide on a strategy for service integration. Timeouts are _first-class
citizens_ in the API, and protect against surprises.

```java
  var authors = Queries.queryFor("authors", String.class)
                  .waitingFor(800)
                  .orEmpty();
```

Defaults are suddenly top-of-mind for developers, and either the _empty case_
is good enough, or fallbacks can be provided.

```java
  var authors = Queries.queryFor("authors", String.class)
                  .waitingFor(800)
                  .orDefaults(Authors.defaults());
```

Preserve resources, specific to the current needs and protect your services,
by limiting the amount of data consumed.

```java
  var authors = Queries.queryFor("authors", String.class)
                  .takingAtMost(10)
                  .waitingFor(800)
                  .orDefaults(Authors.defaults());
```

Express constraints and react accordingly, throwing on an unfulfilled query, as
an option to lenient handling with defaults.

```java
  var offers = Queries.queryFor("offers/rental", Offer.class)
                  .takingAtLeast(10)
                  .takingAtMost(20)
                  .waitingFor(2, ChronoUnit.SECONDS)
                  .orThrow(TooFewOffersConstraintException::new);
```

Optional capabilities, to handle exceptions and errors are built-in and very
easy to use.

```java
  var offers = Queries.queryFor("offers/rental", NewOffer.class)
                  .takingAtLeast(3)
                  .waitingFor(400)
                  .onError(error -> LOG.error("Failure!", error))
                  .orThrow(TooFewOffersConstraintException::new);
```

Responses
---------

To create responding services we provide a really effective and expressive
fluid-API. Developers benefit from the declarative style, and won't have to
write any boilerplate code. It is easy to understand, work with and explore.

Very simple scenarios can quickly be created, for tests or proof of concept
work.

```java
  Responses.respondTo("authors")
       .withAll()
       .from("William Gibson", "Isaac Asimov", "J.R.R. Tolkien");
```

Batch responses provide developers with more options to tune and throttle a
system using Query/Response across many services.

```java
  Responses.respondTo("offers/monday")
       .withBatchesOf(20)
       .from(Offers.findAllOffersByDayOfWeek(Calendar.MONDAY));
```

AMQP Resources & Formats
------------------------

The declared [AMQP] broker resources are very limited. Only the `queries`
topic-exchange is created. All active Query/Response services will automatically
declare the required exchange, with the following parameters:

```
  name: queries
  type: topic
  auto-delete: true
```

  [AMQP]: https://www.rabbitmq.com/protocol.html

The Query/Response library defines a small set of properties and data-formats,
which are used in the AMQP messages - a mini-protocol:

### Query messages

Query messages are very simple in structure and form. The common `queries`
exchanged is published to, and the message `routing-key` will carry the specific
`query-term` that is requested. The `reply-to` header property is set to the
queue name of a generated `query-response-queue`, specific to the published
query.

Both query and response messages use the `application/json` content-type. There
is no further content in the body, just an empty JSON object `{}`, as a
placeholder.

```
  exchange: queries
  routing-key: ${query-term}
  reply-to: ${query-response-queue}
  content-type: application/json
  body:
  {}
```

### Response messages

Published responses also use a common format. They are published to the empty
(default) exchange, with the `query-response-queue` from the `reply-to` property
of a consumed query as the `routing-key`. This will cause a direct routing of
responses back to the declared response-queue.

The response body payload JSON structure always contains `count` and `total`
properties. This is meta-information which provide consumers with hints on
possible paged responses. The `elements` collection contain the actual response
data.

```
  exchange: (default)
  routing-key: ${query-response-queue}
  content-type: application/json
  body:
  {
    count: 42,
    total: 1337,
    elements: [...]
  }
```

---

Spring is a trademark of Pivotal Software, Inc. in the U.S. and other countries.
