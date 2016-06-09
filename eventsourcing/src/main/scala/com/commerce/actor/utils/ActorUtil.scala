package com.commerce.actor.utils

import akka.typed.{ActorContext, ActorRef, Props}
import akka.util.Timeout
import com.main.config.ConfigProps

import scala.concurrent.duration._

object ActorUtil {

  val queryTimeout = ConfigProps.queryTimeout

  val timeoutDuration = queryTimeout.seconds

  implicit val timeout = Timeout(timeoutDuration)

  def findOrCreate[T](context: ActorContext[_],
                      props: Props[T],
                      id: String,
                      initialCommand: Option[T]): ActorRef[T] = {
    val childMayBe = context.child(id)
    val newChild =
      if (childMayBe.isDefined)
        childMayBe.get.asInstanceOf[ActorRef[T]]
      else {
        val newlyCreatedChild = context.spawn(props, id)
        context.watch(newlyCreatedChild)
        if (initialCommand.isDefined) newlyCreatedChild ! initialCommand.get
        newlyCreatedChild
      }
    newChild
  }
}
