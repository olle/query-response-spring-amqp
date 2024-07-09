# Query/Response Protocol

I'd like to describe the Query/Response pattern in a more formal but not
too strict way, since it's not in any way some type of _standard_ or
_protocol_. This is a pattern derived from the general idea of expressing a
_need_ or _demand_, as previously told. It is shaped here, into a specific
version, or flavour, in the **Query/Response pattern**. It simply contains
my recommendations and suggestions on rules or principles to follow.

Please, take what you like, leave the rest, and extend as you seem fit.

Use of the keywords: "MUST", "MUST NOT", "REQUIRED", "SHALL", "SHALL NOT",
"SHOULD", "SHOULD NOT", "RECOMMENDED", "MAY", and "OPTIONAL" are intended
to follow the definitions of [RFC 2119](https://www.ietf.org/rfc/rfc2119.txt).

## Intent

The Query/Response pattern aims to describe a model for information sharing
in a distributed system. It does so by using strong decoupling of system
actors and establishing asynchronous message-based high-level data exchange,
as the only means of communication.

The following specifications tries to provide a set of rules and guides,
which can be used as an authoritative source for developer, implementing the
pattern.

## Components and Collaborators

| Name        | Type     | Description |
| ----------- | -------- | ----------- |
| `Query`     | message  | Very small, published notification.
| `Response`  | message  | Carries information as payload.
| `Address`   | location | Reference to "a mailbox"
| `Publisher` | actor    | Initiates _publish_ method calls.
| `Consumer`  | actor    | Accepts _consume_ method calls.

### `Query`

A notification that expresses a specific _need_ or _whish_, which can be
fulfilled by a response, published to a specified return address. The query
MUST state its _need_ or _whish_ in an interpretable way. It may use any
suitable syntax, semantics or language. Most commonly a simple string or term
is used, similar to a message subject, -name or an event _routing-key_. A
query MUST specify an address for responses, which SHOULD be _appropriate_
for the stated query and, technically _available_, as the query is created.

::: tip Recommendation
I very much recommend creating queries with expressions or terms from a
domain specific, or ubiquitous language. This allows for broader understanding
and involvement of stakeholders. Keeping queries human readable makes sense.
It's often desirable to use structured terms, with semantics, such as
filters or parameters. This is quite common and not at all bad.
:::

### `Response`

A notification, published, as a response to a query, optionally carrying an
information- or data-payload. A response MUST NOT be sent without an intent to
_answer_ a specific query (use event notifications for that). The response
MUST be sent to the address of the query it responds to, without manipulating
it. A response SHOULD carry an appropriate information- or data-payload, with
the intent to answer the query it responds to. Note that this is not a strict
requirement. Responses SHOULD be sent within an appropriate time frame of
seeing a query.

::: tip
In most cases it's desirable to publish a response as quick as possible,
after consuming a query.
:::

### `Address`

Describes and designates an addressable _location_ with the capability to
receive and handle responses. Typically a messaging _mailbox_ or a queue. The
address MUST NOT describe a system actor or collaborator, but instead ensure
decoupling between a publisher and a consumer.

::: tip
In messaging or broker based systems, the address is typically a routing key,
topic or a queue-name.
:::

### `Publisher`

An actor that initiates the publishing of a notification, either a query or
a response depending on its current role. The publisher MUST NOT be responsible
for the arrival of any published information. Publishers MUST NOT know any
consumers.

::: info Note
The concrete _interpolated_ roles `Query-Publisher` and
`Response-Publisher`, does not have to be bound to a single or unique actor.
:::

_It is open for the implementation of the Query/Response pattern to solve or
choose how it ensures delivery of messages, e.g. using a broker- or queue-
based messaging system or some other solution for asynchronous communication._

### `Consumer`

An actor that willingly yields to the consumption of notifications, from some
external source, either a response or a query depending on its current role.
Consumers MUST NOT know any publishers.

::: info Note
The concrete _interpolated_ roles `Query-Consumer` and
`Response-Consumer`, does not have to be bound to a single or unique actor.
:::

## Methods and Actions

_Nothing in the Query/Response pattern is synchronous, or based on the notion
of guaranteed delivery (or only-once semantics). The following structured
step-by-step description is only for documentation purposes, and does not,
in any way, define a sequence which can be relied upon._

### Prepare `Address`

Before publishing a query, the query publisher SHOULD ensure that an
appropriate address, specified for the query, can be handled.

::: info Note
Implementations are free to use a best-effort approach. It may be that the
only option is to use short-lived or temporary resources, which may or may
not fail to be allocated. Therefore there's no strict requirement to ensure
that the address can be handled.
:::

### Publish `Query`

The query publisher can, at any time, choose to publish a query. No ACK or
NACK will be provided and the query publisher MUST NOT assume that the query
has been consumed, or that a response will be returned at this time. The
publisher SHOULD consider the case where the query is lost, examine options
to detect and repair this, if possible; _timeouts, retries or fallbacks are
perhaps options to investigate_.

### Consume `Query`

A query consumer, that is willingly listening for queries, may at any time
receive, and choose to handle a query. Consuming queries is an unbound
operation. The consumer SHOULD handle queries with an intent to provide a
response, or ignore the query. A consumer MAY decide to publish none, one or
any number of responses to the query - it is optional. A consumer MAY at any
time choose to stop listening for queries.

::: tip
Please note that the Query/Response pattern does not protect against
query consumers with harmful intent. Implementations should consider issues
like security, encryption and trust as extensions to it.
:::

### Publish `Response`

A response publisher MUST use the provided address of the query it responds to,
when publishing responses. No ACK or NACK will be provided and the publisher
MUST NOT assume that the response has been delivered, arrived properly or
consumed.

### Consume `Response`

A response consumer, listening for responses at a previously created address,
MAY at any time receive one or several responses - or not at all. Consuming
responses is an unbounded operation. Any received response MAY have a payload
or body of information. The consumer SHOULD assert and validate any
transferred information with great care. A consumer MAY at any time choose to
stop listening for responses.
