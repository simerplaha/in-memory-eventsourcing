package com.commerce.aggregate.shop.manager

import akka.typed.ActorRef
import com.base.State
import com.commerce.aggregate.common.state.{EasyPasswordStrength, PasswordStrength}
import com.commerce.aggregate.shop.ShopCommands.CreateShop
import com.commerce.aggregate.user.UserCommand
import com.commerce.enums.AggregateIdPrefix
import com.commerce.utils.RegexUtils

import scala.util.matching.Regex


case class ShopManagerState(shopIds: List[String],
                            nextId: Long,
                            parent: ActorRef[UserCommand],
                            inProgressCreateCommands: List[CreateShop],
                            setting: ShopSetting) extends State {
  def nextIdWithPrefix = (parent.path.name + ":") + AggregateIdPrefix.shop + nextId
}

case class ShopSetting(maximumFirstNameCharacters: Int = 100,
                       maximumLastNameCharacters: Int = 100,
                       minimumFirstNameCharacters: Int = 0,
                       minimumLastNameCharacters: Int = 0,
                       passwordStrength: PasswordStrength = EasyPasswordStrength(),
                       emailValidatorRegex: Regex = RegexUtils.emailRegex)

