package com.commerce.aggregate.user.manager

import akka.typed.{ActorContext, ActorRef, Terminated}
import com.typesafe.scalalogging.LazyLogging
import com.base._
import com.commerce.actor.utils.ActorUtil._
import com.commerce.aggregate.user.UserCommands.{CreateUser, UserAggregatesData, UserDefaultSettings}
import com.commerce.aggregate.user.{ErrorUserCreationIsInProgress, ErrorUserDoesNotExists, User, UserCommand}

import scalaz.NonEmptyList
import scalaz.Scalaz._

object UserManagerService extends ServiceBase with LazyLogging {

  /**
    * WRAPPERS
    */
  def processCommand(command: UserManagerChildCommand, state: UserManagerState, ctx: ActorContext[UserManagerCommand]): UserManagerState = {
    command.userCommand match {
      case command: CreateUser => processCreateUserCommand(command, state, ctx)
      case command: UserCommand => forward(state, ctx, command)
    }
  }


  def processCommand(terminated: Terminated, state: UserManagerState): UserManagerState = {
    val terminatedId = terminated.ref.path.name
    val newProgressCreateCommand = state.inProgressCreateUserCommands.filterNot(_.destinationId == terminatedId)
    state.copy(inProgressCreateUserCommands = newProgressCreateCommand)
  }

  def processCommand(command: GetCreatedUsers, state: UserManagerState, ctx: ActorContext[UserManagerCommand]): Unit = {
    val allUserAggregates =
      state.userIds.collect {
        case userId if userId != command.ignoreId =>
          findOrCreate(ctx, User.props(ctx.self), userId, getInitialCommand(userId, state, ctx.self).some)
      }
    command.replyTo ! UserAggregatesData(command.replyTo.path.name, allUserAggregates, system)
  }


  /**
    * PRIVATE FUNCTIONS
    */

  private def processCreateUserCommand(command: CreateUser, state: UserManagerState, ctx: ActorContext[UserManagerCommand]): UserManagerState = {
    if (state.inProgressCreateUserCommands.exists(_.email == command.email)) {
      command.replyTo ! ErrorMessages(NonEmptyList(ErrorInternal(ErrorUserCreationIsInProgress(command.email.get), command.language)))
      state
    } else {
      val nextUserId = state.nextIdWithPrefix
      val commandWithId = command.copy(destinationId = nextUserId)(command.replyTo)
      findOrCreate(ctx, User.props(ctx.self), nextUserId, getInitialCommand(nextUserId, state, ctx.self).some) ! commandWithId
      state.copy(nextId = state.nextId + 1, inProgressCreateUserCommands = state.inProgressCreateUserCommands.+:(commandWithId))
    }
  }

  private def forward(state: UserManagerState, ctx: ActorContext[UserManagerCommand], command: UserCommand): UserManagerState = {
    if (state.userIds.contains(command.destinationId)) {
      val userId = command.destinationId
      val initialCommand = getInitialCommand(userId, state, ctx.self)
      val user = findOrCreate(ctx, User.props(ctx.self), userId, initialCommand.some)
      user ! command
    }
    else command match {
      case command: ReplyableCommand =>
        command.replyTo ! ErrorMessages(NonEmptyList(ErrorInternal(ErrorUserDoesNotExists(command.destinationId), command.language)))
      case command: Command =>
        logger.info(s"******Ignoring command $command because it's not Replyable")
    }
    state
  }

  private def getInitialCommand(userId: String, state: UserManagerState, self: ActorRef[UserManagerCommand]) =
    UserDefaultSettings(userId, state.setting, self, system)


}
