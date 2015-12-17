package dsl

import ch.epfl.yinyang._
import ch.epfl.yinyang.typetransformers._
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context
import scala.reflect.api.Trees

package object la {

  def la[T](block: => T): T = macro implementations.liftRep[T]
  def laDebug[T](block: => T): T = macro implementations.liftRepDebug[T]
  def boolS[T](block: => T): T = macro implementations.boolShallow[T]
  def boolD[T](block: => T): T = macro implementations.boolYY[T]
  //  def boolDLMS[T](block: => T): T = macro implementations.boolLMS[T]
  //  def typeOnly[T](block: => T): T = macro implementation.typeTranform[T]

  object implementations {
    def liftRep[T](c: Context)(block: c.Expr[T]): c.Expr[T] = {
      //      val x =
      YYTransformer[c.type, T](c)(
        "dsl.la.rep.VectorDSL",
        new GenericTypeTransformer[c.type](c) {
          override val IRType = "R"
        },
        None, None,
        Map(
          "direct" -> false,
          "virtualizeFunctions" -> true,
          "virtualizeValDef" -> true,
          "debug" -> 0,
          "restrictLanguage" -> false,
          "ascribeTerms" -> false),
        None)(block)
      //      println(c.universe.showCode(x.tree))
      //      x
    }

    def liftRepDebug[T](c: Context)(block: c.Expr[T]): c.Expr[T] =
      YYTransformer[c.type, T](c)(
        "dsl.la.rep.VectorDSL",
        new GenericTypeTransformer[c.type](c) {
          override val IRType = "R"
        },
        None, None,
        Map(
          "direct" -> false,
          "virtualizeFunctions" -> true,
          "virtualizeValDef" -> true,
          "debug" -> 3,
          "restrictLanguage" -> false,
          "ascribeTerms" -> false),
        None)(block)

    def boolShallow[T](c: Context)(block: c.Expr[T]): c.Expr[T] =
      YYTransformer[c.type, T](c)(
        "dsl.la.rep.BooleanDSL",
        new GenericTypeTransformer[c.type](c) {
          override val IRType = "Rep"
        },
        None, None,
        Map(
          "direct" -> true,
          "virtualizeFunctions" -> true,
          "virtualizeValDef" -> true,
          "debug" -> 3,
          "restrictLanguage" -> false,
          "ascribeTerms" -> false),
        None)(block)

    def boolYY[T](c: Context)(block: c.Expr[T]): c.Expr[T] = {
      YYTransformer[c.type, T](c)(
        "dsl.la.rep.BooleanDSLYY",
        new GenericTypeTransformer[c.type](c) {
          override val IRType = "R"
        },
        None, None,
        Map(
          "direct" -> false,
          "virtualizeFunctions" -> false,
          "virtualizeValDef" -> false,
          "debug" -> 3,
          "restrictLanguage" -> false,
          "ascribeTerms" -> false),
        None)(block)
    }
  }
}

//    object implementation {
//      def typeTranform[T](c: Context)(block: c.Expr[T]): c.Expr[T] = {
//        import c.universe._
//        class TTTransformer extends TypeTreeTransformation {
//          //        type Ctx = C [C <: Context](x: C)
//          val typeTransformer =
//            new GenericTypeTransformer[c.type](c) {
//              override val IRType = "R"
//            }
//          def transform(t: Tree) = new TypeTreeTransformer().transform(t)
//        }
//        c.Expr(new TTTransformer(c).transform(block.tree))
//      }
//    }
//}
