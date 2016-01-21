package dsl

import ch.epfl.yinyang._
import ch.epfl.yinyang.typetransformers._
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

package object la {

  def la[T](block: => T): T = macro implementations.liftRep[T]
  def laDebug[T](block: => T): T = macro implementations.liftDebug[T]
  def lms[T](block: => T): T = macro implementations.boolLMS[T]
  def lmsDebug[T](block: => T): T = macro implementations.boolLMSDebug[T]
  def deliteTestDsl[T](block: => T): T = macro implementations.deliteTestBaseDSL[T]

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

    def liftDebug[T](c: Context)(block: c.Expr[T]): c.Expr[T] =
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
          "debug" -> 3,
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
          "debug" -> 0,
          //          "restrictLanguage" -> false,
          "ascriptionTransforming" -> false))(block)
    }

    def boolLMSDebug[T](c: Context)(block: c.Expr[T]): c.Expr[T] = {
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
          "debug" -> 3,
          //          "restrictLanguage" -> false,
          "ascriptionTransforming" -> false))(block)
    }

    def deliteTestBaseDSL[T](c: Context)(block: c.Expr[T]): c.Expr[T] = {
      YYTransformer[c.type, T](c)(
        "ppl.delite.framework.ops.DeliteOpsExp", //don't use version with Reify
        new GenericTypeTransformer[c.type](c) {
          override val IRType = "Rep"
        },
        None, None,
        Map(
          "shallow" -> false,
          "virtualizeFunctions" -> false,
          "virtualizeVal" -> false,
          "debug" -> 4,
          //          "restrictLanguage" -> false,
          "ascriptionTransforming" -> false))(block)
    }
  }
}