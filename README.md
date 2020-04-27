Query/Response for Spring® AMQP
===============================

![Java CI](https://github.com/olle/spring-query-response-amqp/workflows/Java%20CI/badge.svg) [![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/olle/query-response-spring-amqp.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/olle/query-response-spring-amqp/context:java) [![Join the chat at https://gitter.im/query-response-spring-amqp/community](https://badges.gitter.im/query-response-spring-amqp/community.svg)](https://gitter.im/query-response-spring-amqp/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

**WIP: Driving the initial version from this README, please feel free to
       provide feedback through issues.**

Build safer and more resilient distributed services. Get the benefits of an
_always async_ approach to data exchange. Ensure decoupling of components. Be
better prepared for system evolution. Create more scalable solutions. Change the
way you think and design, by using Query/Response for Spring AMQP.

Getting Started
---------------

The Query/Response library can easily be added to any Spring Boot® project, as
a single dependency.

```xml
  <dependency>
    <!-- From Jitpack-repo -->
    <groupId>com.github.olle</groupId>
    <artifactId>query-response-spring-amqp</artifactId>
    <version>${SOME-TAG}</version>
  </dependency>
```

_At the current time We recommend using [Jitpack](https://jitpack.io) to
resolve the dependency. Query/Response will however be published to the
Sonatype OSS repository, and Maven Central, in the future._

Queries
-------

The fluid Query/Response API, makes it easy for developers and understand and
decide on a strategy for service integration. Timeouts are _first-class
citizens_ in the API, and protect against surprises.

```java
  var authors = QueryBuilder.queryFor("authors", String.class)
                  .waitingFor(800)
                  .orEmpty();
```

Defaults are suddenly top-of-mind for developers, and either the _empty case_
is good enough, or fallbacks can be provided.

```java
  var authors = QueryBuilder.queryFor("authors", String.class)
                  .waitingFor(800)
                  .orDefaults(Authors.defaults());
```

Preserve resources, specific to the current needs and protect your services,
by limiting the amount of data consumed.

```java
  var authors = QueryBuilder.queryFor("authors", String.class)
                  .takingAtMost(10)
                  .waitingFor(800)
                  .orDefaults(Authors.defaults());
```

Express constraints and react accordingly, throwing on an unfulfilled query, as
an option to lenient handling with defaults.

```java
  var offers = QueryBuilder.queryFor("offers/rental", Offer.class)
                  .takingAtLeast(10)
                  .takingAtMost(20)
                  .waitingFor(2, ChronoUnit.SECONDS)
                  .orThrow(TooFewOffersConstraintException::new);
```

Optional capabilities, to handle exceptions and errors are built-in and very
easy to use.

```java
  var offers = QueryBuilder.queryFor("offers/rental", NewOffer.class)
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
  ResponseBuilder.respondTo("authors", String.class)
      .withAll()
      .from("William Gibson", "Isaac Asimov", "J.R.R. Tolkien");
```

Batch responses provide developers with more options to tune and throttle a
system using Query/Response across many services.

```java
  ResponseBuilder.respondTo("offers/monday", Offer.class)
      .withBatchesOf(20)
      .from(offers.findAllOffersByDayOfWeek(Calendar.MONDAY));
```

Dynamic responses are easy to build, with an API that suits modern Java, using
lazy calls to suppliers of data.

```java
  ResponseBuilder.respondTo("users/current", Token.class)
      .withBatchesOf(128)
      .suppliedBy(userTokenService::findAllCurrentUserTokens);
```

AMQP Resources & Formats
------------------------

The declared [AMQP] broker resources are very limited. Only the `query-response`
topic-exchange is created. All active Query/Response services will automatically
declare the required exchange, with the following parameters:

```
  name: query-response
  type: topic
  auto-delete: true
```

  [AMQP]: https://www.rabbitmq.com/protocol.html

The Query/Response library defines a small set of properties and data-formats,
which are used in the AMQP messages - a mini-protocol:

### Query messages

Query messages are very simple in structure and form. The common
`query-response` exchanged is published to, and the message `routing-key` will
carry the specific `query-term` that is requested. The `reply-to` header
property is set to the queue name of a generated `query-response-queue`,
specific to the published query.

Both query and response messages use the `application/json` content-type. There
is no further content in the body, just an empty JSON object `{}`, as a
placeholder.

```
  exchange: query-response
  routing-key: ${query-term}
  reply-to: ${query-response-queue}
  content-type: application/json
  body:
  {}
```

### Response messages

Published responses also use a common format. They are published to the empty
(default) exchange, with the `query-response-queue` from the `reply-to`
property of a consumed query as the `routing-key`. This will cause a direct
routing of responses back to the declared response-queue.

The response body payload JSON structure always wraps the `elements` collection
containing the actual response data in a _envelope object_.

```
  exchange: (default)
  routing-key: ${query-response-queue}
  content-type: application/json
  body:
  {
    elements: [...]
  }
```

The current properties and of Query/Response messages are simple but provide
room for extensions in future versions.

Happy hacking!

---

Spring is a trademark of Pivotal Software, Inc. in the U.S. and other countries.
