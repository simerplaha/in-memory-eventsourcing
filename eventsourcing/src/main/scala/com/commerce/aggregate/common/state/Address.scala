package com.commerce.aggregate.common.state

case class Address(address1: Option[String],
                   address2: Option[String],
                   address3: Option[String],
                   address4: Option[String],
                   city: Option[String],
                   state: Option[String],
                   zip: Option[String],
                   country: Option[String],
                   latitude: Option[Double],
                   longitude: Option[Double])

object Address {
  def initialState =
    Address(
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None
    )
}