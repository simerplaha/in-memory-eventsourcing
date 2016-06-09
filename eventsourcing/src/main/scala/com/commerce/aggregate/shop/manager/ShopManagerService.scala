package com.commerce.aggregate.shop.manager

import akka.typed.{ActorContext, ActorRef, Terminated}
import com.base.{ErrorInternal, ErrorMessages, ReplyableCommand, ServiceBase}
import com.commerce.actor.utils.ActorUtil._
import com.commerce.aggregate.shop.ShopCommands.{CreateShop, ShopAggregatesData, ShopDefaultSettings}
import com.commerce.aggregate.shop.{ErrorShopCreationIsInProgress, ErrorShopDoesNotExists, Shop, ShopCommand}

import scalaz.NonEmptyList
import scalaz.Scalaz._

object ShopManagerService extends ServiceBase {


  def processCommand(command: ShopManagerChildCommand, state: ShopManagerState, ctx: ActorContext[ShopManagerCommand]): ShopManagerState = {
    command.shopCommand match {
      case command: CreateShop => processCommand(command, state, ctx)
      case command: ShopCommand with ReplyableCommand => forward(state, ctx, command)
    }
  }

  def processCommand(terminated: Terminated, state: ShopManagerState): ShopManagerState = {
    val terminatedId = terminated.ref.path.name
    val newProgressCreateCommand = state.inProgressCreateCommands.filterNot(_.destinationId == terminatedId)
    state.copy(inProgressCreateCommands = newProgressCreateCommand)
  }

  def processCommand(command: GetCreatedShops, state: ShopManagerState, ctx: ActorContext[ShopManagerCommand]): Unit = {
    val allShopAggregates =
      state.shopIds.collect {
        case shopId if shopId != command.ignoreId =>
          findOrCreate(ctx, Shop.props(ctx.self), shopId, getInitialCommand(shopId, state, ctx.self).some)
      }
    command.replyTo ! ShopAggregatesData(command.replyTo.path.name, allShopAggregates, system)
  }


  /**
    * PRIVATE FUNCTIONS
    */
  private def processCommand(command: CreateShop, state: ShopManagerState, ctx: ActorContext[ShopManagerCommand]): ShopManagerState = {
    if (state.inProgressCreateCommands.exists(_.name == command.name)) {
      command.replyTo ! ErrorMessages(NonEmptyList(ErrorInternal(ErrorShopCreationIsInProgress(command.name), command.language)))
      state
    } else {
      val nextShopId = state.nextIdWithPrefix
      val commandWithId = command.copy(destinationId = nextShopId)(command.replyTo)
      findOrCreate(ctx, Shop.props(ctx.self), nextShopId, getInitialCommand(nextShopId, state, ctx.self).some) ! commandWithId
      state.copy(
        nextId = state.nextId + 1,
        inProgressCreateCommands = state.inProgressCreateCommands.+:(commandWithId)
      )
    }
  }

  private def forward(state: ShopManagerState, ctx: ActorContext[ShopManagerCommand], command: ShopCommand with ReplyableCommand): ShopManagerState = {
    if (state.shopIds.contains(command.destinationId)) {
      val shopId = command.destinationId
      val initialCommand = getInitialCommand(shopId, state, ctx.self)
      val shop = findOrCreate(ctx, Shop.props(ctx.self), shopId, initialCommand.some)
      shop ! command
    }
    else
      command.replyTo ! ErrorMessages(NonEmptyList(ErrorInternal(ErrorShopDoesNotExists(command.destinationId), command.language)))
    state
  }

  private def getInitialCommand(shopId: String, state: ShopManagerState, self: ActorRef[ShopManagerCommand]) =
    ShopDefaultSettings(shopId, state.setting, self, system)


}
