---
outline: deep
---

# The example revisited

Let's examine one of the most powerful aspects of using the Query/Response
pattern. If we think back to our [initial example](./what-is-query-response.md)
we published a query for books in the sci-fi genre.

```
query: books.sci-fi
reply-to: library/books.sci-fi#42
```

We also learned that responses may come from different sources, with different
payloads and we are responsible for dealing with validation and duplicates etc.

The query in this example uses only some minimal semantics to express the
genre of books requested, the term `sci-fi`. This is part of a contract from
our domain, together with rules on how any result payload should be presented.
The list of strings within quotes are not by accident, it is also by design.

The Query/Response pattern does not enforce any structural rules for query,
address or response syntax. This must come from designers and developers. _I
would suggest, using [Domain Driven Design](https://en.wikipedia.org/wiki/Domain-driven_design)
to leverage the power of a ubiquitous language in the queries_.

All this together puts us in a position to allow change and evolution in our
system.

## A better library protocol

We have agreed on supporting _stars_ for book ratings, and different teams
scramble to their stations to extend for the new feature.

We saw earlier that data returned was formed as a list of quoted strings, and
the contract for parsing was: "first quoted string per line is book title".

```
body:
    "Neuromancer"
```

That rule and the capability to extend it, made it possible to agree on a new
optional format: "trailing key-values are properties". For example:

```
body:
    "Neuromancer" isbn:9780307969958 stars:4
```

This is great. Let's get to work.

## Top-3 books have stars

```
query: books.sci-fi
reply-to: library/books.sci-fi#77
```

At a later time a new query for science fiction books is published. Now, we
still must not assume anything about the service or collaborator publishing
the query. It may be that we have a new service running in our system, not yet
live, or an updated version of the first one - we don't need to know.

```
response: library/books.sci-fi#77
body:
    "Neuromancer" stars:3
    "Snow Crash" stars:5
    "I, Robot" stars:4
```

The first response looks great, it's using the new extended protocol and
provides star-ratings with the top-3 sci-fi book list.

## One of each flavour

Another response is consumed:

```
response: library/books.sci-fi#77
body:
    "I, Robot"
    "The Gods Themselves"
    "Pebble in the Sky"
```

Oh, ok seems that we've received a response with only Asimov books again, and
sadly no stars. Luckily the protocol rules allows us to still use the response
if we choose to.

```
response: library/books.sci-fi#77
body:
    "I, Robot" stars:2
    "The Gods Themselves"
    "Pebble in the Sky" stars:5
```

And what is this now. We've consumed yet another response and it appears to be
the Asimov list again, but this time with star-ratings, but only for a few
titles.

This is quite normal and shows us a really important and valuable aspect of
the Query/Response pattern. If we would pull the curtain back a bit, it could
be reasonable to assume that the publisher of Asimov books now exists in 2
distinct versions. One supports the new updated format, and has a couple of
star-ratings set. The other appears to be the _older_ version.

We have effectively seen how response publishers can evolve, and even exist
side-by-side, if care is taken to design a suitable payload protocol.

_The backward compatibility of the payload format is not at all required in the
Query/Response pattern. Implementations could use version tags or classifiers
to check for compatibility at the consumer side._

::: warning Important!
The key point here is, the consumer is still responsible for asserting the
usefulness and value of the response information. Parsing, validating or
checking for version compatibility is required.
:::

## Out with the old

Let's jump forward and say that at some later time, the query for sci-fi books
is published again.

```
query: books.sci-fi
reply-to: library/books.sci-fi#88
```

And this time, the only consumed response with Asimov books is the following:

```
response: library/books.sci-fi#88
body:
    "I, Robot" stars:3
    "The Gods Themselves" stars:3
    "Pebble in the Sky" stars:5
```

We can almost certainly conclude that the original version of the Asimov
book service has been shut down.

Again we can see how the Query/Response pattern helps in coping with a natural
evolution of the system. Services can be added, removed or upgraded at any
time.
