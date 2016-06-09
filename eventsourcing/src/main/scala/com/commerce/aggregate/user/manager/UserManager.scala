package com.commerce.aggregate.user.manager

import akka.typed.ScalaDSL._
import akka.typed._
import com.base._
import com.commerce.actor.utils.ActorUtil._
import com.commerce.aggregate.user.UserCommands._
import com.commerce.aggregate.user.UserEvents.{UserCreated, UserDeleted}
import com.commerce.aggregate.user._
import com.commerce.aggregate.user.manager.UserManagerEvents.{UserCreatedUM, UserDeletedUM}
import com.commerce.database.DatabaseEvent
import com.commerce.enums.EventType

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scalaz.Scalaz._

object UserManager {
  def props = Props(new UserManager().recover)
}

class UserManager extends AggregateBase[UserManagerCommand, UserManagerEvent, UserManagerState] {

  import UserManagerService._

  override def getBehavior(state: UserManagerState): Behavior[UserManagerCommand] =
    created(state)


  protected def created(state: UserManagerState): Behavior[UserManagerCommand] =
    Full[UserManagerCommand] {
      case Sig(_, terminated: Terminated) =>
        created(processCommand(terminated, state))
      case Sig(_, failed: Failed) =>
        failed.decide(Failed.Stop)
        Same
      case Msg(ctx, command: UserManagerChildCommand) =>
        created(processCommand(command, state, ctx))
      case Msg(ctx, command: UserManagerFinder) =>
        processCommand(command, state)
        Same
      case Msg(ctx, alert: UserManagerAlert) =>
        created(processAlert(alert, state))
      case Msg(ctx, command: GetCreatedUsers) =>
        processCommand(command, state, ctx)
        Same
    }


  override protected def initializeChildren(state: UserManagerState, ctx: ActorContext[UserManagerCommand]): Unit =
    state.userIds foreach {
      userId =>
        val initialCommand = UserDefaultSettings(userId, state.setting, ctx.self, system).some
        findOrCreate(ctx, User.props(ctx.self), userId, initialCommand)
    }

  override def databaseEvents(id: String, parentId: String): List[DatabaseEvent] = {
    val eventsFuture: Future[Seq[DatabaseEvent]] = eventDAO.getEvents(EventType.User, Set(classOf[UserCreated], classOf[UserDeleted]))
    Await.result(eventsFuture, queryTimeout seconds).toList
  }

  override def convertEvent(id: String, event: Event[_]): Option[UserManagerEvent] = {
    event match {
      case event: UserCreated =>
        UserCreatedUM(id, event.email).some
      case event: UserDeleted =>
        UserDeletedUM(id).some
    }
  }

  override val initialState: UserManagerState =
    UserManagerState(
      userIds = List.empty[String],
      nextId = 0,
      inProgressCreateUserCommands = List.empty[CreateUser],
      setting = UserSetting()
    )

}
