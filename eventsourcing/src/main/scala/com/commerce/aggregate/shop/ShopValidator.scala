package com.commerce.aggregate.shop

import akka.typed.ScalaDSL._
import akka.typed.{ActorRef, Props, _}
import com.base.ErrorInternal
import com.commerce.aggregate.shop.ShopCommands._
import com.commerce.aggregate.shop.ShopEvents.{ShopCreated, ShopIntroUpdated, ShopNameUpdated}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaz.Scalaz._
import scalaz._


object ShopValidator {

  def run(command: CreateShop, state: ShopState, ctx: ActorContext[ShopCommand]): Unit = {
    Future {
      val replyTo = ctx.self
      val errorsOrErrors: Validation[NonEmptyList[ErrorInternal], NonEmptyList[ShopEvent]] = validate(command, state, ctx)
      errorsOrErrors match {
        case Success(events) =>
          val nameAvailabilityCheckerActor = ctx.spawnAnonymous(Props(nameAvailabilityCheck(command, state, replyTo, errorsOrErrors)))
          ctx.spawnAnonymous(Props(ShopSearch.findShopWithName(command.name, state.parent, nameAvailabilityCheckerActor)))
        case Failure(errors) =>
          replyTo ! ValidationFailed(replyTo.path.self.name, "system", errors)
      }
    }
  }


  private def nameAvailabilityCheck(createShopCommand: CreateShop, state: ShopState, replyTo: ActorRef[ShopCommand], errorsOrErrors: Validation[NonEmptyList[ErrorInternal], NonEmptyList[ShopEvent]]): Behavior[Option[ShopState]] = {
    ContextAware[Option[ShopState]] {
      ctx =>
        val replyToId = replyTo.path.self.name
        Total[Option[ShopState]] {
          case Some(_) =>
            val nameIsTakenError = ErrorInternal(NameIsTakenError(createShopCommand.name), createShopCommand.language)
            errorsOrErrors match {
              case Success(events) =>
                replyTo ! ValidationFailed(replyToId, "system", NonEmptyList(nameIsTakenError))
              case Failure(errors) =>
                replyTo ! ValidationFailed(replyToId, "system", errors.<::(nameIsTakenError))
            }
            Stopped
          case _ =>
            errorsOrErrors match {
              case Success(events) =>
                replyTo ! ValidationSuccessful(replyToId, "system", events)
              case Failure(errors) =>
                replyTo ! ValidationFailed(replyToId, "system", errors)
            }
            Stopped

        }
    }
  }

  /**
    * BASIC VALIDATION HERE
    */

  private def validate(command: CreateShop, oldState: ShopState, ctx: ActorContext[_]): Validation[NonEmptyList[ErrorInternal], NonEmptyList[ShopEvent]] = {
    (checkName(command, oldState, ctx).toValidationNel
      |@| checkIntro(command, oldState).toValidationNel) (createShop)
  }

  private def createShop: (ShopNameUpdated, ShopIntroUpdated) => NonEmptyList[ShopEvent] = {
    (nameUpdated, introUpdated) =>
      NonEmptyList(
        ShopCreated(nameUpdated.name),
        nameUpdated,
        introUpdated
      )
  }

  private def checkName(command: CreateShop, state: ShopState, ctx: ActorContext[_]): Validation[ErrorInternal, ShopNameUpdated] = {
    val shopSetting = state.setting
    command.name match {
      case null =>
        val error = ErrorInternal(NameIsRequiredError, command.language)
        error.failure[ShopNameUpdated]
      case name if name.trim.isEmpty =>
        val error = ErrorInternal(NameIsRequiredError, command.language)
        error.failure[ShopNameUpdated]
      case _ =>
        ShopNameUpdated(command.name).success
    }
  }

  private def checkIntro(command: CreateShop, state: ShopState): Validation[ErrorInternal, ShopIntroUpdated] = {
    val shopSetting = state.setting
    command.intro match {
      case null =>
        val error = ErrorInternal(IntroIsRequiredError, command.language)
        error.failure[ShopIntroUpdated]
      case intro if intro.trim.isEmpty =>
        val error = ErrorInternal(IntroIsRequiredError, command.language)
        error.failure[ShopIntroUpdated]
      case _ =>
        ShopIntroUpdated(command.intro).success
    }
  }
}
