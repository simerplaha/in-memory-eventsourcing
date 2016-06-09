package com.commerce.aggregate.shop.manager

import akka.typed.ActorRef
import com.base._
import com.commerce.aggregate.shop.ShopCommand
import com.commerce.aggregate.shop.ShopCommands.{CreateShop, ShopAggregatesData}
import com.commerce.aggregate.shop.manager.ShopManagerEvents.{CreateShopCommandSuccessful, ShopDeletedUM}
import com.commerce.enums.Language.Language
import com.commerce.enums.{Language, AggregateIdPrefix}
import com.commerce.enums.Language.Language

trait ShopManagerCommand extends Command {
  val shopManagerId = AggregateIdPrefix.shopManager

  def destinationIdSplit = destinationId.split(":")

  def userId = destinationIdSplit.head
}

trait ShopManagerAlert extends ShopManagerCommand with Alert[ShopManagerEvent]

/**
  * COMMANDS
  */
case class ShopManagerChildCommand(shopCommand: ShopCommand)(val replyTo: ActorRef[Response]) extends ShopManagerCommand with ReplyableCommand {
  override val destinationId: String = shopCommand.userId + ":" + shopManagerId
  override val creator: String = "system"
  override val language: Language = Language.English
}

case class ShopManagerFinder(destinationId: String, finder: ShopManagerState => Option[ShopManagerState], creator: String)(val replyTo: ActorRef[Option[ShopManagerState]]) extends ShopCommand with Finder[ShopManagerState]

case class GetCreatedShops(destinationId: String, creator: String, ignoreId: String)(val replyTo: ActorRef[ShopAggregatesData]) extends ShopManagerCommand

/**
  * ALERTS
  */
case class AlertShopManagerOfShopCreated(destinationId: String, createShopCommand: CreateShop, creator: String) extends ShopManagerAlert {
  override val toEvents: List[ShopManagerEvent] =
    List(
      CreateShopCommandSuccessful(createShopCommand)
    )
}

case class AlertShopManagerOfShopDeleted(destinationId: String, creator: String) extends ShopManagerAlert {
  override val toEvents: List[ShopManagerEvent] =
    List(
      ShopDeletedUM(destinationId)
    )
}