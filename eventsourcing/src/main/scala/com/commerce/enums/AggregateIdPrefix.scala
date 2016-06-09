package com.commerce.enums

object AggregateIdPrefix {

  import Prefixes._

  val user = U.toString
  val userValidator = UV.toString
  val userManager = UM.toString

  val shop = S.toString
  val shopValidator = SV.toString
  val shopManager = SM.toString

  val customerManager = CM.toString
  val customer = C.toString

  val productManager = PM.toString
  val product = P.toString

  val emailAvailabilityChecker = EmailAvailabilityChecker.toString

}

object Prefixes extends Enumeration {

  val U, UM, UV, S, SM, SV, CM, C, PM, P = Value

  val EmailAvailabilityChecker = Value

}