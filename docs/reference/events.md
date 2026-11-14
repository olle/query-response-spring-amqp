# Events & Telemetry

## Telemetry

Participating nodes in a Query/Response network topology will broadcast events with structured telemetry for the UI and monitoring applications to consume. The common data-shape encapsulates a way to communicate measurements, events and node-identifying and describing meta data.

The entry structure and format is encoded as a JSON UTF-8 encoded string payload.

```json
{
  "elements": [
    {
      "key": "<string>",
      "value": "<string|number|boolean>",
      "timestamp": "<OPTIONAL:number>",
      "uuid": "<OPTIONAL:string>"
    }
    // ...
  ]
}
```

The `key` and `value` property pairs are used to encode any measurement. The `timestamp` is an optional property, that can be used to associate a measurement with a point in time since the EPOCH, in milliseconds. In the telemetry published by the library, `timestamp` is only present on the throughput samples.

The `uuid` property is used to identify the publishing node during its lifetime—effectively creating an aggregate for statistics to present. This means that in practice there are operational metrics which may _come and go_ as well as logical statistics which may pertain to the topology or cluster of nodes. A node gets a new `uuid` when it restarts.

### Transport

Telemetry is published on the shared topic exchange, `query-response` by default, using the routing key `query-response/internal/stats`. Each participating node publishes its own message, once per interval.

| Setting          | Default                        | Property                            |
|------------------|--------------------------------|-------------------------------------|
| Exchange         | `query-response`               | `queryresponse.exchange.name`       |
| Routing key      | `query-response/internal/stats`| `queryresponse.stats.topic`         |
| Initial delay    | `7000` ms                      | `queryresponse.stats.initial-delay` |
| Publish interval | `11000` ms                     | `queryresponse.stats.delay`         |

The first message is published `initial-delay` milliseconds after startup, and subsequent messages are then published every `delay` milliseconds. Both values are given in milliseconds. See the [configuration reference](./configuration.md) for the full set of properties.

::: tip Vocabulary
The routing key, the payload keys and the `queryresponse.stats.*` properties all use the word _stats_. That is historical naming—the messages carry _telemetry_. The property names and the `internal/stats` topic are kept as they are, for compatibility.
:::

### Table of published telemetry

| Key                       | Value   | Timestamp | UUID   | Description                                                                 |
|---------------------------|---------|-----------|--------|-----------------------------------------------------------------------------|
| `name`                    | string  | -         | ✓      | Name or identity of the node, or `application`                              |
| `pid`                     | string  | -         | ✓      | Node process id, or `-`                                                     |
| `host`                    | string  | -         | ✓      | Node hostname, or `unknown`                                                 |
| `uptime`                  | string  | -         | ✓      | Node uptime, or `-`                                                         |
| `only_responses`          | boolean | -         | ✓      | Whether node has only responded `true`, or published queries `false`         |
| `count_queries`           | number  | -         | ✓      | Number of published queries                                                 |
| `count_consumed_responses`| number  | -         | ✓      | Number of consumed responses                                                |
| `count_published_responses`| number | -         | ✓      | Number of published responses                                               |
| `count_fallbacks`         | number  | -         | ✓      | Number of _fallbacks_ (no responses)                                        |
| `max_latency`             | number  | -         | ✓      | Largest recorded time between query and response, in milliseconds           |
| `min_latency`             | number  | -         | ✓      | Smallest recorded time between query and response, in milliseconds          |
| `avg_latency`             | number  | -         | ✓      | Average time between query and response, in milliseconds                    |
| `throughput_queries`      | number  | ✓         | ✓      | Number of queries published, since last notification                        |
| `throughput_responses`    | number  | ✓         | ✓      | Number of responses consumed, since last notification                       |
| `avg_throughput`          | number  | ✓         | ✓      | Average throughput, derived by the UI from the throughput samples—**not** published by the library |

The `min_latency` and `max_latency` entries are omitted until at least one latency measurement has been recorded.

The two throughput samples are raw counts since the previous message, that is per publish interval—not per second. Any rate, such as events per second, is derived by the consumer.

### Consuming telemetry

The Query/Response UI binds a single anonymous queue to both `query-response/internal/stats` and `query-response/internal/telemetry`, and consumes the messages without acknowledgements. Any other monitoring application can bind its own queue to the stats routing key to receive the same telemetry.

NOTE: `query-response/internal/telemetry` is reserved for future use. The UI binds it, but the library does not publish any messages to it yet.

## Current status

::: warning Work in progress
Telemetry publishing is in place, but the UI consumption side is still under active development. Today the working surfaces are the REST endpoints—`GET /api/v1?q=…` for publishing a query and `GET /api/v0/nodes` for node snapshots. Broadcasting metrics and node updates over the WebSocket at `/ws`, as well as publishing queries over it, is in progress. Treat those parts as intent rather than a stable contract.
:::
