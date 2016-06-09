# In memory eventsourced demo app

## Stack
* Akka-typed
* Postgres
* SBT

## Goal
- To see if I can use Actors to hold the state of each domain object in memory and to be able to search
the in memory actors without persisting it's state to another database like in CQRS.
- To see if I can abstract out index creation (for searching large data sets) of Actor's state using
Lucene by just implementing a DSL. (Not started on this yet)

The overall goal was to just create domain objects using Actors and Events and searching through the Actors state in memory.

Look at the following two classes to checkout how to search through the state of actors.
- [UserSearch.scala](https://github.com/simerplaha/in-memory-eventsourcing/blob/master/eventsourcing/src/main/scala/com/commerce/aggregate/user/UserSearch.scala)
- [ShopSearch.scala](https://github.com/simerplaha/in-memory-eventsourcing/blob/master/eventsourcing/src/main/scala/com/commerce/aggregate/shop/ShopSearch.scala)

TODO
- Add more comments to the code
- Improve test cases
- Add and test more features.

