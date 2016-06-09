# In memory eventsourced demo app

## Stack
* Akka-typed
* Postgres
* SBT

## What does it do
My goal here was to see if I can use Actors to hold the state of each domain object in memory and to be able to search
the in memory actors without persisting it's state to another database like in CQRS.

Look at the following two classes to checkout a DSL to search through the state of actors.
- [UserSearch.scala](https://github.com/simerplaha/in-memory-eventsourcing/blob/master/eventsourcing/src/main/scala/com/commerce/aggregate/user/UserSearch.scala)
- [ShopSearch.scala](https://github.com/simerplaha/in-memory-eventsourcing/blob/master/eventsourcing/src/main/scala/com/commerce/aggregate/shop/ShopSearch.scala)






