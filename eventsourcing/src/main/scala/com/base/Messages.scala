package com.base

import akka.typed.ActorRef
import com.commerce.enums.ErrorType
import com.commerce.enums.EventType._
import com.commerce.enums.Language.Language
import org.joda.time.DateTime

import scalaz.NonEmptyList

trait Command {
  val destinationId: String
  val creator: String
}

trait WrapperCommand extends Command

trait ReplyableCommand extends Command {
  val replyTo: ActorRef[Response]
  val language: Language
}


/**
  * Alerts are just messages sent to actor to inform them of a change. These messages don't really persist anything.
  * Example case would be: A User is successfully created and the UserManager is alerted of the creation.
  * @tparam E
  */
trait Alert[E <: Event[_]] extends Command {
  val toEvents: List[E]
}

/**
  * Finder are just used to search for Actors which satisfy the finder search function.
  */

trait Finder[S <: State] extends Command {
  val finder: S => Option[S]
  val replyTo: ActorRef[Option[S]]
}

trait AggregatorCommand[T] {
  val aggregates: List[ActorRef[T]]
}

case class ErrorInternal(errorCode: ErrorType, language: Language)

case class ErrorMessages(messages: NonEmptyList[ErrorInternal]) extends Response

case class Timeout(message: String = "Ops! Request timed out.") extends Response

trait Response

trait Event[S <: State] extends Response {
  def updateState(id: String, oldState: S, createUser: String, createTime: DateTime, tags: List[String] = List.empty[String]): S

  def eventType: EventType
}

trait State extends Response