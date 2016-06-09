package com.commerce.events.utils

import java.sql.Timestamp

import com.base.{Event, State}
import com.commerce.database.DatabaseEvent
import com.commerce.enums.EventType
import net.liftweb.json._
import org.joda.time.DateTime

object EventUtils {

  implicit val formats = DefaultFormats

  def toJson(event: Event[_]) = compact(render(Extraction.decompose(event)))

  def toEvent(databaseEvent: DatabaseEvent): Event[_] = {
    val result = toEvent(databaseEvent.eventType, databaseEvent.eventName, databaseEvent.json)
    result
  }

  def toEvents(databaseEvents: List[DatabaseEvent]): List[Event[_]] =
    databaseEvents map toEvent

  def toEvent(eventType: String, eventName: String, json: String): Event[_] = {
    val eventTypeEnum = EventType.withName(eventType)
    EventsMapper.eventMapper(eventTypeEnum)(eventName, json).get.asInstanceOf[Event[_]]
  }

  def getPersistentEvent(id: String, event: Event[_], createUser: String, createTime: DateTime, tags: List[String] = List.empty[String]): DatabaseEvent = {
    val eventJson = toJson(event)

    val eventType = event.eventType.toString
    val eventName = event.getClass.getSimpleName
    DatabaseEvent(id, eventJson, eventName, eventType, tags, createUser, new Timestamp(createTime.getMillis))
  }

  def recoverState[S <: State, E <: Event[S]](state: S, databaseEvent: List[DatabaseEvent], preProcessEvent: (String, Event[_]) => Option[E]): S =
    databaseEvent.foldLeft(state) {
      (state, databaseEvent) =>
        val thisEvent = toEvent(databaseEvent)
        val convertedEvent: Option[E] = preProcessEvent(databaseEvent.persistentId, thisEvent)
        updateStateForEvent(
          state = state,
          event = convertedEvent.getOrElse(thisEvent).asInstanceOf[E],
          id = Some(databaseEvent.persistentId),
          createUser = Some(databaseEvent.createUser),
          createTime = Some(new DateTime(databaseEvent.createTime)),
          tags = databaseEvent.tags
        )
    }

  def recoverState[S <: State, E <: Event[S]](state: S, databaseEvents: List[DatabaseEvent]): S =
    databaseEvents.foldLeft(state) {
      (state, databaseEvent) =>
        val thisEvent = toEvent(databaseEvent)
        updateStateForEvent(
          state = state,
          event = thisEvent.asInstanceOf[E],
          id = Some(databaseEvent.persistentId),
          createUser = Some(databaseEvent.createUser),
          createTime = Some(new DateTime(databaseEvent.createTime)),
          tags = databaseEvent.tags
        )
    }


  def recoverStateForEvents[S <: State, E <: Event[S]](state: S, events: List[E]): S =
    events.foldLeft(state) {
      (state, event) =>
        updateStateForEvent(
          state = state,
          event = event,
          id = None,
          createUser = None,
          createTime = None
        )
    }

  def recoverStateForEvent[S <: State, E <: Event[S]](state: S, event: E): S =
    updateStateForEvent(
      state = state,
      event = event,
      id = None,
      createUser = None,
      createTime = None
    )

  def updateStateForEvent[S <: State, E <: Event[S]](state: S,
                                                     event: E,
                                                     id: Option[String] = None,
                                                     createUser: Option[String] = None,
                                                     createTime: Option[DateTime] = None,
                                                     tags: List[String] = List.empty[String]): S =
    event.updateState(
      id = id.getOrElse(""),
      oldState = state,
      createUser = createUser.getOrElse(""),
      createTime = createTime.getOrElse(new DateTime()),
      tags = tags
    )

  def updateStateForEvents[S <: State, E <: Event[S]](state: S,
                                                      events: List[E],
                                                      id: Option[String] = None,
                                                      createUser: Option[String] = None,
                                                      createTime: Option[DateTime] = None,
                                                      tags: List[String] = List.empty[String]): S =
    events.foldLeft(state) {
      (state: S, event: E) =>
        EventUtils.updateStateForEvent(
          state,
          event,
          id,
          createUser,
          createTime,
          tags
        )
    }
}
