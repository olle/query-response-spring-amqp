---
outline: deep
---

# What is Query/Response?

Query/Response is an asynchronous non-blocking messaging pattern for building
highly decoupled evolving service architectures.

## A simple example

Let's learn about the Query/Response pattern by walking through a small
fictional example (no pun intended). The technical context is _messaging_ and
hints at some type of broker-based setup - in theory though, any asynchronous
communication could be used. The examples are only pseudo-code and plain-text
data, to keep things simple.

### Any good sci-fi books out there?

Let's publish a query.

```
query: books.sci-fi
reply-to: library/books.sci-fi#42
```

The structure above captures all the basic components that a query should
communicate. The term `books.sci-fi` expresses the published _need_, and we
can easily understand that it's a _request_ for science fiction books.

_The dot-notation is not at all required, the query can use any syntax that
fits the platform or programming language._

The query has an address where responses should be sent back to:
`library/books.sci-fi#42`. This is really important, not only in order to
receive responses, but also to avoid coupling the sender to the query. We
don't need to state who's publishing the query. The `reply-to` is just an
address, a location or _mailbox_ that can be used for replies.

The address is only for this particular query, and it is made to be unique.
In this example `library/books.sci-fi#42` describes a topic `library`, and
then the unique mailbox or queue for the query with a hash-code
`books.sci-fi#42`.

### The current top-3 books

```
response: library/books.sci-fi#42
body:
    "Neuromancer"
    "Snow Crash"
    "I, Robot"
```

We're in luck. We got a response! The information above represents a response
to the query we published. It's sent to the address from the query, and carries
a body or payload of information which may be of interest to us.

The response does not have to say who it's from. This allows us to think about
exchange of information, without the notion of: _"A sends a request to B,
which responds to A"_. We are making sure that the services are decoupled from
each other, by letting the response be an _optional_ message, sent to the
_address_ instead of a reply to the _sender_. More about this later.

### The Asimov collection

Since our query was published as a notification, we're not bound to a single
reply. We can keep on consuming any number of responses that are sent to the
address we published.

```
response: library/books.sci-fi#42
body:
    "I, Robot"
    "The Gods Themselves"
    "Pebble in the Sky"
```

In this response we received a list of book titles which all have the same
author. The previous was a list with popular books. This reply even has one
entry which was already in the first response we received.

This is of course not a problem, and it shows us a couple of important things.
Responses may come from different sources and contexts. This means that the
consumer of a response will have to assert the value or _usefulness_ of the
received information, and decide how to handle it.

::: warning Important
The structure of a response should of course conform to some common, agreed
upon, format or data-shape. More on this later.
:::

Considering all this, we need to remember [Postel's Law](https://en.wikipedia.org/wiki/Robustness_principle). Information should be liberally handled (interpreted), but publishing should be 
done more conservatively. As a consumer of responses we just can't have a
guarantee that the information received is valid, well formed or not malicious.
We have to consume, convert and validate with great care. The decoupling in
the Query/Response patter has a price, and this is one part of it.

::: danger Consideration
But is a published REST-endpoint, for POST requests, that much better? I
would argue that we still have the same requirements. To be able to handle
requests liberally, we have to convert and validate, with great care. But
we are coupling the client and server to each other and, what is perhaps
even worse, we're actually allowing the client to control the writing of
information inside the server. We have at least surrendered to that model
of thinking. The POST is a write operation!
:::

_To really think and reason about who's controlling the write operation, can
be a very powerful concept in my view. And arguably, the further away we
can push this authority from the actual, internal act of writing, the less
we need to think about the complexity of both collaborators at once. This is
of course the essence of messaging. We could still achieve this with the REST
endpoint, but I would say that it is a lot harder to avoid thinking about
the effect of the returned response from the POST request. Even if it is
empty. We are caught in a lock-step or imperative model._

### No book lovers out there?

Let's rewind the scenario a bit. Let's say we've just published the query,
but no responses arrive. What should we do?

This is not a flaw in the design, but a specific part of the Query/Response
pattern. It is always up to the consumer of responses (the one that sent
the query), to decide _how long_ it will continue to read, or wait for any to
arrive at all. The pattern does not force this or make any promises.

There might be responses. There may be none, a single one or a huge amount.
This is by design, and it forces us to think about important questions, early
in development. Fallback values, proper defaults, circuit-breakers and how
to deal with a flood of responses.

::: warning Important
The most commonly asked question, by developers new to the Query/Response
pattern, is: "But what if there are no responses, what do I show the user?".
Exactly! Plan for that. This is something that should be considered early
in design and development. There might very well be a response, eventually,
but how long do you let the user wait for a result?
:::

### Reprise, surprise

Back to our original scenario. We've received both the top-3, as well as
a collection of Asimov books. And we're still open for more responses to the
published address.

```
response: library/books.sci-fi#42
body:
    "Neuromancer"
    "Snow Crash"
    "I, Robot"
```

Hey, what's this! We now received the same response and body payload, as
before. This is still not a problem, and it's not a flaw in the pattern. It
is not possible to avoid multiple responses, even from the same publisher. As
a consumer, we have to be ready to handle it. There is nothing wrong with this
response at all.

_The consumer must handle this, and can't keep the entries in a simple list. If
we did, it would contain several duplicate entries. It would be enough to use
a set instead, so any duplicate entries would only be kept once._

### So, what's in the library?

Let's see what we have.

```
query: library.sci-fi
reply-to: bookshelf/library.sci-fi#1337
```

A new query is published and we understand the `query` term to mean that
there's an _interest_ in knowing what books are in the library. A successful
scenario could arrive at the following response being consumed.

```
response: bookshelf/library.sci-fi#1337
body:
    "Neuromancer"
    "Snow Crash"
    "I, Robot"
    "The Gods Themselves"
    "Pebble in the Sky"
```

Just as expected.

### Inversion of flow

What we've seen in this example scenario is actually an inversion of what
could have been implemented as a tightly coupled, chained set of synchronous
service calls:

> A user whishes to view a list of science fiction books through the
> `Bookshelf` service, which needs to call the `Library` for the list. The
> `Library` service aggregates all sci-fi books by calls to 2 configured
> services: `Top-3` and `Authors`. Only after both service calls return, can
> the `Library` respond to the `Bookshelf` and the user is presented with
> a list of sci-fi books.

In this type of system, not only are the calls aggregated in the total time,
effectively forcing the user to wait until all calls return, but also to the
availability of each service. This accumulates at the point of the user,
making it highly probable that viewing the list of books will fail.

_There are many ways to work towards better and more resilient solutions, also
in the synchronous solution. I'm not trying to say that it is the wrong
model. The point I'm trying to make, is the very different way of thinking
that the Query/Response pattern forces us into from the start. Availability,
fallbacks, resilience and strict timeouts are called out as key-concepts._

_I hope this illustrates what's possible using this approach and that I've
sparked at least som interest in the Query/Response pattern. Later I will
extend on some of the features and caveats._
