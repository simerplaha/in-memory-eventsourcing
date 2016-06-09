package com.commerce.aggregate.user

import com.base.Event
import com.commerce.enums.EventType
import org.joda.time.DateTime
import scalaz.Scalaz._

sealed trait UserEvent extends Event[UserState] {
  def eventType = EventType.User
}


object UserEvents {

  case class UserCreated(email: String, password: String) extends UserEvent {
    override def updateState(persistentId: String, oldState: UserState, createUser: String, createTime: DateTime, tags: List[String]): UserState =
      oldState.copy(
        email = email,
        password = password,
        pendingCreation = false
      )
  }

  case class UserPasswordUpdated(password: String) extends UserEvent {
    override def updateState(id: String, oldState: UserState, createUser: String, createTime: DateTime, tags: List[String]): UserState =
      oldState.copy(password = password)
  }

  case class UserEmailUpdated(email: String) extends UserEvent {
    override def updateState(id: String, oldState: UserState, createUser: String, createTime: DateTime, tags: List[String]): UserState =
      oldState.copy(email = email)
  }

  case class UserFirstNameUpdated(firstName: String) extends UserEvent {
    override def updateState(id: String, oldState: UserState, createUser: String, createTime: DateTime, tags: List[String]): UserState =
      oldState.copy(firstName = firstName)
  }

  case class UserLastNameUpdated(lastName: String) extends UserEvent {
    override def updateState(id: String, oldState: UserState, createUser: String, createTime: DateTime, tags: List[String]): UserState =
      oldState.copy(lastName = lastName)
  }

  case class UserPhoneNumberUpdated(phoneNumber: String) extends UserEvent {
    override def updateState(id: String, oldState: UserState, createUser: String, createTime: DateTime, tags: List[String]): UserState =
      oldState.copy(phoneNumber = phoneNumber)
  }

  case class UserAddress1Updated(address1: String) extends UserEvent {
    override def updateState(id: String, oldState: UserState, createUser: String, createTime: DateTime, tags: List[String]): UserState =
      oldState.copy(address = oldState.address.copy(address1 = address1.some))
  }

  case class UserAddress2Updated(address2: String) extends UserEvent {
    override def updateState(id: String, oldState: UserState, createUser: String, createTime: DateTime, tags: List[String]): UserState =
      oldState.copy(address = oldState.address.copy(address2 = address2.some))
  }

  case class UserAddress3Updated(address3: String) extends UserEvent {
    override def updateState(id: String, oldState: UserState, createUser: String, createTime: DateTime, tags: List[String]): UserState =
      oldState.copy(address = oldState.address.copy(address3 = address3.some))
  }

  case class UserAddress4Updated(address4: String) extends UserEvent {
    override def updateState(id: String, oldState: UserState, createUser: String, createTime: DateTime, tags: List[String]): UserState =
      oldState.copy(address = oldState.address.copy(address4 = address4.some))
  }

  case class UserCityUpdated(city: String) extends UserEvent {
    override def updateState(id: String, oldState: UserState, createUser: String, createTime: DateTime, tags: List[String]): UserState =
      oldState.copy(address = oldState.address.copy(city = city.some))
  }

  case class UserStateUpdated(state: String) extends UserEvent {
    override def updateState(id: String, oldState: UserState, createUser: String, createTime: DateTime, tags: List[String]): UserState =
      oldState.copy(address = oldState.address.copy(state = state.some))
  }

  case class UserZipUpdated(zip: String) extends UserEvent {
    override def updateState(id: String, oldState: UserState, createUser: String, createTime: DateTime, tags: List[String]): UserState =
      oldState.copy(address = oldState.address.copy(zip = zip.some))
  }

  case class UserCountryUpdated(country: String) extends UserEvent {
    override def updateState(id: String, oldState: UserState, createUser: String, createTime: DateTime, tags: List[String]): UserState =
      oldState.copy(address = oldState.address.copy(country = country.some))
  }

  case class UserLatitudeUpdated(latitude: Double) extends UserEvent {
    override def updateState(id: String, oldState: UserState, createUser: String, createTime: DateTime, tags: List[String]): UserState =
      oldState.copy(address = oldState.address.copy(latitude = latitude.some))
  }

  case class UserLongitudeUpdated(longitude: Double) extends UserEvent {
    override def updateState(id: String, oldState: UserState, createUser: String, createTime: DateTime, tags: List[String]): UserState =
      oldState.copy(address = oldState.address.copy(longitude = longitude.some))
  }


  case class UserChallengeQuestionUpdated(challengeQuestion: String) extends UserEvent {
    override def updateState(id: String, oldState: UserState, createUser: String, createTime: DateTime, tags: List[String]): UserState =
      oldState.copy(challengeQuestion = challengeQuestion)
  }

  case class UserChallengeAnswerUpdated(challengeAnswer: String) extends UserEvent {
    override def updateState(id: String, oldState: UserState, createUser: String, createTime: DateTime, tags: List[String]): UserState =
      oldState.copy(challengeAnswer = challengeAnswer)
  }

  //Booleans
  case class UserEmailConfirmed() extends UserEvent {
    override def updateState(id: String, oldState: UserState, createUser: String, createTime: DateTime, tags: List[String]): UserState =
      oldState.copy(emailConfirmed = true)
  }

  case class UserDeactivated() extends UserEvent {
    override def updateState(persistentId: String, oldState: UserState, createUser: String, createTime: DateTime, tags: List[String]): UserState =
      oldState.copy(active = false)
  }

  case class UserActivated() extends UserEvent {
    override def updateState(persistentId: String, oldState: UserState, createUser: String, createTime: DateTime, tags: List[String]): UserState =
      oldState.copy(active = true)
  }

  case class UserLoggedIn() extends UserEvent {
    override def updateState(persistentId: String, oldState: UserState, createUser: String, createTime: DateTime, tags: List[String]): UserState =
      oldState.copy(loggedIn = true)
  }

  case class UserLoggedOut() extends UserEvent {
    override def updateState(persistentId: String, oldState: UserState, createUser: String, createTime: DateTime, tags: List[String]): UserState =
      oldState.copy(loggedIn = false)
  }

  case class UserDeleted() extends UserEvent {
    override def updateState(persistentId: String, oldState: UserState, createUser: String, createTime: DateTime, tags: List[String]): UserState =
      oldState.copy(deleted = true)
  }

  case class UserUnDeleted() extends UserEvent {
    override def updateState(persistentId: String, oldState: UserState, createUser: String, createTime: DateTime, tags: List[String]): UserState =
      oldState.copy(deleted = false)
  }


}
