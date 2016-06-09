package com.commerce.aggregate.user

import akka.typed.ActorRef
import com.base.State
import com.commerce.aggregate.common.state.Address
import com.commerce.aggregate.user.UserCommands.CreateUser
import com.commerce.aggregate.user.manager.{UserManagerCommand, UserSetting}

sealed trait UserStates extends State

case class UserState(email: String,
                     password: String,
                     parent: ActorRef[UserManagerCommand],
                     setting: UserSetting,
                     emailConfirmed: Boolean,
                     active: Boolean,
                     loggedIn: Boolean,
                     deleted: Boolean,
                     pendingCreation: Boolean,
                     createUserCommand: Option[CreateUser],
                     firstName: String,
                     lastName: String,
                     address: Address,
                     challengeQuestion: String,
                     challengeAnswer: String,
                     phoneNumber: String) extends UserStates {
}
