package com.commerce.aggregate.user

import akka.typed.ActorRef
import com.base.StepWise
import com.commerce.aggregate.user.UserCommands.{UserAggregatesData, UserFinder}
import com.commerce.aggregate.user.manager.{GetCreatedUsers, UserManager, UserManagerCommand}

import scala.concurrent.duration._
import scalaz.Scalaz._

object UserSearch {

  val system = "system"


  def findUserWithEmail(email: String, userManager: ActorRef[UserManagerCommand], replyTo: ActorRef[Option[UserState]]) = {
    StepWise[Any] {
      (ctx, startWith) ⇒
        val self = ctx.self
        startWith.withKeepTraces(true) {
//          val domain: ActorRef[UserManagerCommand] = ctx.spawn(UserManager.props, "UM")
          userManager ! GetCreatedUsers(userManager.path.name, system, "")(self)
          (userManager, 1)
        }.expectMultipleMessages(1 seconds) {
          case (users: List[UserAggregatesData], (domain, previousCount)) ⇒

            users.head.aggregates foreach {
              user =>
                user ! UserFinder(user.path.name, isUserWithEmail(email), system)(self)
            }
            if (users.head.aggregates.isEmpty) {
              replyTo ! None
            }
            (domain, users.head.aggregates.size)
        }.expectMultipleMessages(1 seconds) {
          case (users: List[Option[UserState]], (domain, previousCount)) ⇒
            val states = users.collect {
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

  private def isUserWithEmail(email: String)(state: UserState) =
    if (state.email == email) state.some else None
}
