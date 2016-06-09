package com.commerce.utils

import scala.util.matching.Regex

object RegexUtils {

  val emailRegex: Regex = """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r

  val specialCharactersRegex: Regex = "[^A-Za-z0-9]".r

}
