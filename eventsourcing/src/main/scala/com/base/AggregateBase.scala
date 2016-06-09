package com.base

import akka.typed.ScalaDSL._
import akka.typed._
import com.typesafe.scalalogging.LazyLogging
import com.commerce.database.{EventDAO, DatabaseEvent}
import com.commerce.events.utils.EventUtils

/**
  * Base class for AggregateManager and Aggregates.
  * It will probably be better to split this into two different traits (one for AggregateManagers & one for Aggregates)
  */

trait AggregateBase[C <: Command, E <: Event[S], S <: State] extends LazyLogging {

  val eventDAO = EventDAO

  protected def recover: Behavior[C] =
    ContextAware[C] {
      ctx =>
        val id = ctx.self.path.name
        Full[C] {
          case Sig(_, PreStart) =>
            logger.info(s"Recovering state for Aggregate: ${this.getClass.getSimpleName}(id = '$id')")
            val recoveredState = recoverState(id, ctx.self.path.parent.name)
            val recoveredBehavior = getBehavior(recoveredState)
            logger.info(s"Finished playingEvents in recovery mode for: ${this.getClass.getSimpleName}(id = '$id')")
            initializeChildren(recoveredState, ctx)
            recoveredBehavior
        }
    }

  private def recoverState(id: String, parentId: String): S = {
    val events = databaseEvents(id, parentId)
    EventUtils.recoverState(initialState, events, convertEvent)
  }

  /**
    * Returns a list of @DatabaseEvent that this aggregate understands.
    *
    * @param id
    * @param parentId
    * @return
    */
  protected def databaseEvents(id: String, parentId: String): List[DatabaseEvent]

  /**
    * AggregateManager don't really persist any data so they read Events of the children to build up their state
    * This converter will convert a child aggregate Event to an Event of this AggregateManager's type.
    */
  protected def convertEvent(id: String, event: Event[_]): Option[E]

  /**
    * Since every Aggregate can have child Aggregate, this function is invoked when this Actor is
    * initialized. If the extending class is an AggregateManager then the child
    * would be an Aggregate and if the extending class is an AggregateManager the children would be
    * Aggregates.
    *
    * @param state initial state to be set to the child aggregate
    * @param ctx   used to create and instance of the child aggregate.
    */
  protected def initializeChildren(state: S, ctx: ActorContext[C]): Unit

  /**
    * Given a state returns the next behavior of the Actor
    */
  protected def getBehavior(state: S): Behavior[C]

  /**
    * Just the initial state of this actor.
    */
  val initialState: S
}
