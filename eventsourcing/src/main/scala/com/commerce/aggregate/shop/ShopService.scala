package com.commerce.aggregate.shop

import akka.typed.ActorContext
import com.base.{ErrorMessages, ServiceBase}
import com.commerce.aggregate.shop.ShopCommands._
import com.commerce.aggregate.shop.ShopEvents.{ShopDeleted, ShopUnDeleted}
import com.commerce.aggregate.shop.manager.AlertShopManagerOfShopCreated
import com.commerce.database.EventDAO._
import com.commerce.events.utils.EventUtils._

import scalaz.Scalaz._

object ShopService extends ServiceBase {

  def processCommand(command: UnDeleteShop, state: ShopState): ShopState = {
    val event = ShopUnDeleted()
    persist(command.destinationId, List(event), command.creator)
    updateStateForEvent(state, event)
  }

  def processCommand(command: CreateShop, state: ShopState, ctx: ActorContext[ShopCommand]): ShopState = {
    ShopValidator.run(command, state, ctx)
    state.copy(pendingCreation = true, createShopCommand = command.some)
  }


  def processCommand(command: DeleteShop, state: ShopState, selfId: String): ShopState = {
    val event = ShopDeleted()
    persist(selfId, List(event), command.creator)
    updateStateForEvent(state, event)
  }


  def processCommand(command: ShopDefaultSettings, state: ShopState): ShopState =
    state.copy(
      setting = command.shopSetting,
      parent = command.parent
    )

  //  def processCommand(command: ShopCommand, ctx: ActorContext[ShopCommand]): Unit = {
  //    val shopManagerChild = ctx.child(AggregateIdPrefix.shopManager)
  //    val shopManager = shopManagerChild.get.asInstanceOf[ActorRef[ShopManagerCommand]]
  //    shopManager ! ShopCommandWrapper(shopManager.path.name, command.createShop, command)
  //  }

  def processValidation(command: ValidationSuccessful, ctx: ActorContext[ShopCommand], state: ShopState): ShopState = {
    val events = command.events.toList
    val selfId = ctx.self.path.name
    val createShopCommand = state.createShopCommand.get
    persist(selfId, events, command.creator)
    val updatedState = updateStateForEvents(state, events)
    state.parent ! AlertShopManagerOfShopCreated(selfId, createShopCommand, system)
    createShopCommand.replyTo ! updatedState
    updatedState
  }

  def processValidation(command: ValidationFailed, state: ShopState): Unit =
    state.createShopCommand.get.replyTo ! ErrorMessages(command.errors)


}
