package com.base

import com.commerce.events.utils.EventUtils

/**
  * Every domain object has a Service class. Eg: UserService or ShopService.
  *
  */
trait ServiceBase {

  val system = "system"

  def processAlert[S <: State, E <: Event[S]](alert: Alert[E], state: S): S = {
    EventUtils.recoverStateForEvents(state, alert.toEvents)
  }

  def processCommand[S <: State](command: Finder[S], state: S) =
    command.replyTo ! command.finder(state)

}
