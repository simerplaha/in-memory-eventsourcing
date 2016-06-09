package com.commerce.aggregate.shop

import akka.typed.ScalaDSL._
import akka.typed.{ActorContext, ActorRef, Behavior, Props}
import com.base._
import com.commerce.aggregate.common.state.Address
import com.commerce.aggregate.shop.ShopCommands._
import com.commerce.aggregate.shop.manager.{ShopManagerCommand, ShopSetting}
import com.commerce.enums.Language.Language

object Shop {
  def props(parent: ActorRef[ShopManagerCommand]) = Props(new Shop(parent).recover)
}

class Shop(parent: ActorRef[ShopManagerCommand]) extends AggregateBase[ShopCommand, ShopEvent, ShopState] with AggregateDefaultImpl[ShopEvent] {

  import ShopService._

  protected override def getBehavior(state: ShopState): Behavior[ShopCommand] = {
    if (state == initialState)
      uninitialized(initialState)
    else if (state.pendingCreation)
      pendingCreation(state)
    else if (state.deleted)
      deleted(state)
    else
      created(state)
  }

  protected def uninitialized(state: ShopState): Behavior[ShopCommand] =
    ContextAware[ShopCommand] {
      ctx =>

        Total[ShopCommand] {
          case command: CreateShop =>
            getBehavior(processCommand(command, state, ctx))
          case command: ShopDefaultSettings =>
            uninitialized(processCommand(command, state))
        }
    }

  protected def pendingCreation(state: ShopState): Behavior[ShopCommand] =
    ContextAware[ShopCommand] {
      ctx =>

        Total[ShopCommand] {
          case command: ValidationFailed =>
            processValidation(command, state)
            Stopped
          case command: ValidationSuccessful =>
            getBehavior(processValidation(command, ctx, state))
          case command: ShopDefaultSettings =>
            getBehavior(processCommand(command, state))
        }
    }


  private def created(state: ShopState): Behavior[ShopCommand] =
    ContextAware[ShopCommand] {
      ctx =>
        val selfId = ctx.self.path.name
        Total[ShopCommand] {
          case command: DeleteShop =>
            getBehavior(processCommand(command, state, selfId))
          case command: ShopDefaultSettings =>
            getBehavior(processCommand(command, state))
            Same
          case command: ShopFinder =>
            processCommand(command, state)
            Same
        }
    }

  private def deleted(state: ShopState): Behavior[ShopCommand] =
    Total[ShopCommand] {
      case command: UnDeleteShop =>
        getBehavior(processCommand(command, state))
    }


  override protected def initializeChildren(state: ShopState, ctx: ActorContext[ShopCommand]): Unit = {
    //    val shopManager = ctx.spawn(ShopManager.props, AggregateIdPrefix.shopManager)
    //    UdpReporter.send(ActorCreated(shopManager))
  }

  override val initialState =
    ShopState(
      name = "",
      address = Address.initialState,
      intro = "",
      description = "",
      returnPolicy = "",
      openingTime = 0,
      closingTime = 0,
      phoneNumber = "",
      adminIds = List.empty[String],
      followerIds = List.empty[String],
      parent = parent,
      setting = ShopSetting(),
      active = false,
      deleted = false,
      pendingCreation = false,
      createShopCommand = None,
      shopInfoByLanguage = Map.empty[Language, ShopStateByLanguage]
    )

}
