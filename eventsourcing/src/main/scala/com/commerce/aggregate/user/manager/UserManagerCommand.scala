package com.commerce.aggregate.user.manager

import akka.typed.ActorRef
import com.base._
import com.commerce.aggregate.user.UserCommand
import com.commerce.aggregate.user.UserCommands.{CreateUser, UserAggregatesData}
import com.commerce.aggregate.user.manager.UserManagerEvents.{CreateUserCommandSuccessful, UserDeletedUM}
import com.commerce.enums.Language.Language
import com.commerce.enums.{AggregateIdPrefix, Language}

trait UserManagerCommand extends Command {
  val userManagerId = AggregateIdPrefix.userManager
}

trait UserManagerAlert extends UserManagerCommand with Alert[UserManagerEvent]

/**
  * COMMANDS
  */
case class UserManagerChildCommand(userCommand: UserCommand)(val replyTo: ActorRef[Response]) extends UserManagerCommand with ReplyableCommand {
  override val destinationId: String = userManagerId
  override val creator: String = "system"
  override val language: Language = Language.English
}

case class UserManagerFinder(destinationId: String, finder: UserManagerState => Option[UserManagerState], creator: String)(val replyTo: ActorRef[Option[UserManagerState]]) extends UserManagerCommand with Finder[UserManagerState]

case class GetCreatedUsers(destinationId: String, creator: String, ignoreId: String)(val replyTo: ActorRef[UserAggregatesData]) extends UserManagerCommand

/**
  * ALERTS
  */
case class AlertUserManagerOfUserCreated(destinationId: String, createUserCommand: CreateUser, creator: String) extends UserManagerAlert {
  override val toEvents: List[UserManagerEvent] =
    List(
      CreateUserCommandSuccessful(createUserCommand)
    )
}

case class AlertUserManagerOfUserDeleted(destinationId: String, deletedUserId: String, creator: String) extends UserManagerAlert {
  override val toEvents: List[UserManagerEvent] =
    List(
      UserDeletedUM(deletedUserId)
    )
}