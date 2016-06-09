package com.base

import com.commerce.database.{DatabaseEvent, EventDAO}
import com.main.config.ConfigProps

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * This trait is used for Aggregates default implementation.
  */
trait AggregateDefaultImpl[E <: Event[_]] {

  val timeout = ConfigProps.queryTimeout

  def databaseEvents(id: String, parentId: String): List[DatabaseEvent] =
    Await.result(EventDAO.getEvents(id), timeout seconds).toList

  /**
    * Aggregate that are not AggregateManager mostly read their own events that's why this returns None.
    */
  def convertEvent(id: String, event: Event[_]): Option[E] = None

}
