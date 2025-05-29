# Query/Response Events

## Metrics or "Stats"

Participating nodes in a Query/Response topology will broadcast events of structured statistics for the UI and monitoring applications to consume. The common data-shape encapsulates a way to communicate measurements, events, and node-identifying and describing meta-data.

The entry structure and format is encoded as a JSON UTF-8 encoded string.

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

The `key` and `value` property pairs are used to encode any measurement. The `timestamp` is an optional property, that can be used to associate a measurement with a point in time since the EPOCH, in milliseconds.

The `uuid` property is used to identify the publishing node during its lifetime—effectively creating an aggregate for statistics to present. This means that in practice there are operational metrics which may _come and go_ as well as logical statistics which may pertain to the topology or cluster of nodes.

### Table of published statistics

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