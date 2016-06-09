# In memory eventsourced demo app

Small application to test an Eventsourced type architecture but keeping all the domain objects in memory

## Stack
* Akka-typed
* PostgresSQL
* SBT

Required: Update postgres configuration in application.conf.

## Goal
- To see if I can use Actors to hold the state of each domain object in memory and to be able to search
the in memory actors without persisting it's state to another database like in CQRS.
- To see if I can abstract out index creation (for searching large data sets) of Actor's state using
Lucene by just implementing a DSL (Not started on this yet)

The overall goal was to just create domain objects using Actors and Events and searching through the Actors state in memory
efficiently without sacrificing performance.

Look at the following two classes to checkout how to search through the state of actors.
- [UserSearch.scala](https://github.com/simerplaha/in-memory-eventsourcing/blob/master/eventsourcing/src/main/scala/com/commerce/aggregate/user/UserSearch.scala)
- [ShopSearch.scala](https://github.com/simerplaha/in-memory-eventsourcing/blob/master/eventsourcing/src/main/scala/com/commerce/aggregate/shop/ShopSearch.scala)

## Events

Set property persistEvents to true in application.conf file to start saving events to database.

## Actor hierarchy

UserManager -> User -> ShopManager -> Shop

## TODO
- Add more comments to the code
- Improve test cases
- Add and test more features.
- Exception messages are not printed in StackTrace when using StepWise because of actor timeout.