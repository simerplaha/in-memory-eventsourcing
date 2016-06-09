package com.commerce.aggregate.user.manager

import com.base.State
import com.commerce.aggregate.common.state.{EasyPasswordStrength, PasswordStrength}
import com.commerce.aggregate.user.UserCommands.CreateUser
import com.commerce.enums.AggregateIdPrefix
import com.commerce.utils.RegexUtils

import scala.util.matching.Regex


case class UserManagerState(userIds: List[String],
                            nextId: Long,
                            inProgressCreateUserCommands: List[CreateUser],
                            setting: UserSetting) extends State {
  def nextIdWithPrefix = AggregateIdPrefix.user + nextId
}

case class UserSetting(maxFirstNameCharacters: Int = 100,
                       minFirstNameCharacters: Int = 0,
                       maxLastNameCharacters: Int = 100,
                       minLastNameCharacters: Int = 0,
                       maxChallengeQuestionCharacters: Int = 100,
                       minChallengeQuestionCharacters: Int = 0,
                       maxChallengeAnswerCharacters: Int = 100,
                       minChallengeAnswerCharacters: Int = 0,
                       maxPhoneNumberCharacters: Int = 100,
                       minPhoneNumberCharacters: Int = 0,
                       maxAddress1Characters: Int = 100,
                       minAddress1Characters: Int = 0,
                       maxAddress2Characters: Int = 100,
                       minAddress2Characters: Int = 0,
                       passwordStrength: PasswordStrength = EasyPasswordStrength(),
                       emailValidatorRegex: Regex = RegexUtils.emailRegex)

