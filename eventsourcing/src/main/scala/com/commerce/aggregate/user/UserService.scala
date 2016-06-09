package com.commerce.aggregate.user

import akka.typed.{ActorContext, ActorRef}
import com.base.{ErrorMessages, ServiceBase}
import com.commerce.aggregate.shop.manager.ShopManagerCommand
import com.commerce.aggregate.user.UserCommands._
import com.commerce.aggregate.user.UserEvents.{UserDeleted, UserUnDeleted}
import com.commerce.aggregate.user.manager.AlertUserManagerOfUserCreated
import com.commerce.database.EventDAO._
import com.commerce.enums.AggregateIdPrefix
import com.commerce.events.utils.EventUtils._

import scalaz.Scalaz._

object UserService extends ServiceBase {

  def processCommand(command: UnDeleteUser, state: UserState): UserState = {
    val event = UserUnDeleted()
    persist(command.destinationId, List(event), command.creator)
    updateStateForEvent(state, event)
  }

  def processCommand(command: CreateUser, state: UserState, ctx: ActorContext[UserCommand]): UserState = {
    UserValidator.run(command, state, ctx)
    state.copy(pendingCreation = true, createUserCommand = command.some)
  }


  def processCommand(command: DeleteUser, state: UserState, selfId: String): UserState = {
    val event = UserDeleted()
    persist(selfId, List(event), command.creator)
    updateStateForEvent(state, event)
  }


  def processCommand(command: UserDefaultSettings, state: UserState): UserState =
    state.copy(
      setting = command.userSetting,
      parent = command.parent
    )

  def processCommand(command: UserChildCommand, ctx: ActorContext[UserCommand]): Unit = {
    val shopManagerChild = ctx.child(AggregateIdPrefix.shopManager)
    val shopManager = shopManagerChild.get.asInstanceOf[ActorRef[ShopManagerCommand]]
    shopManager ! command.command
  }


  def processValidation(command: ValidationSuccessful, ctx: ActorContext[UserCommand], state: UserState): UserState = {
    val events = command.events.toList
    val selfId = ctx.self.path.name
    val createUserCommand = state.createUserCommand.get
    persist(selfId, events, command.creator)
    val updatedState = updateStateForEvents(state, events)
    state.parent ! AlertUserManagerOfUserCreated(selfId, createUserCommand, system)
    createUserCommand.replyTo ! updatedState
    updatedState
  }

  def processValidation(command: ValidationFailed, state: UserState): Unit =
    state.createUserCommand.get.replyTo ! ErrorMessages(command.errors)


}
