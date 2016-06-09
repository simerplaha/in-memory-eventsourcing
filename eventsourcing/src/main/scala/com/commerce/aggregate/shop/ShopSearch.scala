package com.commerce.aggregate.shop

import akka.typed.ActorRef
import com.base.StepWise
import com.commerce.aggregate.shop.ShopCommands.{ShopAggregatesData, ShopFinder}
import com.commerce.aggregate.shop.manager.{GetCreatedShops, ShopManagerCommand}

import scala.concurrent.duration._
import scalaz.Scalaz._

object ShopSearch {

  val system = "system"

  def isShopWithName(name: String)(state: ShopState) =
    if (state.name == name) state.some else None


  def findShopWithName(name: String, shopManager: ActorRef[ShopManagerCommand], replyTo: ActorRef[Option[ShopState]]) = {
    StepWise[Any] {
      (ctx, startWith) ⇒
        val self = ctx.self
        startWith.withKeepTraces(true) {
          shopManager ! GetCreatedShops("", system, "")(self)
          (shopManager, 1)
        }.expectMultipleMessages(1 seconds) {
          case (shops: List[ShopAggregatesData], (domain, previousCount)) ⇒

            shops.head.aggregates foreach {
              shop =>
                shop ! ShopFinder(shop.path.name, isShopWithName(name), system)(self)
            }
            if (shops.head.aggregates.isEmpty) {
              replyTo ! None
            }
            (domain, shops.head.aggregates.size)
        }.expectMultipleMessages(1 seconds) {
          case (shops: List[Option[ShopState]], (domain, previousCount)) ⇒
            val states = shops.collect {
              case Some(state) =>
                state
            }
            if (states.isEmpty) {
              replyTo ! None
            } else {
              replyTo ! Some(states.head)
            }
        }
    }
  }

}
