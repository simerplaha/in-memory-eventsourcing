package com.utils.command

import com.commerce.aggregate.common.state.Address
import com.commerce.enums.AddressError._

object CommandValidationUtils {


  def isNegative(value: Double): Boolean = {
    value < 0.0
  }

  def isNegative(value: Int): Boolean =
    value < 0

  def isEmpty(string: String): Boolean =
    string == ""

  def isLengthMinimum(string: String, minimumLength: Int): Boolean =
    string.length >= minimumLength

  def isLengthMaximum(string: String, maximumLength: Int): Boolean =
    string.length <= maximumLength

  def isLengthBetween(string: String, minLength: Int, maxLength: Int): Boolean =
    isLengthMinimum(string, minLength) && isLengthMaximum(string, maxLength)

  def isValidEmail(email: String): Boolean =
    """(?=[^\s]+)(?=(\w+)@([\w\.]+))""".r.findFirstIn(email).isEmpty

  def isValidAddress(address: Address): List[String] =
    List(
      if (address.address1.isDefined) isValidAddress1(address.address1.get),
      if (address.address2.isDefined) isValidAddress2(address.address2.get),
      if (address.city.isDefined) isValidCity(address.city.get),
      if (address.country.isDefined) isValidCountry(address.country.get),
      if (address.latitude.isDefined) isValidLatitude(address.latitude.get.toString),
      if (address.longitude.isDefined) isValidLongitude(address.longitude.get.toString),
      if (address.state.isDefined) isValidState(address.state.get),
      if (address.zip.isDefined) isValidZip(address.zip.get)

    ).filter(_.isInstanceOf[String]).asInstanceOf[List[String]]

  def isValidAddress1(address1: String): Option[String] =
    if (address1.length > 1000) Some(`length cannot greater than`("Address1", 1000)) else None

  def isValidAddress2(address2: String): Option[String] =
    if (address2.length > 1000) Some(`length cannot greater than`("Address2", 1000)) else None

  def isValidCountry(country: String): Option[String] =
    if (country.length > 1000) Some(`length cannot greater than`("Country", 1000)) else None

  def isValidCity(city: String): Option[String] =
    if (city.length > 1000) Some(`length cannot greater than`("City", 1000)) else None

  def isValidState(state: String): Option[String] =
    if (state.length > 1000) Some(`length cannot greater than`("State", 1000)) else None

  def isValidLongitude(longitude: String): Option[String] =
    if (longitude.length > 1000) Some(`length cannot greater than`("Longitude", 1000)) else None

  def isValidLatitude(latitude: String): Option[String] =
    if (latitude.length > 1000) Some(`length cannot greater than`("Latitude", 1000)) else None

  def isValidZip(zip: String): Option[String] =
    if (zip.length > 1000) Some(`length cannot greater than`("Zip", 1000)) else None


}
