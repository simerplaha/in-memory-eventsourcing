package com.commerce.aggregate.shop.manager

import akka.typed.ScalaDSL._
import akka.typed._
import com.base._
import com.commerce.actor.utils.ActorUtil._
import com.commerce.aggregate.shop.ShopCommands._
import com.commerce.aggregate.shop.ShopEvents.{ShopCreated, ShopDeleted}
import com.commerce.aggregate.shop._
import com.commerce.aggregate.shop.manager.ShopManagerEvents.{ShopCreatedUM, ShopDeletedUM}
import com.commerce.aggregate.user.UserCommand
import com.commerce.database.DatabaseEvent
import com.commerce.enums.EventType

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scalaz.Scalaz._

object ShopManager {
  def props(parent: ActorRef[UserCommand]) =
    Props(new ShopManager(parent).recover)
}

class ShopManager(parent: ActorRef[UserCommand]) extends AggregateBase[ShopManagerCommand, ShopManagerEvent, ShopManagerState] {

  import ShopManagerService._

  override def getBehavior(state: ShopManagerState): Behavior[ShopManagerCommand] =
    created(state)


  protected def created(state: ShopManagerState): Behavior[ShopManagerCommand] =
    Full[ShopManagerCommand] {
      case Sig(_, terminated: Terminated) =>
        created(processCommand(terminated, state))
      case Sig(_, failed: Failed) =>
        failed.decide(Failed.Stop)
        Same
      case Msg(ctx, command: ShopManagerChildCommand) =>
        created(processCommand(command, state, ctx))
      case Msg(ctx, command: ShopManagerFinder) =>
        processCommand(command, state)
        Same
      case Msg(ctx, alert: ShopManagerAlert) =>
        created(processAlert(alert, state))
      case Msg(ctx, command: GetCreatedShops) =>
        processCommand(command, state, ctx)
        Same
    }


  override protected def initializeChildren(state: ShopManagerState, ctx: ActorContext[ShopManagerCommand]): Unit =
    state.shopIds foreach {
      shopId =>
        val initialCommand = ShopDefaultSettings(shopId, state.setting, ctx.self, system).some
        findOrCreate(ctx, Shop.props(ctx.self), shopId, initialCommand)
    }

  override def databaseEvents(id: String, parentId: String): List[DatabaseEvent] = {
    val eventsFuture: Future[Seq[DatabaseEvent]] = eventDAO.getEvents(EventType.Shop, Set(classOf[ShopCreated], classOf[ShopDeleted]))
    Await.result(eventsFuture, queryTimeout seconds).toList
  }

  override def convertEvent(id: String, event: Event[_]): Option[ShopManagerEvent] = {
    event match {
      case event: ShopCreated =>
        ShopCreatedUM(id, event.name).some
      case event: ShopDeleted =>
        ShopDeletedUM(id).some
    }
  }

  override val initialState: ShopManagerState =
    ShopManagerState(
      parent = parent,
      shopIds = List.empty[String],
      nextId = 0,
      inProgressCreateCommands = List.empty[CreateShop],
      setting = ShopSetting()
    )

}
