package com.commerce.aggregate.user

import akka.typed.ScalaDSL._
import akka.typed.{ActorContext, ActorRef, Behavior, Props}
import com.base._
import com.commerce.aggregate.common.state.Address
import com.commerce.aggregate.shop.manager.ShopManager
import com.commerce.aggregate.user.UserCommands._
import com.commerce.aggregate.user.manager.{UserManagerCommand, UserSetting}
import com.commerce.enums.AggregateIdPrefix

object User {
  def props(parent: ActorRef[UserManagerCommand]) = Props(new User(parent).recover)
}

class User(parent: ActorRef[UserManagerCommand]) extends AggregateBase[UserCommand, UserEvent, UserState] with AggregateDefaultImpl[UserEvent] {

  import UserService._

  protected override def getBehavior(state: UserState): Behavior[UserCommand] = {
    if (state == initialState)
      uninitialized(initialState)
    else if (state.pendingCreation)
      pendingCreation(state)
    else if (state.deleted)
      deleted(state)
    else
      created(state)
  }

  protected def uninitialized(state: UserState): Behavior[UserCommand] =
    ContextAware[UserCommand] {
      ctx =>
        Total[UserCommand] {
          case command: CreateUser =>
            getBehavior(processCommand(command, state, ctx))
          case command: UserDefaultSettings =>
            uninitialized(processCommand(command, state))
        }
    }

  protected def pendingCreation(state: UserState): Behavior[UserCommand] =
    ContextAware[UserCommand] {
      ctx =>
        Total[UserCommand] {
          case command: ValidationFailed =>
            processValidation(command, state)
            Stopped
          case command: ValidationSuccessful =>
            getBehavior(processValidation(command, ctx, state))
          case command: UserDefaultSettings =>
            getBehavior(processCommand(command, state))
        }
    }


  private def created(state: UserState): Behavior[UserCommand] =
    ContextAware[UserCommand] {
      ctx =>
        val selfId = ctx.self.path.name
        Total[UserCommand] {
          case command: DeleteUser =>
            getBehavior(processCommand(command, state, selfId))
          case command: UserDefaultSettings =>
            getBehavior(processCommand(command, state))
          case command: UserChildCommand =>
            processCommand(command, ctx)
            Same
          case command: UserFinder =>
            processCommand(command, state)
            Same
        }
    }

  private def deleted(state: UserState): Behavior[UserCommand] =
    Total[UserCommand] {
      case command: UnDeleteUser =>
        getBehavior(processCommand(command, state))
    }


  override protected def initializeChildren(state: UserState, ctx: ActorContext[UserCommand]): Unit = {
//    val shopManager = ctx.spawn(ShopManager.props(ctx.self), AggregateIdPrefix.shopManager)
//    UdpReporter.send(ActorCreated(shopManager))
  }

  override val initialState: UserState =
    UserState(
      email = "",
      password = "",
      parent = parent,
      setting = UserSetting(),
      emailConfirmed = false,
      active = false,
      loggedIn = false,
      deleted = false,
      pendingCreation = false,
      createUserCommand = None,
      firstName = "",
      lastName = "",
      address = Address.initialState,
      challengeQuestion = "",
      challengeAnswer = "",
      phoneNumber = ""
    )

}
