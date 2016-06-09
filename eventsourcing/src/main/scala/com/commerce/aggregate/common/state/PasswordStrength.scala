package com.commerce.aggregate.common.state

import com.commerce.utils.RegexUtils

trait PasswordStrength {
  val minLength: Int
  val maxLength: Int
  val minAlphabets: Int
  val minDigits: Int
  val minSymbols: Int
  val minUppercase: Int

  def isValid(password: String): Boolean =
    if (password.length < minLength
      || password.length > maxLength
      || password.count(_.isLetter) < minAlphabets
      || password.count(_.isDigit) < minDigits
      || password.count(_.isUpper) < minUppercase
      || RegexUtils.specialCharactersRegex.findAllMatchIn(password).size < minSymbols
    )
      false
    else
      true
}

case class EasyPasswordStrength(minLength: Int = 6,
                                maxLength: Int = 500,
                                minAlphabets: Int = 0,
                                minDigits: Int = 0,
                                minSymbols: Int = 0,
                                minUppercase: Int = 0) extends PasswordStrength

case class MediumPasswordStrength(minLength: Int = 6,
                                  maxLength: Int = 500,
                                  minAlphabets: Int = 1,
                                  minDigits: Int = 1,
                                  minSymbols: Int = 0,
                                  minUppercase: Int = 1) extends PasswordStrength

case class HardPasswordStrength(minLength: Int = 8,
                                maxLength: Int = 500,
                                minAlphabets: Int = 1,
                                minDigits: Int = 1,
                                minSymbols: Int = 1,
                                minUppercase: Int = 1) extends PasswordStrength

case class CustomPasswordStrength(minLength: Int,
                                  maxLength: Int = 500,
                                  minAlphabets: Int,
                                  minDigits: Int,
                                  minSymbols: Int,
                                  minUppercase: Int) extends PasswordStrength
