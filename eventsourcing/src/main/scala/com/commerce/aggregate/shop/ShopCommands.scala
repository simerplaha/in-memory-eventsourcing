package com.commerce.aggregate.shop

import akka.typed.ActorRef
import com.base._
import com.commerce.aggregate.common.state.Address
import com.commerce.aggregate.shop.manager.{ShopManagerCommand, ShopSetting}
import com.commerce.enums.Language.Language

import scalaz.NonEmptyList

trait ShopCommand extends Command {
  def destinationIdSplit: Array[String] = destinationId.split(":")
  def userId = destinationIdSplit.head
  def shopId = destinationIdSplit.last
}

object ShopCommands {

  case class CreateShop(destinationId: String,
                        name: String,
                        intro: String,
                        phoneNumber: Long,
                        address: Address,
                        creator: String,
                        language: Language)(val replyTo: ActorRef[Response]) extends ShopCommand with ReplyableCommand

  case class ShopFinder(destinationId: String, finder: ShopState => Option[ShopState], creator: String)(val replyTo: ActorRef[Option[ShopState]]) extends ShopCommand with Finder[ShopState]

  case class ValidationSuccessful(destinationId: String, creator: String, events: NonEmptyList[ShopEvent]) extends ShopCommand

  case class ValidationFailed(destinationId: String, creator: String, errors: NonEmptyList[ErrorInternal]) extends ShopCommand

//  case class ShopIsWithEmail(destinationId: String, state: ShopState, createShop: String) extends ShopCommand
//
//  case class ShopIsNotWithEmail(destinationId: String, state: ShopState, createShop: String) extends ShopCommand

//  case class IsShopWithEmail(destinationId: String, email: String, createShop: String)(val replyTo: ActorRef[ShopCommand]) extends ShopCommand

  case class ShopAggregatesData(destinationId: String, aggregates: List[ActorRef[ShopCommand]], creator: String) extends ShopCommand with AggregatorCommand[ShopCommand]

  case class ShopDefaultSettings(destinationId: String, shopSetting: ShopSetting, parent: ActorRef[ShopManagerCommand], creator: String) extends ShopCommand

  case class DeactivateShop(destinationId: String, creator: String, language: Language)(val replyTo: ActorRef[Response]) extends ShopCommand

  case class ActivateShop(destinationId: String, creator: String, language: Language)(val replyTo: ActorRef[Response]) extends ShopCommand

  case class LoginShop(destinationId: String, creator: String, language: Language)(val replyTo: ActorRef[Response]) extends ShopCommand

  case class LogoutShop(destinationId: String, creator: String, language: Language)(val replyTo: ActorRef[Response]) extends ShopCommand

  case class DeleteShop(destinationId: String, creator: String, language: Language)(val replyTo: ActorRef[Response]) extends ShopCommand

  case class UnDeleteShop(destinationId: String, creator: String, language: Language)(val replyTo: ActorRef[Response]) extends ShopCommand

}