package com.commerce.enums

object AddressError {

  def `length cannot greater than`(property: String, max: Long) =
    s"$property's length cannot greater than $max."

}
