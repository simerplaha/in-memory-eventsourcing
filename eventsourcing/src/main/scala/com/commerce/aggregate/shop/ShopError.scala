package com.commerce.aggregate.shop

import com.commerce.enums.ErrorType
import com.macroz.json.mapper.SealedTraitChildrenExistenceChecker


sealed trait ShopError extends ErrorType

sealed trait ShopNameError

//Name
case class NameIsTakenError(name: String) extends ShopError with ShopNameError

case object NameIsRequiredError extends ShopError with ShopNameError

case class NameIsInvalidError(name: String) extends ShopError with ShopNameError

//Intro
case object IntroIsRequiredError extends ShopError

case class IntroIsInvalidError(intro: String) extends ShopError

//Shop root
case class ErrorShopDoesNotExists(shopId: String) extends ShopError

case class ErrorShopCreationIsInProgress(shopName: String) extends ShopError

case class ErrorShopAlreadyExists(shopName: String) extends ShopError


object ShopError {

  val iter = SealedTraitChildrenExistenceChecker.childExists[ShopError]()

  def exists(name: String): Boolean =
    iter(name)

}