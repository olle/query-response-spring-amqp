# Query/Response Maturity Model

Just like with the [Richardson Maturity Model](https://martinfowler.com/articles/richardsonMaturityModel.html),
I've identified an evolution of maturity around the acceptance, use and
implementation of Query/Response. It describes the benefits, opportunities and
also complexities, pretty well.

## Level 0 - Purgatory

All communication and exchange is bound to fixed, configured, service end-
points. Synchronous blocking calls exchange information based on formats
declared in project Wiki-pages or Word-documents. Most solutions are stateless,
with I/O bound performance. Changes typically require system wide,
synchronized, upgrades. This lead to development dropping in velocity, as each
module or team will find it hard or impossible to act independently of each
other.

## Level 1

Using the Query/Response pattern for the first time often leads to healthy
temporal decoupling pretty quick. But with a lot of code still written with
a synchronous model in mind, the data exchange tend to look a bit like _sync_.
Solutions move towards being stateful, but loosen their I/O-bound performance
characteristics. It's hard for developer to think about queries and responses
not coming from known components. Already at this level teams and modules gain
a lot in the capability to move independently. Releases and deployment is
practically not a tangle any more, although the view on evolutionary
data-structures or protocols for data, may lag behind and still be
Wiki/Document-based.

## Level 2

At this level a deeper insight into the value of a proper data-structure or
protocol for payload, which can evolve as required, is often gained. With
this comes the extended benefit of seamless upgrades and service evolution.
Developers get to experience how responsibilities can move without breaking
or changing any integration code - response publishers can change owners and
location.

## Level 3

More and more ideas around reactivity and flexibility begin to take form.
Events can immediately trigger queries which may enrich a local context based
on current needs. This moves the design and use beyond a system using sync,
and durable persistent state, to a more ephemeral and _living_ model. The data
structure of payloads tend to be less bound to strict _types_ and more
malleable _data shapes_.

## Level 4 and beyond...

Information exchange using the Query/Response pattern allows for almost
limitless evolution of services, or components, no longer bound to versions or
availability. The structure of any data is also very dynamic, information can
be partial, enriched, or come in different sets, from different publishers. No
schema is required at this level, but _data shapes_ are used, which can be
embraced by all collaborators in the architecture. Queries are sent and
responses consumed, sometimes within deliberate timeouts; take-until semantics.
This way modules can provide, and fulfill, explicit SLAs if required.

::: info
This is of course not supposed to be taken too seriously, but the maturity
levels describes a journey, from a strict and stale model, into one where
evolution and change is quite normal.
:::
