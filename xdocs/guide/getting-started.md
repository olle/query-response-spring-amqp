<script setup>
import * as attr from "../attributes.js";
</script>

# Getting Started

{{attr.BRAND}} makes it really easy to extend Spring Boot stand-alone,
production-grade applications, that are using Spring AMQP. We have taken
a working pattern for building highly decoupled evolving service architectures,
and wrapped it in a developer friendly library.

## System Requirements

{{attr.BRAND}} requires at least **Spring Boot 2.x** and **Java 11**, and should work
for later releases too. We are building and running it successfully with
**Java 11** and the **Spring Boot 3.0.3** version.

## Installation &amp; Configuration

It is distributed as a [Maven](https://maven.apache.org) dependency, and is
known to work well with Maven 3.3+. Using the dependency with [Gradle](https://gradle.org)
should work too. Please see the [Quickstart](https://github.com/olle/query-response-spring-amqp#quickstart)
information, available on the project [Github page](https://github.com/olle/query-response-spring-amqp),
for information on how to get the Maven dependency.

Enabling {{attr.BRAND}} is done by loading the `QueryResponseConfiguration`
class. The most simple way to do this, is by annotating your Spring Boot
application with the `@EnableQueryResponse` annotation.

<<<@../../examples/myapp/src/main/java/app/MyApp.java#install{java}

NOTE: This annotation will do nothing more but to import the
`QueryResponseConfiguration` class.

That's it! There is no more infrastructure code, wiring or setup that needs to
be done. **It's just that easy.**

### Connecting to an AMQP broker

Before you can run your application you need to make sure there is an AMQP
broker available. By default {{attr.BRAND}} tries to connect to a
https://www.rabbitmq.com[RabbitMQ], running locally on port `5672`.

Start an and run RabbitMQ using `docker`:

```sh
$ docker run -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

NOTE: The `3-management` tag will enable the RabbitMQ Management UI. When the
broker is running, it can be accessed at http://localhost:15672 with
username and password `guest/guest`.

Now running your application, will enable {{attr.BRAND}}, connect to the broker and
create all the resources necessary on the broker.

```sh
$ mvn spring-boot:run
```

Now is a good time to use the RabbitMQ Management UI, available at
http://localhost:15672, to inspect the exchange, queues and bindings created
by {{attr.BRAND}} by default.

## Queries

Publishing **queries** is a way for your application to ask for information that
it may need in order to accomplish tasks. Queries express a _need_, and are not
addressed to any specific service or component.

{{attr.BRAND}} makes it really really easy, to create and publish a query using
the `QueryBuilder`.

<<<@../../examples/myapp/src/main/java/app/Queries.java#query{java}

::: info Note
We are using the `@Order` annotation in our example only to ensure that
responses are built and registered before queries, when they are built
in one and the same app.
:::

::: tip <1>
Initiates a query for the term `marco`, with any results being consumed as,
or _mapped_ to, the type `String.class`. Returned results are always
gathered in a collection. Either **none, one or many** elements may be
returned.
:::

::: tip <2>
Queries require a timeout, here we set it to `1000L` milliseconds. This
means that this specific query will **always** block for 1 second.
:::

::: tip <3>
The query may not receive any responses, so it _always_ needs to specify
how that case should be handled. Default here is an empty collection, of
the declared return type `String.class`.
:::

_Hopefully this shows, how concise and powerful the `QueryBuilder` is, dealing
with results mapping, fault tolerance and default values in just a couple of
lines of code._

If you run the application now, it will publish a **query** to the message
broker, which we can see in the logs.

```sh
$ mvn spring-boot:run
...
c.s.queryresponse.RabbitFacade : |<-- Published query: marco - (Body:'{}' MessageProperties [headers={x-qr-published=1589642002076}, replyTo=94f0fff4-c4f3-4491-831d-00809edb6f95, contentType=application/json, contentLength=2, deliveryMode=NON_PERSISTENT, priority=0, deliveryTag=0])
```

At the moment there are no responses to be consumed, so after blocking for 1
second, nothing is printed `STDOUT`.

## Responses

Building services, medium, large or _micro_ (who cares), that publish
**responses** to queries is also really easy with {{attr.BRAND}}, using the
`ResponseBuilder`.

<<<@../../examples/myapp/src/main/java/app/Responses.java#response{java}

::: tip <1>
Initializes a response to queries for `marco`, providing the type-hint on
how to map entries in the response. Set to `String.class` here.
:::

::: tip <2>
The response `withAll()` will publish all elements in one single response.
:::

::: tip <3>
And finally this response is provided the elements `"polo", "yolo"` as the
actual data to publish. _The builder varags method, used here, is mostly
for trying out Query/Response, or for static responses._
:::

_Again, the builder makes it really easy to create a responding service, without
any special setup or complicated configurations._

Now if you run the application again, with the response component registered
before the query publisher, it will publish the response.

```sh
$ mvn spring-boot:run
...
c.s.queryresponse.RabbitFacade : |<-- Published query: marco - (Body:'{}' MessageProperties [headers={x-qr-published=1589642489894}, replyTo=c77a8a1d-c959-4f2a-bd51-85b7e6b5b69b, contentType=application/json, contentLength=2, deliveryMode=NON_PERSISTENT, priority=0, deliveryTag=0])
c.s.queryresponse.Response : |--> Consumed query: marco
c.s.queryresponse.RabbitFacade : |<-- Published response: c77a8a1d-c959-4f2a-bd51-85b7e6b5b69b - (Body:'{"elements":["polo","yolo"]}' MessageProperties [headers={x-qr-published=1589642489941}, contentType=application/json, contentEncoding=UTF-8, contentLength=28, deliveryMode=NON_PERSISTENT, priority=0, deliveryTag=0])
c.s.queryresponse.Query : |--> Received response message: MessageProperties [headers={x-qr-published=1589642489941}, contentType=application/json, contentEncoding=UTF-8, contentLength=0, receivedDeliveryMode=NON_PERSISTENT, priority=0, redelivered=false, receivedExchange=, receivedRoutingKey=c77a8a1d-c959-4f2a-bd51-85b7e6b5b69b, deliveryTag=1, consumerTag=amq.ctag-Q_ghWp4TWU9EYhi_rqErcg, consumerQueue=c77a8a1d-c959-4f2a-bd51-85b7e6b5b69b]
marco? polo
marco? yolo
```

Now you can see a full roundtrip of the **query** being published and consumed,
and the **response** being published and also consumed. And the finished output
is "polo" and "yolo" printed on `STDOUT`.
