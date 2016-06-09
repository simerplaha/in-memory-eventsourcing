package com.commerce.aggregate.shop

import akka.typed.ActorRef
import com.base.State
import com.commerce.aggregate.common.state.Address
import com.commerce.aggregate.shop.ShopCommands.CreateShop
import com.commerce.aggregate.shop.manager.{ShopManagerCommand, ShopSetting}
import com.commerce.enums.Language.Language

sealed trait ShopStates extends State

case class ShopState(name: String,
                     address: Address,
                     intro: String,
                     description: String,
                     returnPolicy: String,
                     openingTime: Long,
                     closingTime: Long,
                     phoneNumber: String,
                     adminIds: List[String],
                     followerIds: List[String],
                     parent: ActorRef[ShopManagerCommand],
                     setting: ShopSetting,
                     active: Boolean,
                     deleted: Boolean,
                     pendingCreation: Boolean,
                     createShopCommand: Option[CreateShop],
                     shopInfoByLanguage: Map[Language, ShopStateByLanguage]) extends ShopStates {
  def shopState(language: Language) =
    shopInfoByLanguage(_)
}

case class ShopStateByLanguage(firstName: String,
                               lastName: String,
                               address: Address,
                               challengeQuestion: String,
                               challengeAnswer: String,
                               phoneNumber: String)