Query/Response
==============

**WIP: Driving the initial version from this README, please feel free to
       provide feedback through issues.**

Ensure decoupling of services and systems by taking an _always async_ approach
to integration. By querying a common AMQP broker, instead of depending on
a plethora of REST-ful microservices, developers can design with failures,
timeouts, restarts or floodings in mind from the start, and not as a poorly
tacked-on afterthought. This creates more resilient, available and scalable
solutions.

Queries
-------

The fluid Query/Response API, makes it easy for developers and understand and
decide on a strategy for service integration. Timeouts are _first-class
citizens_ in the API, and protect against surprises.

```java
  var authors = Query.queryFor("authors")
                  .waitingFor(800)
                  .orEmpty()
                  .collect(Collectors.toList());
```

Defaults are suddenly top-of-mind for developers, and either the _empty case_
is good enough, or fallbacks can be provided.

```java
  var authors = Query.queryFor("authors")
                  .waitingFor(800)
                  .orDefaults(Authors.defaults())
                  .collect(Collectors.toList());
```

Preserve resources, specific to the current needs and protect your services,
by limiting the amount of data consumed.

```java
  var authors = Query.queryFor("authors")
                  .takingAtMost(10)
                  .waitingFor(800)
                  .orDefaults(Authors.defaults())
                  .collect(Collectors.toList());
```

Express constraints and react accordingly, as an option to lenient handling.

```java
  var offers = Query.queryFor("offers/rental")
                 .takingAtLeast(10)
                 .takingAtMost(20)
                 .waitingFor(2, ChronoUnit.SECONDS)
                 .orThrow(TooFewOffersConstraintException::new)
                 .collect(Collectors.toList());
```

Responses
---------

To create responding services we provide a really effective and expressive
fluid-API. Developers benefit from the declarative style, and won't have to
write any boilerplate code. It is easy to understand, work with and explore.

Very simple scenarios can quickly be created, for tests or proof of concept
work.

```java
  Response.respondTo("authors")
       .withAll()
       .from("William Gibson", "Isaac Asimov", "J.R.R. Tolkien");
```

Batch responses provide developers with great options for tuning a system with
Query/Response as a whole.

```java
  Response.respondTo("offers/monday")
       .withBatchesOf(20)
       .from(Offers.findAllOffersByDayOfWeek(Calendar.MONDAY));
```
