package com.commerce.enums

object CustomerError {

  def `Customer account for email already exists`(email: String) =
    s"Customer account for email $email already exists."

  def `Customer account has reached maximum API quota. Please upgrade`() =
    s"Customer account has reached maximum API quota. Please upgrade!"

  def `Invalid email address`(email: String) =
    s"Invalid email address $email"

  def `Password should contain minimum 8 characters` =
    s"Password should contain minimum 8 characters."

  def `Email is already attached with another account. Try deleting that account first`(email: String) =
    s"Email '$email' is already attached with another account. Try deleting that account first."

  def `invalid first name length`(min: Int, max: Int) =
    s"Required minimum $min characters and maximum $max characters for your first name."

  def `invalid last name length`(min: Int, max: Int) =
    s"Required minimum $min characters and maximum $max characters for your last name."

  def `invalid password length`(min: Int, max: Int) =
    s"Required minimum $min characters and maximum $max characters for your password."


}
