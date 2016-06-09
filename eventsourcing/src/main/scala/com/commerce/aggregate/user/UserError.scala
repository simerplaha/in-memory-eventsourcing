package com.commerce.aggregate.user

import com.commerce.enums.ErrorType
import com.macroz.json.mapper.{SealedTraitChildrenExistenceChecker}


sealed trait UserError extends ErrorType

//Email
case class EmailIsTakenError(email: String) extends UserError

case object EmailIsRequiredError extends UserError

case class EmailIsInvalidError(email: String) extends UserError

case class NoChangeError(key: String, value: String) extends UserError

case object EmailCannotBeEmpty extends UserError

//Password
case object PasswordIsRequiredError extends UserError

case object PasswordIsInvalidError extends UserError

case class UserFirstNameHasInvalidCharacters(firstName: String, min: Long, max: Long) extends UserError

case class UserLastNameHasInvalidCharacters(firstName: String, min: Long, max: Long) extends UserError

case class UserChallengeQuestionHasInvalidCharacters(firstName: String, min: Long, max: Long) extends UserError

case class UserChallengeAnswerHasInvalidCharacters(firstName: String, min: Long, max: Long) extends UserError

case class UserPhoneNumberHasInvalidCharacters(firstName: String, min: Long, max: Long) extends UserError

//User root
case class ErrorUserDoesNotExists(userId: String) extends UserError

case class ErrorUserCreationIsInProgress(email: String) extends UserError

case class ErrorUserAlreadyExists(email: String) extends UserError


object UserError {

  val userErrorIterator = SealedTraitChildrenExistenceChecker.childExists[UserError]()

  def exists(name: String): Boolean =
    userErrorIterator(name)
}