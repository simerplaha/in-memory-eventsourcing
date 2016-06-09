package com.commerce.enums

//trait DBOErrorType
//
//case class DBOError(errorString: String)
//
//object CommandError {
//  def isTakenBySomeoneElse(what: String, language: String) = DBOError()
//}

object CommonError extends Enumeration {
  type ErrorType = Value

  def `unchanged value`(valueName: String) = s"Unchanged value $valueName"

  val `user already created` = "User is already created."

  def `user with email already exists`(email: String) = s"User with email `$email` already exists."

  val `user id is required` = s"User id is required."

  val `user email id is required` = s"User email is required."

  def `invalid email`(email: String) = s"Invalid email `$email`."

  //Wrapped command
  val `wrapped commands are not runnable` = "Command is not runnable."

  //shopkeeper
  val `Shopkeeper does not exists.` = "Shopkeeper does not exists."

  val `Shopkeeper already exists.` = "Shopkeeper already exists."

  //User
  val `User does not exists.` = "User does not exists."

  //developer
  val `developer does not exists.` = "Developer does not exists."

  val `developer already exists.` = "Developer already exists."

  val `Developer is deleted.` = "Developer is deleted."


  //product
  val `product id is required.` = "Product id is required."

  def `no change.`(what: String) = s"$what no change."

  def `is invalid.`(what: String) = s"$what is invalid."

  def `is required.`(what: String) = Value(s"$what is required.")

  def `already exists.`(what: String) = s"$what already exists."

  def `already exists not.`(what: String) = s"$what does not already exists."

  val `product does not exists.` = "Product does not exists."

  val `product already exists.` = "Product already exists."

  val `Product is deleted.` = "Product is deleted."

  val `product image already exists.` = "Product image already exists."

  val `product image does not exists.` = "Product image does not exists."

  val `product profile image is already set to this image.` = "Product profile image is already set to this image."

  //product variation

  val `product variation id is required` = "Product variation id is required."

  val `product variation image already exists` = "Product variation image already exists."

  val `product variation image does not exists` = "Product variation image does not exists."

  //Shop
  val `Shop admin user does not exists` = s"Shop admin user does not exists."

  val `Deleted shop cannot be activated` = s"Deleted shop cannot be activated. Try Undeleting it first."

  val `Cannot run commands on deleted shop` = s"Cannot run commands on delete shop."

  val `shop does not exists.` = "Shop does not exists."

  val `Shop is deleted.` = "Shop is deleted."

  val `Product request did not arrive at the correct store.` = "Product request did not arrive at the correct store."

  val `shop name is required` = s"Shop name is required."

  val `shop already exists` = s"Shop already exists."

  val `shop already exists. Try requesting AddShopToMarket` = s"Shop already exists! Try invoking AddShopToMarket"


  val `shop id is required` = s"Shop id is required."

  val `shop owner id is required` = s"Shop owner id is required."

  def `invalid shop name length`(min: Int, max: Int) = s"Required minimum $min characters and maximum $max characters for shop name."

  def `invalid length`(min: Int, max: Int) = s"Required minimum $min characters and maximum $max characters."


  //password
  def `invalid user password length`(min: Int, max: Int) = s"Required minimum $min characters and maximum $max characters for a secured password."

  val `error parsing json` = "Error parsing json"

  val `got invalid response` = "Got invalid response."

  val `not implemented` = "Not implemented"


}