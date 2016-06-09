package com.commerce.aggregate.user

import akka.typed.ActorRef
import com.base._
import com.commerce.aggregate.common.state.Address
import com.commerce.aggregate.shop.manager.ShopManagerCommand
import com.commerce.aggregate.user.manager.{UserManagerCommand, UserSetting}
import com.commerce.enums.{AggregateIdPrefix, Language}
import com.commerce.enums.Language.Language

import scalaz.NonEmptyList

trait UserCommand extends Command {
  def userId = destinationId

  def userIdWithoutPrefix =
    destinationId.replaceFirst(AggregateIdPrefix.user, "").toLong
}

object UserCommands {

  case class CreateUser(destinationId: String,
                        email: Option[String],
                        password: Option[String],
                        firstName: Option[String],
                        lastName: Option[String],
                        address: Option[Address],
                        challengeQuestion: Option[String],
                        challengeAnswer: Option[String],
                        phoneNumber: Option[String],
                        creator: String,
                        language: Language)(val replyTo: ActorRef[Response]) extends UserCommand with ReplyableCommand

  case class UpdateUser(destinationId: String,
                        email: Option[String],
                        password: Option[String],
                        firstName: Option[String],
                        lastName: Option[String],
                        address: Option[Address],
                        challengeQuestion: Option[String],
                        challengeAnswer: Option[String],
                        phoneNumber: Option[String],
                        creator: String,
                        language: Language)(val replyTo: ActorRef[Response]) extends UserCommand with ReplyableCommand


  case class DeleteUser(destinationId: String,
                        creator: String,
                        language: Language)(val replyTo: ActorRef[Response]) extends UserCommand with ReplyableCommand

  case class UnDeleteUser(destinationId: String,
                          creator: String,
                          language: Language)(val replyTo: ActorRef[Response]) extends UserCommand with ReplyableCommand


  case class ActivateUser(destinationId: String,
                          creator: String,
                          language: Language)(val replyTo: ActorRef[Response]) extends UserCommand with ReplyableCommand

  case class DeactivateUser(destinationId: String,
                            creator: String,
                            language: Language)(val replyTo: ActorRef[Response]) extends UserCommand with ReplyableCommand

  case class LoginUser(destinationId: String,
                       creator: String,
                       language: Language)(val replyTo: ActorRef[Response]) extends UserCommand with ReplyableCommand

  case class LogoutUser(destinationId: String,
                        creator: String,
                        language: Language)(val replyTo: ActorRef[Response]) extends UserCommand with ReplyableCommand


  case class UserFinder(destinationId: String, finder: UserState => Option[UserState], creator: String)(val replyTo: ActorRef[Option[UserState]]) extends UserCommand with Finder[UserState]

  case class ValidationSuccessful(destinationId: String, creator: String, events: NonEmptyList[UserEvent]) extends UserCommand

  case class ValidationFailed(destinationId: String, creator: String, errors: NonEmptyList[ErrorInternal]) extends UserCommand

  case class UserChildCommand(command: ShopManagerCommand)(val replyTo: ActorRef[Response]) extends UserCommand with WrapperCommand with ReplyableCommand {
    override val destinationId: String = command.userId
    override val creator: String = command.creator
    override val language: Language = Language.English
  }

  case class UserAggregatesData(destinationId: String, aggregates: List[ActorRef[UserCommand]], creator: String) extends UserCommand with AggregatorCommand[UserCommand]

  case class UserDefaultSettings(destinationId: String, userSetting: UserSetting, parent: ActorRef[UserManagerCommand], creator: String) extends UserCommand

}