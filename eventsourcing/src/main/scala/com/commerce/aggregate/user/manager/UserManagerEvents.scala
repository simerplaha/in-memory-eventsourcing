package com.commerce.aggregate.user.manager

import com.base.Event
import com.commerce.aggregate.user.UserCommands.CreateUser
import com.commerce.enums.{AggregateIdPrefix, EventType}
import org.joda.time.DateTime

sealed trait UserManagerEvent extends Event[UserManagerState] {
  def eventType = EventType.UserManager
}

object UserManagerEvents {

  case class UserCreatedUM(id: String, email: String) extends UserManagerEvent {
    override def updateState(persistentId: String, state: UserManagerState, createUser: String, createTime: DateTime, tags: List[String]): UserManagerState = {
      var nextUserId = state.nextId
      val thisEventsUserId = id.substring(id.lastIndexOf(AggregateIdPrefix.user) + 1, id.length).toLong
      if (nextUserId <= thisEventsUserId) {
        nextUserId = thisEventsUserId + 1
      }
      state.copy(
        userIds = state.userIds.+:(id),
        nextId = nextUserId
      )
    }
  }

  case class UserDeletedUM(id: String) extends UserManagerEvent {
    override def updateState(persistentId: String, state: UserManagerState, createUser: String, createTime: DateTime, tags: List[String]): UserManagerState =
      state.copy(
        userIds = state.userIds.filterNot(_ == id)
      )
  }

  case class CreateUserCommandSuccessful(createUserCommand: CreateUser) extends UserManagerEvent {
    override def updateState(id: String, state: UserManagerState, createUser: String, createTime: DateTime, tags: List[String]): UserManagerState = {
      var nextId = state.nextId
      if(createUserCommand.userIdWithoutPrefix >= state.nextId) {
        nextId = createUserCommand.userIdWithoutPrefix + 1
      }
      state.copy(
        userIds = state.userIds.+:(createUserCommand.destinationId),
        inProgressCreateUserCommands = state.inProgressCreateUserCommands.filterNot(_ == createUserCommand),
        nextId = nextId
      )
    }
  }

}



