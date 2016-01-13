package dsl

import ch.epfl.yinyang._
import ch.epfl.yinyang.typetransformers._
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

package object la {

  def la[T](block: => T): T = macro implementations.liftRep[T]
  def boolDLMS[T](block: => T): T = macro implementations.boolLMS[T]

  object implementations {
    def liftRep[T](c: Context)(block: c.Expr[T]): c.Expr[T] =
      YYTransformer[c.type, T](c)(
        "dsl.la.rep.VectorDSL",
        new GenericTypeTransformer[c.type](c) {
          override val IRType = "R"
        },
        None, None,
        Map(
          "shallow" -> false,
          "virtualizeFunctions" -> true,
          "virtualizeVal" -> true,
          "debug" -> 0,
          "featureAnalysing" -> false,
          "ascriptionTransforming" -> false))(block)

    def boolLMS[T](c: Context)(block: c.Expr[T]): c.Expr[T] = {
      YYTransformer[c.type, T](c)(
        "dsl.la.rep.BooleanLMS", //don't use version with Reify
        new GenericTypeTransformer[c.type](c) {
          override val IRType = "Rep"
        },
        None, None,
        Map(
          "shallow" -> false,
          "virtualizeFunctions" -> false,
          "virtualizeVal" -> false,
          "debug" -> 100,
          //          "restrictLanguage" -> false,
          "ascriptionTransforming" -> false))(block)
    }
  }
}