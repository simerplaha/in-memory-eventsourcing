package com.macroz.json.mapper

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

/**
  * This guy generates a function which given an EventName and the Payload(Json string) will return and initialized
  * object.
  *
  * This is helpful to convert the saved Events in the database to Case Classes.
  */

object EventMapperMacro {

  def generatePatternMatcher[A](): (String, String) => Option[A] = macro generatePatternMatcherImpl[A]

  def generatePatternMatcherImpl[A: c.WeakTypeTag](c: Context)(): c.Tree = {
    import c.universe._
    val subclasses: Set[c.universe.Symbol] = c.weakTypeOf[A].typeSymbol.asClass.knownDirectSubclasses

    val cases = subclasses.map {
      subClass =>
        val simpleName = subClass.fullName.substring(subClass.fullName.lastIndexOf(".") + 1, subClass.fullName.length)
        cq"""$simpleName => jsonString.decodeOption[${subClass.asType}]"""
    }

    q"""(eventName: String, jsonString: String) =>
        eventName match {
          case ..$cases
        }
      """
  }


}
