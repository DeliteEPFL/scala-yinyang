package dsl

import ch.epfl.yinyang._
import ch.epfl.yinyang.typetransformers._
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

package object la {

  def la[T](block: => T): T = macro implementations.liftRep[T]
  def laDebug[T](block: => T): T = macro implementations.liftRepDebug[T]
  def boolS[T](block: => T): T = macro implementations.boolShallow[T]
  def boolD[T](block: => T): T = macro implementations.boolYY[T]
  def intD[T](block: => T): T = macro implementations.intYY[T]
  def boolDLMS[T](block: => T): T = macro implementations.boolLMS[T]
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
          "debug" -> 0,
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
          "debug" -> 0,
          "restrictLanguage" -> false,
          "ascribeTerms" -> false),
        None)(block)

    def intYY[T](c: Context)(block: c.Expr[T]): c.Expr[T] = {
      YYTransformer[c.type, T](c)(
        "dsl.la.rep.IntDSL",
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

    //    def boolLMS[T](c: Context)(block: c.Expr[T]): c.Expr[T] = {
    //
    //      object O extends TypeTreeTransformation {
    //
    //        //private type Ctx = this.Ctx
    //        val typeTransformer = new GenericTypeTransformer[Ctx](c) {
    //          override val IRType = "Rep"
    //        }
    //        new TypeTreeTransformer().transform(block.tree.asInstanceOf[this.c.universe.Tree])
    //      }
    //      ???
    //    }

    def boolLMS[T](c: Context)(block: c.Expr[T]): c.Expr[T] = {
      YYTransformer[c.type, T](c)(
        "dsl.la.rep.BooleanDSLLMS",
        new GenericTypeTransformer[c.type](c) {
          override val IRType = "Rep"
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

  object implementation { //extends scala.reflect.api.Trees
    //abstract
    class TTTransformer[C <: Context, T](val c: C) extends TypeTreeTransformation {
      type Ctx = C
      import c.universe._ //Tree, TypTree, ValDef, showRaw...
      val debugLevel = 3
      val typeTransformer =
        new GenericTypeTransformer[c.type](c) { //[Ctx]
          override val IRType = "Rep"
        }
      def apply[T](block: c.Expr[T]): c.Expr[T] = {
        c.Expr(TypeTreeTransformer(block.tree))
      }
      //import typeTransformer._ //already imported in super
      //def transform(t: c.Tree) = typeTransformer transform (typeTransformer.other, inType)
    }

    def typeTranform[T](c: Context)(block: c.Expr[T]): c.Expr[T] = {
      //import c.universe._
      new TTTransformer[c.type, T](c)(block)
    }
  }
}
