# Configuration

Query/Response is configured through Spring Boot configuration properties, bound
under the `queryresponse` prefix. All properties are optional, and the defaults
work well for a single Query/Response network sharing one AMQP broker.

## Properties

| Property                           | Default                         | Description                                                                 |
|------------------------------------|---------------------------------|-----------------------------------------------------------------------------|
| `queryresponse.exchange.name`      | `query-response`                | Name of the shared topic exchange                                           |
| `queryresponse.queue.prefix`       | `query-response-`               | Prefix for generated queue names                                            |
| `queryresponse.stats.topic`        | `query-response/internal/stats` | Routing key used when publishing telemetry                                  |
| `queryresponse.stats.initial-delay`| `7000`                          | Milliseconds before the first telemetry message is published, after startup |
| `queryresponse.stats.delay`        | `11000`                         | Milliseconds between subsequent telemetry messages                          |

NOTE: The prefix is `queryresponse`, without a trailing dot. Normal Spring Boot
relaxed binding applies, so `queryresponse.stats.initial-delay`,
`queryresponse.stats.initialDelay` and
`QUERYRESPONSE_STATS_INITIALDELAY` all refer to the same property.

## Example

Using YAML:

```yaml
queryresponse:
  exchange:
    name: query-response
  queue:
    prefix: query-response-
  stats:
    topic: query-response/internal/stats
    initial-delay: 7000
    delay: 11000
```

Or using properties:

```properties
queryresponse.exchange.name=query-response
queryresponse.queue.prefix=query-response-
queryresponse.stats.topic=query-response/internal/stats
queryresponse.stats.initial-delay=7000
queryresponse.stats.delay=11000
```

Both examples show the default values, and are only needed when you want to
change them.

## Naming and networks

The **exchange name** identifies the Query/Response network. All participating
nodes must use the same exchange name to see each other's queries, responses
and telemetry. Changing the name effectively creates a separate, distinct
network on the same broker.

The **queue prefix** is used when naming the queues that Query/Response creates
for a node: the queue a responder listens on for queries, and the reply queue a
querying node uses for responses. Both are anonymous—non-durable, exclusive and
auto-delete—and named `<prefix><uuid>`. The prefix must not be empty.

## Telemetry

Each node publishes its telemetry on the shared exchange, using the configured
stats topic as the routing key. The initial delay and the publish interval are
both expressed in milliseconds. See [Events & Telemetry](./events.md) for the
payload format and the published keys.

A monitoring application—such as the Query/Response UI—binds its own queue to
the stats topic to receive the telemetry. Producers and consumers must agree on
both the exchange name and the stats topic.

## Caveats

- There is **no `enabled` property**. To opt out of Query/Response, simply do not
  load the `QueryResponseConfiguration`—typically by not using the
  `@EnableQueryResponse` annotation.
- The exchange name, queue prefix and stats topic are validated to be non-empty.
  The stats delays are not validated, so avoid negative values.
- Changing the exchange name or stats topic on only some nodes will split the
  network: those nodes will no longer exchange queries, responses or telemetry
  with the rest.
