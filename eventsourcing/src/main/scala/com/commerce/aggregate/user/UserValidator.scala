package com.commerce.aggregate.user

import akka.typed.ScalaDSL._
import akka.typed.{ActorRef, Props, _}
import com.base.ErrorInternal
import com.commerce.aggregate.user.UserCommands._
import com.commerce.aggregate.user.UserEvents._
import com.commerce.enums.Language.Language

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaz.Scalaz._
import scalaz._


object UserValidator {

  def run(command: CreateUser, state: UserState, ctx: ActorContext[UserCommand]): Unit = {
    Future {
      val replyTo = ctx.self
      val errorsOrErrors: Validation[NonEmptyList[ErrorInternal], NonEmptyList[UserEvent]] = validate(command, state)
      errorsOrErrors match {
        case Success(events) =>
          val email = command.email.get
          val emailAvailabilityCheckerActor = ctx.spawnAnonymous(Props(emailAvailabilityCheck(email, command.language, state, replyTo, errorsOrErrors)))
          ctx.spawnAnonymous(Props(UserSearch.findUserWithEmail(email, state.parent, emailAvailabilityCheckerActor)))
        case Failure(errors) =>
          replyTo ! ValidationFailed(replyTo.path.self.name, "system", errors)
      }
    }
  }


  private def emailAvailabilityCheck(email: String, language: Language, state: UserState, replyTo: ActorRef[UserCommand], errorsOrErrors: Validation[NonEmptyList[ErrorInternal], NonEmptyList[UserEvent]]): Behavior[Option[UserState]] = {
    ContextAware[Option[UserState]] {
      ctx =>
        val replyToId = replyTo.path.self.name
        Total[Option[UserState]] {
          case Some(_) =>
            val emailIsTakenError = ErrorInternal(EmailIsTakenError(email), language)
            errorsOrErrors match {
              case Success(events) =>
                replyTo ! ValidationFailed(replyToId, "system", NonEmptyList(emailIsTakenError))
              case Failure(errors) =>
                replyTo ! ValidationFailed(replyToId, "system", errors.<::(emailIsTakenError))
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


  private def validate(command: CreateUser, oldState: UserState): Validation[NonEmptyList[ErrorInternal], NonEmptyList[UserEvent]] =
    (checkEmail(command, oldState).toValidationNel
      |@| checkPassword(command, oldState).toValidationNel
      |@| checkFirstName(command, oldState).toValidationNel
      |@| checkLastName(command, oldState).toValidationNel
      |@| checkChallengeQuestion(command, oldState).toValidationNel
      |@| checkChallengeAnswer(command, oldState).toValidationNel
      |@| checkPhoneNumber(command, oldState).toValidationNel) {
      createUser
    }

  private def createUser = {
    (emailEvent: UserEmailUpdated,
     passwordEvent: UserPasswordUpdated,
     firstNameEvent: Option[UserFirstNameUpdated],
     lastNameEvent: Option[UserLastNameUpdated],
     questionEvent: Option[UserChallengeQuestionUpdated],
     answerEvent: Option[UserChallengeAnswerUpdated],
     phoneNumberEvent: Option[UserPhoneNumberUpdated]) =>

      val eventListBuffer = ListBuffer.empty[UserEvent]
      if (firstNameEvent.isDefined) eventListBuffer += firstNameEvent.get
      if (lastNameEvent.isDefined) eventListBuffer += lastNameEvent.get
      if (questionEvent.isDefined) eventListBuffer += questionEvent.get
      if (answerEvent.isDefined) eventListBuffer += answerEvent.get
      if (phoneNumberEvent.isDefined) eventListBuffer += phoneNumberEvent.get

      val userCreatedEvent = UserCreated(emailEvent.email, passwordEvent.password)

      NonEmptyList(userCreatedEvent, emailEvent, passwordEvent).<:::(eventListBuffer.toList)
  }


  //  private def createUser = {
  //    (emailEvent, passwordEvent, firstNameOpt, lastNameOpt, challengeQuestionOpt, challengeAnswerOpt, phoneNumberOpt) =>
  //      val eventListBuffer = ListBuffer.empty[UserEvent]
  //      eventListBuffer += UserCreated(emailEvent.email, passwordEvent.password)
  //      eventListBuffer += emailEvent
  //      eventListBuffer += passwordEvent
  //      if (firstNameOpt.isDefined) eventListBuffer += firstNameOpt.get
  //
  //      NonEmptyList(
  //        UserCreated(emailEvent.email, passwordEvent.password),
  //        emailEvent,
  //        passwordEvent
  //      )
  //  }


  private def checkEmail(command: CreateUser, state: UserState): Validation[ErrorInternal, UserEmailUpdated] = {
    val userSetting = state.setting
    command.email match {
      case Some(email) =>
        email match {
          case email if email.trim.isEmpty =>
            val error = ErrorInternal(EmailCannotBeEmpty, command.language)
            error.failure[UserEmailUpdated]
          case email if userSetting.emailValidatorRegex.findFirstMatchIn(email).isEmpty =>
            val error = ErrorInternal(EmailIsInvalidError(email), command.language)
            error.failure[UserEmailUpdated]
          case email if state.email == email =>
            val error = ErrorInternal(NoChangeError("email", email), command.language)
            error.failure[UserEmailUpdated]
          case _ =>
            UserEmailUpdated(email).success
        }
      case None =>
        val error = ErrorInternal(EmailIsRequiredError, command.language)
        error.failure[UserEmailUpdated]
    }
  }


  private def checkPassword(command: CreateUser, state: UserState): Validation[ErrorInternal, UserPasswordUpdated] = {
    val userSetting = state.setting
    command.password match {
      case Some(password) =>
        password match {
          case password if password.trim.isEmpty =>
            val error = ErrorInternal(PasswordIsRequiredError, command.language)
            error.failure[UserPasswordUpdated]
          case password if !userSetting.passwordStrength.isValid(password) =>
            val error = ErrorInternal(PasswordIsInvalidError, command.language)
            error.failure[UserPasswordUpdated]
          case password if state.password == password =>
            val error = ErrorInternal(NoChangeError("password", password), command.language)
            error.failure[UserPasswordUpdated]
          case _ =>
            UserPasswordUpdated(password).success
        }
      case None =>
        val error = ErrorInternal(PasswordIsRequiredError, command.language)
        error.failure[UserPasswordUpdated]
    }
  }


  private def checkFirstName(command: CreateUser, state: UserState): Validation[ErrorInternal, Option[UserFirstNameUpdated]] = {
    val userSetting = state.setting
    command.firstName match {
      case Some(firstName) =>
        firstName match {
          case firstName if firstName.trim.isEmpty =>
            None.success
          case firstName if firstName.length > userSetting.maxFirstNameCharacters || firstName.length < userSetting.minLastNameCharacters =>
            val errorObject = UserFirstNameHasInvalidCharacters(firstName, userSetting.minFirstNameCharacters, userSetting.maxFirstNameCharacters)
            val error = ErrorInternal(errorObject, command.language)
            error.failure[Option[UserFirstNameUpdated]]
          case firstName if state.firstName == firstName =>
            val error = ErrorInternal(NoChangeError("firstName", firstName), command.language)
            error.failure[Option[UserFirstNameUpdated]]
          case _ =>
            UserFirstNameUpdated(firstName).some.success
        }
      case None =>
        None.success
    }
  }

  private def checkLastName(command: CreateUser, state: UserState): Validation[ErrorInternal, Option[UserLastNameUpdated]] = {
    val userSetting = state.setting
    command.lastName match {
      case Some(lastName) =>
        lastName match {
          case lastName if lastName.trim.isEmpty =>
            None.success
          case lastName if lastName.length > userSetting.maxLastNameCharacters || lastName.length < userSetting.minLastNameCharacters =>
            val errorObject = UserLastNameHasInvalidCharacters(lastName, userSetting.minLastNameCharacters, userSetting.maxLastNameCharacters)
            val error = ErrorInternal(errorObject, command.language)
            error.failure[Option[UserLastNameUpdated]]
          case lastName if state.lastName == lastName =>
            val error = ErrorInternal(NoChangeError("lastName", lastName), command.language)
            error.failure[Option[UserLastNameUpdated]]
          case _ =>
            UserLastNameUpdated(lastName).some.success
        }
      case None =>
        None.success
    }
  }

  private def checkChallengeQuestion(command: CreateUser, state: UserState): Validation[ErrorInternal, Option[UserChallengeQuestionUpdated]] = {
    val userSetting = state.setting
    command.challengeQuestion match {
      case Some(challengeQuestion) =>
        challengeQuestion match {
          case challengeQuestion if challengeQuestion.trim.isEmpty =>
            None.success
          case challengeQuestion if challengeQuestion.length > userSetting.maxChallengeQuestionCharacters || challengeQuestion.length < userSetting.minChallengeQuestionCharacters =>
            val errorObject = UserChallengeQuestionHasInvalidCharacters(challengeQuestion, userSetting.minChallengeQuestionCharacters, userSetting.maxChallengeQuestionCharacters)
            val error = ErrorInternal(errorObject, command.language)
            error.failure[Option[UserChallengeQuestionUpdated]]
          case challengeQuestion if state.challengeQuestion == challengeQuestion =>
            val error = ErrorInternal(NoChangeError("challengeQuestion", challengeQuestion), command.language)
            error.failure[Option[UserChallengeQuestionUpdated]]
          case _ =>
            UserChallengeQuestionUpdated(challengeQuestion).some.success
        }
      case None =>
        None.success
    }
  }

  private def checkChallengeAnswer(command: CreateUser, state: UserState): Validation[ErrorInternal, Option[UserChallengeAnswerUpdated]] = {
    val userSetting = state.setting
    command.challengeAnswer match {
      case Some(challengeAnswer) =>
        challengeAnswer match {
          case challengeAnswer if challengeAnswer.trim.isEmpty =>
            None.success
          case challengeAnswer if challengeAnswer.length > userSetting.maxChallengeAnswerCharacters || challengeAnswer.length < userSetting.minChallengeAnswerCharacters =>
            val errorObject = UserChallengeAnswerHasInvalidCharacters(challengeAnswer, userSetting.minChallengeAnswerCharacters, userSetting.maxChallengeAnswerCharacters)
            val error = ErrorInternal(errorObject, command.language)
            error.failure[Option[UserChallengeAnswerUpdated]]
          case challengeAnswer if state.challengeAnswer == challengeAnswer =>
            val error = ErrorInternal(NoChangeError("challengeAnswer", challengeAnswer), command.language)
            error.failure[Option[UserChallengeAnswerUpdated]]
          case _ =>
            UserChallengeAnswerUpdated(challengeAnswer).some.success
        }
      case None =>
        None.success
    }
  }

  private def checkPhoneNumber(command: CreateUser, state: UserState): Validation[ErrorInternal, Option[UserPhoneNumberUpdated]] = {
    val userSetting = state.setting
    command.phoneNumber match {
      case Some(phoneNumber) =>
        phoneNumber match {
          case phoneNumber if phoneNumber.trim.isEmpty =>
            None.success
          case phoneNumber if phoneNumber.length > userSetting.maxPhoneNumberCharacters || phoneNumber.length < userSetting.minPhoneNumberCharacters =>
            val errorObject = UserPhoneNumberHasInvalidCharacters(phoneNumber, userSetting.minPhoneNumberCharacters, userSetting.maxPhoneNumberCharacters)
            val error = ErrorInternal(errorObject, command.language)
            error.failure[Option[UserPhoneNumberUpdated]]
          case phoneNumber if state.phoneNumber == phoneNumber =>
            val error = ErrorInternal(NoChangeError("phoneNumber", phoneNumber), command.language)
            error.failure[Option[UserPhoneNumberUpdated]]
          case _ =>
            UserPhoneNumberUpdated(phoneNumber).some.success
        }
      case None =>
        None.success
    }
  }

  //  private def checkAddress1(address1: String, language: Language, state: UserState): Validation[ErrorDBO, Option[UserAddress1Updated]] = {
  //    val userSetting = state.setting
  //    address1 match {
  //      case address1 if address1.trim.isEmpty =>
  //        None.success
  //      case address if address.length > userSetting.maxPhoneNumberCharacters || address.length < userSetting.minPhoneNumberCharacters =>
  //        val errorObject = UserPhoneNumberHasInvalidCharacters(address, userSetting.minPhoneNumberCharacters, userSetting.maxPhoneNumberCharacters)
  //        val error = ErrorDBO(errorObject, command.language)
  //        error.failure[Option[UserPhoneNumberUpdated]]
  //      case address1 if state.address1 == address1 =>
  //        val error = ErrorDBO(NoChangeError("address", address1), command.language)
  //        error.failure[Option[UserPhoneNumberUpdated]]
  //      case _ =>
  //        UserPhoneNumberUpdated(address).some.success
  //    }
  //  }
}
