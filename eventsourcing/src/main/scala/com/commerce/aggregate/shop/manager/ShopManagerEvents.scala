package com.commerce.aggregate.shop.manager

import com.base.Event
import com.commerce.aggregate.shop.ShopCommands.CreateShop
import com.commerce.enums.{AggregateIdPrefix, EventType}
import org.joda.time.DateTime

sealed trait ShopManagerEvent extends Event[ShopManagerState] {
  def eventType = EventType.ShopManager
}

object ShopManagerEvents {

  case class ShopCreatedUM(id: String, name: String) extends ShopManagerEvent {
    override def updateState(persistentId: String, state: ShopManagerState, createShop: String, createTime: DateTime, tags: List[String]): ShopManagerState = {
      var nextShopId = state.nextId
      val thisEventsShopId = id.substring(id.lastIndexOf(AggregateIdPrefix.shop) + 1, id.length).toLong
      if (nextShopId <= thisEventsShopId) {
        nextShopId = thisEventsShopId + 1
      }
      state.copy(shopIds = state.shopIds.+:(id), nextId = nextShopId)
    }
  }

  case class ShopDeletedUM(id: String) extends ShopManagerEvent {
    override def updateState(persistentId: String, state: ShopManagerState, createShop: String, createTime: DateTime, tags: List[String]): ShopManagerState =
      state.copy(shopIds = state.shopIds.filterNot(_ == id))
  }

  case class CreateShopCommandSuccessful(createShopCommand: CreateShop) extends ShopManagerEvent {
    override def updateState(id: String, state: ShopManagerState, createShop: String, createTime: DateTime, tags: List[String]): ShopManagerState = {
      state.copy(shopIds = state.shopIds.+:(id), inProgressCreateCommands = state.inProgressCreateCommands.filterNot(_ == createShopCommand))
    }
  }

}



