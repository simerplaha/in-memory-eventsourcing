package com.commerce.aggregate.shop

import com.base.Event
import com.commerce.enums.EventType
import org.joda.time.DateTime

sealed trait ShopEvent extends Event[ShopState] {
  def eventType = EventType.Shop
}


object ShopEvents {

  case class ShopCreated(name: String) extends ShopEvent {
    override def updateState(persistentId: String, oldState: ShopState, createShop: String, createTime: DateTime, tags: List[String]): ShopState =
      oldState.copy(
        name = name
      )
  }

  case class ShopIntroUpdated(intro: String) extends ShopEvent {
    override def updateState(id: String, oldState: ShopState, createShop: String, createTime: DateTime, tags: List[String]): ShopState =
      oldState.copy(intro = intro)
  }


  case class ShopNameUpdated(name: String) extends ShopEvent {
    override def updateState(id: String, oldState: ShopState, createShop: String, createTime: DateTime, tags: List[String]): ShopState =
      oldState.copy(name = name)
  }


  //  case class ShopFirstNameUpdated(firstName: String) extends ShopEvent {
  //    override def updateState(id: String, oldState: ShopState, createShop: String, createTime: DateTime, tags: List[String]): ShopState =
  //      oldState.copy(shopInfoByLanguage = oldState.shopState())
  //  }
  //
  //  case class ShopLastNameUpdated(lastName: String) extends ShopEvent {
  //    override def updateState(persistentId: String, tags: List[String], createShop: String, createTime: DateTime, oldState: ShopState): ShopState =
  //      oldState.copy(lastName = lastName)
  //  }
  //
  //  case class ShopPhoneNumberUpdated(phoneNumber: Long) extends ShopEvent {
  //    override def updateState(persistentId: String, tags: List[String], createShop: String, createTime: DateTime, oldState: ShopState): ShopState =
  //      oldState.copy(phoneNumber = phoneNumber)
  //  }
  //
  //  case class ShopNameUpdated(name: String) extends ShopEvent {
  //    override def updateState(persistentId: String, tags: List[String], createShop: String, createTime: DateTime, oldState: ShopState): ShopState =
  //      oldState.copy(name = name)
  //  }
  //
  //  case class ShopChallengeQuestionUpdated(challengeQuestion: String) extends ShopEvent {
  //    override def updateState(persistentId: String, tags: List[String], createShop: String, createTime: DateTime, oldState: ShopState): ShopState =
  //      oldState.copy(challengeQuestion = challengeQuestion)
  //  }
  //
  //  case class ShopChallengeAnswerUpdated(challengeAnswer: String) extends ShopEvent {
  //    override def updateState(persistentId: String, tags: List[String], createShop: String, createTime: DateTime, oldState: ShopState): ShopState =
  //      oldState.copy(challengeAnswer = challengeAnswer)
  //  }
  //
  //  //address events
  //  case class ShopAddress1Updated(address1: String) extends ShopEvent {
  //    override def updateState(persistentId: String, tags: List[String], createShop: String, createTime: DateTime, oldState: ShopState): ShopState =
  //      oldState.copy(
  //        address = oldState.address.copy(
  //          address1 = Some(address1)
  //        )
  //      )
  //  }
  //
  //  case class ShopAddress2Updated(address2: String) extends ShopEvent {
  //    override def updateState(persistentId: String, tags: List[String], createShop: String, createTime: DateTime, oldState: ShopState): ShopState =
  //      oldState.copy(
  //        address = oldState.address.copy(
  //          address2 = Some(address2)
  //        )
  //      )
  //  }
  //
  //  case class ShopAddress3Updated(address3: String) extends ShopEvent {
  //    override def updateState(persistentId: String, tags: List[String], createShop: String, createTime: DateTime, oldState: ShopState): ShopState =
  //      oldState.copy(
  //        address = oldState.address.copy(
  //          address3 = Some(address3)
  //        )
  //      )
  //  }
  //
  //  case class ShopAddress4Updated(address4: String) extends ShopEvent {
  //    override def updateState(persistentId: String, tags: List[String], createShop: String, createTime: DateTime, oldState: ShopState): ShopState =
  //      oldState.copy(
  //        address = oldState.address.copy(
  //          address4 = Some(address4)
  //        )
  //      )
  //  }
  //
  //  case class ShopCityUpdated(city: String) extends ShopEvent {
  //    override def updateState(persistentId: String, tags: List[String], createShop: String, createTime: DateTime, oldState: ShopState): ShopState =
  //      oldState.copy(
  //        address = oldState.address.copy(
  //          city = Some(city)
  //        )
  //      )
  //  }
  //
  //  case class ShopStateUpdated(state: String) extends ShopEvent {
  //    override def updateState(persistentId: String, tags: List[String], createShop: String, createTime: DateTime, oldState: ShopState): ShopState =
  //      oldState.copy(
  //        address = oldState.address.copy(
  //          state = Some(state)
  //        )
  //      )
  //  }
  //
  //  case class ShopZipUpdated(zip: String) extends ShopEvent {
  //    override def updateState(persistentId: String, tags: List[String], createShop: String, createTime: DateTime, oldState: ShopState): ShopState =
  //      oldState.copy(
  //        address = oldState.address.copy(
  //          zip = Some(zip)
  //        )
  //      )
  //  }
  //
  //  case class ShopCountryUpdated(country: String) extends ShopEvent {
  //    override def updateState(persistentId: String, tags: List[String], createShop: String, createTime: DateTime, oldState: ShopState): ShopState =
  //      oldState.copy(
  //        address = oldState.address.copy(
  //          country = Some(country)
  //        )
  //      )
  //  }
  //
  //  case class ShopLatitudeUpdated(latitude: Double) extends ShopEvent {
  //    override def updateState(persistentId: String, tags: List[String], createShop: String, createTime: DateTime, oldState: ShopState): ShopState =
  //      oldState.copy(
  //        address = oldState.address.copy(
  //          latitude = Some(latitude)
  //        )
  //      )
  //  }
  //
  //  case class ShopLongitudeUpdated(longitude: Double) extends ShopEvent {
  //    override def updateState(persistentId: String, tags: List[String], createShop: String, createTime: DateTime, oldState: ShopState): ShopState =
  //      oldState.copy(
  //        address = oldState.address.copy(
  //          longitude = Some(longitude)
  //        )
  //      )
  //  }

  //Booleans
  case class ShopDeactivated() extends ShopEvent {
    override def updateState(persistentId: String, oldState: ShopState, createShop: String, createTime: DateTime, tags: List[String]): ShopState =
      oldState.copy(active = false)
  }

  case class ShopActivated() extends ShopEvent {
    override def updateState(persistentId: String, oldState: ShopState, createShop: String, createTime: DateTime, tags: List[String]): ShopState =
      oldState.copy(active = true)
  }

  case class ShopDeleted() extends ShopEvent {
    override def updateState(persistentId: String, oldState: ShopState, createShop: String, createTime: DateTime, tags: List[String]): ShopState =
      oldState.copy(deleted = true)
  }

  case class ShopUnDeleted() extends ShopEvent {
    override def updateState(persistentId: String, oldState: ShopState, createShop: String, createTime: DateTime, tags: List[String]): ShopState =
      oldState.copy(deleted = false)
  }


}
