package dsl

import ch.epfl.yinyang._
import ch.epfl.yinyang.typetransformers._
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

package object lms {
  def lms[T](block: => T): T = macro implementations.boolLMS[T]
  def lmsDebug[T](block: => T): T = macro implementations.boolLMSDebug[T]
  def primitiveDsl[T](block: => T): T = macro implementations.primitiveDSL[T]
  def vectorDsl[T](block: => T): T = macro implementations.vectorDSL[T]
  //  def deliteTestDsl[T](block: => T): T = macro implementations.deliteTestBaseDSL[T]

  object implementations {

    def boolLMS[T](c: Context)(block: c.Expr[T]): c.Expr[T] = {
      YYTransformer[c.type, T](c)(
        "dsl.lms.rep.BooleanLMS", //don't use version with Reify
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
        "dsl.lms.rep.BooleanLMS", //don't use version with Reify
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

    def primitiveDSL[T](c: Context)(block: c.Expr[T]): c.Expr[T] = {
      YYTransformer[c.type, T](c)(
        "dsl.lms.rep.PrimitiveDSL", //don't use version with Reify
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

    def vectorDSL[T](c: Context)(block: c.Expr[T]): c.Expr[T] = {
      YYTransformer[c.type, T](c)(
        "dsl.lms.rep.VectorDSL", //don't use version with Reify
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

    //    def deliteTestBaseDSL[T](c: Context)(block: c.Expr[T]): c.Expr[T] = {
    //      YYTransformer[c.type, T](c)(
    //        "ppl.delite.framework.ops.DeliteOpsExp", //don't use version with Reify
    //        new GenericTypeTransformer[c.type](c) {
    //          override val IRType = "Rep"
    //        },
    //        None, None,
    //        Map(
    //          "shallow" -> false,
    //          "virtualizeFunctions" -> false,
    //          "virtualizeVal" -> false,
    //          "debug" -> 4,
    //          //          "restrictLanguage" -> false,
    //          "ascriptionTransforming" -> false))(block)
    //    }
  }
}
