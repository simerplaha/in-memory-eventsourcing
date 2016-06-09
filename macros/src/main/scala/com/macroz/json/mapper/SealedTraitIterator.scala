package com.macroz.json.mapper

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

object SealedTraitIterator {

  def values[A]: Set[A] = macro values_impl[A]

  def values_impl[A: c.WeakTypeTag](c: Context): c.Expr[Set[A]] = {
    import c.universe._

    val symbol = weakTypeOf[A].typeSymbol

    if (!symbol.isClass)
      c.abort(
        c.enclosingPosition,
        "Can only enumerate values of a sealed trait or class."
      )
    else if (!symbol.asClass.isSealed)
      c.abort(
        c.enclosingPosition,
        "Can only enumerate values of a sealed trait or class."
      )
    else {
      val children = symbol.asClass.knownDirectSubclasses.toList

      if (!children.forall(_.isModuleClass))
        c.abort(
          c.enclosingPosition,
          "All children must be objects."
        )
      else c.Expr[Set[A]] {
        def sourceModuleRef(sym: Symbol) =
          Ident(
            sym.asInstanceOf[scala.reflect.internal.Symbols#Symbol].sourceModule.asInstanceOf[Symbol]
          )

        Apply(
          Select(
            reify(Set).tree,
            TermName("apply")
          ),
          children.map(sourceModuleRef(_))
        )
      }
    }
  }


}
