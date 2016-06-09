package com.commerce.enums

object EventType extends Enumeration {
  type EventType = Value

  val Shop, Product, ProductVariation, ShopManager, User, Cart, CartCoupon, CategoryGroup, Developer, Market, Customer, UserManager = Value

  val Error = Value

}
