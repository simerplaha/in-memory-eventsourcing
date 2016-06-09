package com.macroz.json.mapper

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

/**
  * Used as a validator to check if a case class does exists.
  */

object SealedTraitChildrenExistenceChecker {

  def childExists[A](): (String) => Boolean = macro childExistsImpl[A]

  def childExistsImpl[A: c.WeakTypeTag](c: Context)(): c.Tree = {
    import c.universe._
    val subclasses: Set[c.universe.Symbol] = c.weakTypeOf[A].typeSymbol.asClass.knownDirectSubclasses

    val cases = subclasses.map {
      subClass =>
        val simpleName = subClass.fullName.substring(subClass.fullName.lastIndexOf(".") + 1, subClass.fullName.length)
        cq"""$simpleName => true"""
    }

    q"""(name: String) =>
        name match {
          case ..$cases
          case _ => false
        }
      """
  }


}
