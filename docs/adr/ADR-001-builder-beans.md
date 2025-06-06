# Builder Beans

## Status

Accepted.

## Context

There's an idiomatic way of providing library functionality throughout Spring.  
Modules such as Spring JDBC, AMQP or Web provide *templates* as beans. Users  
can have these templates injected into their components, and access the  
functionality.

Publishing the Query/Response API through static factory methods may be  
convenient but **it is not the common way.** It also hides the capabilities  
of *mock* or *test dummy* setup in the user code, which makes testing harder.

## Decision

The `QueryBuilder` and `ResponseBuilder` should both be made available through  
the means of bean injection. The static factory methods are removed.

The beans shall be available as mockable types, to better support testing.

## Consequences

A lot of complexity caused by static access to the registry beans will  
disappear.

The convenient and very terse static API is no longer available.

Testing is much easier for users.