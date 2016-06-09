package com.commerce.database

import com.typesafe.scalalogging.LazyLogging
import com.base.Event
import com.commerce.database.DBConfig.slickDriver.api._
import com.commerce.database.SchemaWrite.events
import com.commerce.enums.EventType.EventType
import com.commerce.events.utils.EventUtils
import com.main.config.ConfigProps
import org.joda.time.DateTime

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object EventDAO extends LazyLogging {

  val queryTimeout = ConfigProps.queryTimeout

  val persistenceEnabled = ConfigProps.persistenceEnabled

  val timeoutDuration = 500.seconds

  val emptyList = List.empty[String]


  val database = DBConfig.database

  def createEvent(event: DatabaseEvent): Future[Int] =
    database.run(events += event)

  def createEvents(event: List[DatabaseEvent]): Future[Option[Int]] =
    database.run(events ++= event)

  def getEvents(id: String): Future[Seq[DatabaseEvent]] = {
    val query = events.filter(_.persistentId === id).sortBy(_.createTime)
    database.run(query.result)
  }

  def getEvents(eventType: EventType): Future[Seq[DatabaseEvent]] = {
    val query = events.filter(_.eventType === eventType.toString).sortBy(_.createTime)
    database.run(query.result)
  }

  def getEvents(eventType: EventType, eventClasses: Set[Class[_]]): Future[Seq[DatabaseEvent]] = {
    val eventNameStrings: Set[String] = eventClasses.map(_.getSimpleName)
    val query = events.filter {
      event =>
        event.eventType === eventType.toString && event.eventName.inSet(eventNameStrings)
    }.sortBy(_.createTime)
    database.run(query.result)
  }

  def getEvents(parentId: String, eventType: EventType, eventNames: Set[String]): Future[Seq[DatabaseEvent]] = {
    val query = events.filter {
      event =>
        event.eventType === eventType.toString && event.eventName.inSet(eventNames) && event.persistentId.startsWith(parentId + ":")
    }.sortBy(_.createTime)
    database.run(query.result)
  }

  def deleteAllEvents: Future[Int] =
    database.run(events.delete)

  def persist[E <: Event[_]](id: String,
                             events: List[E],
                             createUser: String,
                             tags: List[String] = emptyList): DateTime = {
    val createTime = new DateTime()
    persistenceEnabled match {
      case true =>
        persist(id, events, tags, createUser, createTime)
        createTime
      case false =>
        createTime
    }
  }

  private def persist(id: String, events: List[Event[_]], tags: List[String], createUser: String, createTime: DateTime): Unit = {
    val persistentEvents: List[DatabaseEvent] = events map (event => EventUtils.getPersistentEvent(id, event, createUser, createTime, tags))
    Await.result(createEvents(persistentEvents), queryTimeout seconds)
  }
}
