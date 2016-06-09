package com.commerce.events.utils

import com.commerce.aggregate.shop.ShopEvent
import com.commerce.aggregate.user.UserEvent
import com.commerce.enums.EventType
import com.commerce.enums.EventType.EventType
import com.macroz.json.mapper.EventMapperMacro._


object EventsMapper {

  //Argonaut._, Shapeless._ imports are required
  import argonaut._, Argonaut._, Shapeless._

  val shopEventsMapper = generatePatternMatcher[ShopEvent]()

  val userEventsMapper = generatePatternMatcher[UserEvent]()

  def eventMapper(eventType: EventType): (String, String) => Option[_] =
    eventType match {
      case EventType.User =>
        userEventsMapper
      case EventType.Shop =>
        shopEventsMapper
    }


}


